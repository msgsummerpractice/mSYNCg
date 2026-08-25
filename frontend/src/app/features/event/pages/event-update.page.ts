import { Component } from '@angular/core';
import { EventUpdateContainer } from '../components/containers/event-update.container';
import { LanguageSwitcherContainer } from '../../../shared/components/containers/language-switcher.container';
import { ToolbarView } from '../../../shared/components/views/toolbar/toolbar.view';

@Component({
  selector: 'app-event-update-page',
  standalone: true,
  imports: [EventUpdateContainer, ToolbarView, LanguageSwitcherContainer],
  template: ` <app-event-update-container></app-event-update-container> `,
})
export class EventUpdatePage {}
