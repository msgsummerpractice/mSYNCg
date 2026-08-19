import { Component, DestroyRef, effect, inject, input, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EventService } from '../../../../core/services/event.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-publish-event-container',
  template: '',
})
export class PublishEventContainer {
  private readonly eventService = inject(EventService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastService = inject(ToastService);

  readonly isLoading = signal(false);
  readonly eventId = input<number>(0);

  constructor() {
    effect(() => {
      const id = this.eventId();

      if (Number.isInteger(id) && id > 0) {
        this.onPublish(id);
      }
    });
  }

  private onPublish(id: number): void {
    this.isLoading.set(true);
    this.eventService
      .publishEvent(id)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe({
        error: (err) => this.toastService.showError('Failed to publish event. Please try again.'),
      });
  }
}
