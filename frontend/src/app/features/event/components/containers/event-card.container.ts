import { Component, Input } from '@angular/core';

import { EventCardView } from '../views/event-card.view';
import { Event as AppEvent } from '../../../../core/models/event.model';
import { MOCK_EVENT } from '../../../../core/mocks/event.mock';

@Component({
  selector: 'app-event-card-container',
  standalone: true,
  imports: [EventCardView],
  template: `<app-event-card-view [eventData]="event"></app-event-card-view>`,
})
export class EventCardContainer {
  //   @Input({ required: true }) event!: AppEvent;
  protected readonly event: AppEvent = MOCK_EVENT;
}
