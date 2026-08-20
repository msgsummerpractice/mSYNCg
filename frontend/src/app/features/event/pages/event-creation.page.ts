import { Component } from '@angular/core';
import { EventCreationContainer } from '../components/containers/event-creation.container';
import { LanguageSwitcherContainer } from '../../../shared/components/containers/language-switcher.container';

@Component({
  selector: 'app-event-creation-page',
  standalone: true,
  imports: [EventCreationContainer, LanguageSwitcherContainer],
  template: ` <app-event-creation-container></app-event-creation-container> `,
})
export class EventCreationPage {}
