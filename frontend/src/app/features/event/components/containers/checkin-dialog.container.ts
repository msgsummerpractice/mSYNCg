import { Component, signal, input, output, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { CheckInScannerView } from '../views/checkin-scanner/checkin-scanner.view';
import { CheckInService } from '../../../../core/services/checkin.service';
import { ToastService } from '../../../../core/services/toast.service';
import { TranslateService } from '@ngx-translate/core';

export interface CheckInDialogData {
  eventId: number;
}

@Component({
  selector: 'app-checkin-dialog-container',
  standalone: true,
  imports: [CheckInScannerView, MatDialogModule],
  template: `<app-checkin-scanner-view
    [isLoading]="isLoading()"
    [errorMessage]="errorMessage()"
    [successMessage]="successMessage()"
    (codeScanned)="onCodeScanned($event)"
    (codeEntered)="onCodeEntered($event)"
    (close)="onClose()"
  ></app-checkin-scanner-view>`,
})
export class CheckInDialogContainer {
  private readonly checkInService = inject(CheckInService);
  private readonly toastService = inject(ToastService);
  private readonly dialogRef = inject(MatDialogRef<CheckInDialogContainer>);
  private readonly translateService = inject(TranslateService);
  private readonly dialogData = inject<CheckInDialogData>(MAT_DIALOG_DATA);

  isLoading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  onCodeScanned(code: string): void {
    this.processCheckIn(code);
  }

  onCodeEntered(code: string): void {
    this.processCheckIn(code);
  }

  onClose(): void {
    this.dialogRef.close();
  }

  private processCheckIn(code: string): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.checkInService.checkIn(code).subscribe({
      next: (response) => {
        this.isLoading.set(false);

        const successMsg = this.translateService.instant('CHECKIN.SUCCESS.MESSAGE');
        this.successMessage.set(successMsg);
        this.toastService.showSuccess(successMsg);

        setTimeout(() => {
          this.dialogRef.close(true);
        }, 2000);
      },
      error: (error) => {
        this.isLoading.set(false);

        let errorMsg = this.translateService.instant('CHECKIN.ERROR.DEFAULT');

        if (error.status === 400) {
          errorMsg = this.translateService.instant('CHECKIN.ERROR.INVALID_CODE');
        } else if (error.status === 403) {
          errorMsg = this.translateService.instant('CHECKIN.ERROR.NOT_REGISTERED');
        } else if (error.status === 409) {
          const backendMsg = error.error?.message;
          if (backendMsg?.includes('already')) {
            errorMsg = this.translateService.instant('CHECKIN.ERROR.ALREADY_CHECKED_IN');
          } else if (backendMsg?.includes('completed')) {
            errorMsg = this.translateService.instant('CHECKIN.ERROR.EVENT_COMPLETED');
          } else if (backendMsg?.includes('past')) {
            errorMsg = this.translateService.instant('CHECKIN.ERROR.EVENT_PAST_END');
          } else {
            errorMsg = backendMsg || this.translateService.instant('CHECKIN.ERROR.DEFAULT');
          }
        } else if (error.error?.message) {
          errorMsg = error.error.message;
        }

        this.errorMessage.set(errorMsg);
        this.toastService.showError(errorMsg);
      },
    });
  }
}
