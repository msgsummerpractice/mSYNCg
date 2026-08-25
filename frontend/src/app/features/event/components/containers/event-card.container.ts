import { isPlatformBrowser } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  computed,
  DestroyRef,
  effect,
  inject,
  input,
  output,
  PLATFORM_ID,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TranslateService } from '@ngx-translate/core';
import { finalize } from 'rxjs';

import { EventCardView } from '../views/event-card/event-card.view';
import { UserRole } from '../../../../core/constants/role.constant';
import {
  Event as AppEvent,
  EventParticipationStatus,
  EventStatusEnum,
} from '../../../../core/models/event.model';
import { AuthService } from '../../../../core/services/auth.service';
import { EventService } from '../../../../core/services/event.service';
import { Router } from '@angular/router';
import { ToastService } from '../../../../core/services/toast.service';
@Component({
  selector: 'app-event-card-container',
  standalone: true,
  imports: [EventCardView],
  template: `
    @if (event(); as eventData) {
      <app-event-card-view
        [eventData]="eventData"
        [canGenerateCodes]="canGenerateCodes()"
        [qrCode]="qrCode()"
        [accessCode]="accessCode()"
        [isGeneratingCodes]="isGeneratingCodes()"
        [isRegistered]="isRegistered()"
        [isCheckedIn]="isCheckedIn()"
        [canCheckIn]="canCheckIn()"
        (generateCodes)="onGenerateCodes()"
        (checkIn)="onCheckIn()"
        (navigate)="navigate($event)"
        (close)="close.emit()"
      ></app-event-card-view>
    }
  `,
})
export class EventCardContainer {
  private readonly destroyRef = inject(DestroyRef);
  private readonly eventService = inject(EventService);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);
  private readonly translateService = inject(TranslateService);
  private readonly platformId = inject(PLATFORM_ID);
  private router = inject(Router);

  readonly event = signal<AppEvent | null>(null);
  readonly isLoading = signal(false);
  readonly qrCode = signal<string | null>(null);
  readonly accessCode = signal<string | null>(null);
  readonly isGeneratingCodes = signal(false);

  readonly eventId = input<number>(0);
  readonly participationStatus = input<EventParticipationStatus | null>(null);
  readonly close = output<void>();
  readonly checkIn = output<number>();

  readonly isRegistered = computed(() => this.participationStatus() !== null);

  readonly isCheckedIn = computed(
    () => this.participationStatus() === EventParticipationStatus.CHECKED_IN
  );

  readonly canCheckIn = computed(() => {
    const event = this.event();
    const endTime = event?.endTime ? new Date(event.endTime).getTime() : NaN;

    return (
      this.isRegistered() &&
      !this.isCheckedIn() &&
      event?.status === EventStatusEnum.PUBLISHED &&
      Number.isFinite(endTime) &&
      endTime >= Date.now()
    );
  });

  onCheckIn(): void {
    this.checkIn.emit(this.eventId());
  }

  readonly canGenerateCodes = computed(() => {
    const event = this.event();

    return (
      isPlatformBrowser(this.platformId) &&
      event?.status === EventStatusEnum.PUBLISHED &&
      !this.authService.hasRole(UserRole.PARTICIPANT)
    );
  });

  constructor() {
    effect(() => {
      const id = this.eventId();

      if (Number.isInteger(id) && id > 0) {
        this.loadEvent(id);
      }
    });
  }

  onGenerateCodes(): void {
    const id = this.eventId();

    if (!Number.isInteger(id) || id <= 0 || this.isGeneratingCodes()) {
      return;
    }

    this.isGeneratingCodes.set(true);

    this.eventService
      .generateEventCodes(id)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isGeneratingCodes.set(false))
      )
      .subscribe({
        next: (codes) => {
          this.qrCode.set(codes.qrCode);
          this.accessCode.set(codes.code);
        },
        error: () =>
          this.toastService.showError(
            this.translateService.instant('EVENT.CARD.GENERATE_CODES_ERROR')
          ),
      });
  }

  private loadEvent(id: number): void {
    this.isLoading.set(true);

    this.eventService
      .getEventById(id)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe({
        next: (event) => {
          this.event.set(event);
          this.qrCode.set(event?.qrCode ?? null);
          this.accessCode.set(event?.code ?? null);
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 401 || error.status === 403) {
            this.authService.logout();
            this.router.navigate(['/login']);
            return;
          }

          this.toastService.showError(this.translateService.instant('EVENT_LIST.LOAD_ERROR'));
        },
      });
  }

  navigate(route: string): void {
    this.router.navigate([route]);
  }
}
