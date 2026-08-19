import { Component, Input, computed, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';

import { Event as AppEvent } from '../../../../../core/models/event.model';

@Component({
  selector: 'app-event-card-view',
  standalone: true,
  imports: [
    DatePipe,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatDividerModule,
    TranslatePipe,
  ],
  templateUrl: './event-card.view.html',
})
export class EventCardView {
  readonly event = signal<AppEvent | null>(null);

  @Input({ required: true }) set eventData(value: AppEvent) {
    this.event.set(value);
  }

  readonly posterSrc = computed(() => {
    const image = this.event()?.image ?? '';
    return /^(data:|https?:\/\/|\/)/.test(image) ? image : `data:image/*;base64,${image}`;
  });
}
