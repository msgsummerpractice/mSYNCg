import { Component, computed, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';

import { Event as AppEvent } from '../../../../../core/models/event.model';

// Only jpg/jpeg and png are accepted; these are their fixed base64 prefixes
function toImageSrc(value: string): string {
  // JPEG base64 starts with "/9j/", so URL detection must run before the leading-slash check
  if (/^(data:|https?:\/\/)/.test(value)) {
    return value;
  }

  if (value.startsWith('iVBORw0KGgo')) {
    return `data:image/png;base64,${value}`;
  }

  if (value.startsWith('/9j/')) {
    return `data:image/jpeg;base64,${value}`;
  }

  return value;
}

@Component({
  selector: 'app-event-card-view',
  standalone: true,
  imports: [
    DatePipe,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatButton,
    MatIconButton,
    MatDividerModule,
    MatProgressSpinnerModule,
    TranslatePipe,
  ],
  templateUrl: './event-card.view.html',
})
export class EventCardView {
  readonly eventData = input.required<AppEvent>();
  readonly canGenerateCodes = input(false);
  readonly qrCode = input<string | null>(null);
  readonly accessCode = input<string | null>(null);
  readonly isGeneratingCodes = input(false);

  readonly close = output<void>();
  readonly generateCodes = output<void>();

  readonly posterSrc = computed(() => toImageSrc(this.eventData().image ?? ''));

  readonly qrCodeSrc = computed(() => {
    const qrCode = this.qrCode();

    return qrCode ? toImageSrc(qrCode) : null;
  });

  readonly hasCodes = computed(() => !!this.qrCodeSrc() && !!this.accessCode());
}
