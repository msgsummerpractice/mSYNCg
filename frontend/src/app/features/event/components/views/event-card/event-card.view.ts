import { Component, computed, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconButton } from '@angular/material/button';
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
    MatIconButton,
    MatDividerModule,
    TranslatePipe,
  ],
  templateUrl: './event-card.view.html',
})
export class EventCardView {
  readonly eventData = input.required<AppEvent>();
  readonly close = output<void>();

  readonly posterSrc = computed(() => {
    const image = this.eventData().image ?? '';

    // JPEG base64 starts with "/9j/", so URL detection must run before the leading-slash check
    if (/^(data:|https?:\/\/)/.test(image)) {
      return image;
    }

    // Only jpg/jpeg and png are accepted; these are their fixed base64 prefixes
    if (image.startsWith('iVBORw0KGgo')) {
      return `data:image/png;base64,${image}`;
    }
    if (image.startsWith('/9j/')) {
      return `data:image/jpeg;base64,${image}`;
    }

    return image;
  });
}
