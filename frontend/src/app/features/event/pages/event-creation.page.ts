import { Component } from '@angular/core';
import { EventCreationContainer } from '../components/containers/event-creation.container';
import { LanguageSwitcherContainer } from '../../../shared/components/containers/language-switcher.container';
import { ToolbarView } from '../../../shared/components/views/toolbar/toolbar.view';

@Component({
  selector: 'app-event-creation-page',
  standalone: true,
  imports: [EventCreationContainer,ToolbarView, LanguageSwitcherContainer],
  template: `
    <app-toolbar-view [showNavigation]="true" [showUserIcon]="true">
      <app-language-switcher></app-language-switcher>
    </app-toolbar-view>
    <app-event-creation-container></app-event-creation-container>
  `,
})
export default class EventCreationPage {}