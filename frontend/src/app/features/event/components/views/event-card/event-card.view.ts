import { Component, computed, EventEmitter, Input, input, Output, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';
import { ButtonContainer } from '../../../../../shared/components/containers/button.container';
import { Event as AppEvent } from '../../../../../core/models/event.model';

interface NavItem {
  label: string;
  route: string;
}

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
    ButtonContainer,
  ],
  templateUrl: './event-card.view.html',
})
export class EventCardView {
  readonly eventData = input.required<AppEvent>();
  readonly close = output<void>();
  @Output() navigate = new EventEmitter<string>();

  @Input() navItems: NavItem[] = [{ label: 'Register', route: '/register' }];

  readonly route = computed(() => '/events/' + this.eventData().id + '/register');

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

  handleEventClick(): void {
    this.navigate.emit(this.route());
  }
}
