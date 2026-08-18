import { Component } from '@angular/core';
import { EventListContainer } from '../../../features/event/components/containers/event-list.container';

@Component({
  selector: 'app-event-list-page',
  standalone: true,
  imports: [EventListContainer],
  templateUrl: './event-list.page.html',
})
export class EventListPage {}
