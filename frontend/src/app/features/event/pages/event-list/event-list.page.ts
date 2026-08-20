import { Component } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { EventListContainer } from '../../components/containers/event-list.container';
@Component({
  selector: 'app-event-list-page',
  standalone: true,
  imports: [EventListContainer, TranslatePipe],
  templateUrl: './event-list.page.html',
})
export class EventListPage {}
