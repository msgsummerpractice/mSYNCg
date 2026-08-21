import {
  Component,
  DestroyRef,
  effect,
  inject,
  input,
  Injector,
  OnInit,
  output,
  runInInjectionContext,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EventService } from '../../../../core/services/event.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { ToastService } from '../../../../core/services/toast.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-publish-event-container',
  standalone: true,
  template: '',
})
export class PublishEventContainer implements OnInit {
  private readonly eventService = inject(EventService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastService = inject(ToastService);
  private readonly translateService = inject(TranslateService);
  private readonly injector = inject(Injector);

  readonly isLoading = signal(false);
  readonly eventId = input<number>(0);

  readonly published = output<number>();
  readonly finished = output<void>();

  ngOnInit(): void {
    runInInjectionContext(this.injector, () => {
      effect(() => {
        const id = this.eventId();

        if (Number.isInteger(id) && id > 0) {
          this.onPublish(id);
        }
      });
    });
  }

  private onPublish(id: number): void {
    this.isLoading.set(true);
    this.eventService
      .publishEvent(id)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => {
          this.isLoading.set(false);
          this.finished.emit();
        })
      )
      .subscribe({
        next: () => {
          this.toastService.showSuccess(
            this.translateService.instant('EVENT_LIST.PUBLISH_SUCCESS')
          );
          this.published.emit(id);
        },
        error: () =>
          this.toastService.showError(this.translateService.instant('EVENT_LIST.PUBLISH_ERROR')),
      });
  }
}
