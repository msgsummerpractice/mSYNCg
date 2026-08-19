import { isPlatformBrowser } from '@angular/common';
import { Component, DestroyRef, effect, inject, input, PLATFORM_ID, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';

import { EventCardView } from '../views/event-card/event-card.view';
import { Event as AppEvent } from '../../../../core/models/event.model';
import { EventService } from '../../../../core/services/event.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-event-card-container',
  standalone: true,
  imports: [EventCardView],
  template: `
    @if (event(); as eventData) {
      <app-event-card-view [eventData]="eventData"></app-event-card-view>
    }
  `,
})
export class EventCardContainer {
  private readonly destroyRef = inject(DestroyRef);
  private readonly eventService = inject(EventService);
  private readonly platformId = inject(PLATFORM_ID);
  //for testing
  private readonly route = inject(ActivatedRoute);

  readonly event = signal<AppEvent | null>(null);
  readonly isLoading = signal(false);

  readonly eventId = input<number>(0);

  constructor() {
    effect(() => {
      const id = this.eventId();

      if (Number.isInteger(id) && id > 0) {
        this.loadEvent(id);
      }
    });

    //for testing
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (Number.isInteger(id) && id > 0) {
      this.loadEvent(id);
    }
  }

  private loadEvent(id: number): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

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
