import { isPlatformBrowser } from '@angular/common';
import {
  Component,
  DestroyRef,
  effect,
  inject,
  input,
  output,
  PLATFORM_ID,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';

import { EventCardView } from '../views/event-card/event-card.view';
import { Event as AppEvent } from '../../../../core/models/event.model';
import { EventService } from '../../../../core/services/event.service';

@Component({
  selector: 'app-event-card-container',
  standalone: true,
  imports: [EventCardView],
  template: `
    @if (event(); as eventData) {
      <app-event-card-view [eventData]="eventData" (close)="close.emit()"></app-event-card-view>
    }
  `,
})
export class EventCardContainer {
  private readonly destroyRef = inject(DestroyRef);
  private readonly eventService = inject(EventService);
  private readonly platformId = inject(PLATFORM_ID);

  readonly event = signal<AppEvent | null>(null);
  readonly isLoading = signal(false);

  readonly eventId = input<number>(0);
  readonly close = output<void>();

  constructor() {
    effect(() => {
      const id = this.eventId();

      if (Number.isInteger(id) && id > 0) {
        this.loadEvent(id);
      }
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
      .subscribe((event) => this.event.set(event));
  }
}
