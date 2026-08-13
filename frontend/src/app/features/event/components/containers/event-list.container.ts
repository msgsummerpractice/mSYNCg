import { Component } from '@angular/core';
import { EventListView } from '../views/event-list.view';
@Component({
  selector: 'app-event-list-container',
  imports: [EventListView],
  template: `<app-event-list-view></app-event-list-view>`,
})
export class EventListContainer {}
