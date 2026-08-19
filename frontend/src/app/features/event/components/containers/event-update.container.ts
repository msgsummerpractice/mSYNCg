import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { EventService } from '../../../../core/services/event.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Event as EventDetails } from '../../../../core/models/event.model';
import { EventCreationView } from '../views/event-creation/event-creation.view';
import { EventCreationContainer } from './event-creation.container';

@Component({
  selector: 'app-event-update-container',
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
    />
  `,
})
export class EventUpdateContainer extends EventCreationContainer {
  private readonly route = inject(ActivatedRoute);
  private readonly events = inject(EventService);
  private readonly toast = inject(ToastService);
  private readonly translation = inject(TranslateService);

  constructor() {
    super();

    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isNaN(id)) {
      this.eventId = id;
      this.loadEvent(id);
    }
  }

  private loadEvent(id: number): void {
    this.isLoading.set(true);

    this.events
      .getEvent(id)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: (event) => this.fillForm(event),
        error: () =>
          this.toast.showError(this.translation.instant('REGISTER.EVENT.MESSAGES.ERROR.SAVE')),
      });
  }

  private fillForm(event: EventDetails): void {
    this.eventFormGroup.controls.type.setValue(event.type);

    const start = event.startTime;
    const end = event.endTime;
    const registrationStart = event.registrationStart;
    const registrationEnd = event.registrationEnd;

    this.eventFormGroup.patchValue({
      title: event.name,
      description: event.description,
      startDate: start,
      startTime: start,
      endDate: end,
      endTime: end,
      registrationStartDate: registrationStart,
      registrationStartTime: registrationStart,
      registrationEndDate: registrationEnd,
      registrationEndTime: registrationEnd,
      location: event.location,
      isFoodProvided: event.foodProvided,
    });

    if (event.image) {
      this.posterBase64 = event.image;
      this.posterName.set(event.name);
    }
  }
}
