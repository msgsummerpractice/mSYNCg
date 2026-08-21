import { Component } from '@angular/core';
import { EventCreationView } from '../views/event-creation/event-creation.view';
import { EventDraftContainer } from './event-draft.container';

@Component({
  selector: 'app-event-creation-container',
  standalone: true,
  imports: [EventCreationView],
  template: `
    <app-event-creation-view
      [formGroup]="eventFormGroup"
      [isLoading]="isLoading()"
      [selectedType]="selectedType()"
      [posterName]="posterName()"
      (posterSelected)="handlePosterSelected($event)"
      (submitEvent)="handleEventSubmit()"
      (invalidSubmit)="handleInvalidForm()"
      (cancelEvent)="handleCancel()"
    />
  `,
})
export class EventCreationContainer extends EventDraftContainer {}
