import {
  Component,
  signal,
  output,
  input,
  computed,
  ViewChild,
  ElementRef,
  OnInit,
  OnDestroy,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { TranslatePipe } from '@ngx-translate/core';
import jsQR from 'jsqr';

@Component({
  selector: 'app-checkin-scanner-view',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatIconModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatTabsModule,
    TranslatePipe,
  ],
  templateUrl: './checkin-scanner.view.html',
})
export class CheckInScannerView implements OnInit, OnDestroy {
  @ViewChild('videoElement', { static: false }) videoElement!: ElementRef<HTMLVideoElement>;
  @ViewChild('canvasElement', { static: false }) canvasElement!: ElementRef<HTMLCanvasElement>;

  readonly isLoading = input(false);
  readonly errorMessage = input<string | null>(null);
  readonly successMessage = input<string | null>(null);

  readonly codeScanned = output<string>();
  readonly codeEntered = output<string>();
  readonly close = output<void>();

  selectedTab = signal<'camera' | 'manual'>(
    typeof navigator !== 'undefined' && navigator.mediaDevices ? 'camera' : 'manual'
  );
  manualCode = signal<string>('');
  cameraActive = signal<boolean>(false);
  cameraError = signal<string | null>(null);

  canShowCamera = computed(
    () => typeof navigator !== 'undefined' && !!navigator.mediaDevices?.getUserMedia
  );

  isTabCamera = computed(() => this.selectedTab() === 'camera');
  isTabManual = computed(() => this.selectedTab() === 'manual');

  async onTabChange(tab: 'camera' | 'manual'): Promise<void> {
    this.selectedTab.set(tab);
    this.manualCode.set('');
    this.cameraError.set(null);

    if (tab === 'camera' && this.canShowCamera()) {
      await this.startCamera();
    } else if (tab === 'manual') {
      this.stopCamera();
    }
  }

  async startCamera(): Promise<void> {
    this.cameraError.set(null);
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: 'environment' },
      });

      if (this.videoElement && this.videoElement.nativeElement) {
        const videoEl = this.videoElement.nativeElement;
        videoEl.srcObject = stream;
        this.cameraActive.set(true);

        await this.waitForVideoMetadata(videoEl);
        this.scanQRCode();
      }
    } catch (error) {
      const errorMsg = error instanceof Error ? error.message : String(error);
      this.cameraError.set(`CAMERA_ERROR: ${errorMsg}`);
      this.selectedTab.set('manual');
    }
  }

  private waitForVideoMetadata(videoEl: HTMLVideoElement): Promise<void> {
    return new Promise((resolve) => {
      if (videoEl.readyState >= 1) {
        resolve();
      } else {
        const onLoadedMetadata = () => {
          videoEl.removeEventListener('loadedmetadata', onLoadedMetadata);
          resolve();
        };
        videoEl.addEventListener('loadedmetadata', onLoadedMetadata);
      }
    });
  }

  stopCamera(): void {
    if (this.videoElement && this.videoElement.nativeElement) {
      const stream = this.videoElement.nativeElement.srcObject as MediaStream;
      if (stream) {
        stream.getTracks().forEach((track) => track.stop());
      }
    }
    this.cameraActive.set(false);
  }

  private scanQRCode(): void {
    if (!this.cameraActive() || !this.canShowCamera()) {
      return;
    }

    const video = this.videoElement?.nativeElement;
    const canvas = this.canvasElement?.nativeElement;

    if (!video || !canvas) {
      return;
    }

    const ctx = canvas.getContext('2d');
    if (!ctx) {
      return;
    }

    if (video.videoWidth === 0 || video.videoHeight === 0) {
      requestAnimationFrame(() => this.scanQRCode());
      return;
    }

    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;

    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

    const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
    const decoded = this.decodeQR(imageData);

    if (decoded) {
      this.stopCamera();
      this.codeScanned.emit(decoded);
    } else {
      requestAnimationFrame(() => this.scanQRCode());
    }
  }

  private decodeQR(imageData: ImageData): string | null {
    try {
      const code = jsQR(imageData.data, imageData.width, imageData.height, {
        inversionAttempts: 'dontInvert',
      });
      return code ? code.data : null;
    } catch (error) {
      return null;
    }
  }

  onManualSubmit(): void {
    const code = this.manualCode().trim();
    if (code) {
      this.codeEntered.emit(code);
    }
  }

  onCancel(): void {
    this.stopCamera();
    this.close.emit();
  }

  ngOnInit(): void {
    if (this.canShowCamera() && this.selectedTab() === 'camera') {
      this.startCamera();
    }
  }

  ngOnDestroy(): void {
    this.stopCamera();
  }
}
