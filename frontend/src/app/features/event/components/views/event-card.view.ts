import { Component, Input, computed, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';

import { Event as AppEvent } from '../../../../core/models/event.model';

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
  template: `
    @if (event(); as ev) {
      <mat-card appearance="outlined" class="w-full max-w-2xl overflow-hidden rounded-2xl">
        <img [src]="posterSrc()" [alt]="ev.name" class="h-64 w-full bg-brand-muted object-cover" />

        <mat-card-header class="!block !p-6 !pb-2">
          <mat-card-title class="font-ui text-2xl font-semibold text-brand-on-surface">
            {{ ev.name }}
          </mat-card-title>
          <mat-card-subtitle class="mt-1 flex items-center gap-1 text-sm text-brand-on-muted">
            <mat-icon class="!h-4 !w-4 !text-base">place</mat-icon>
            {{ ev.location }}
          </mat-card-subtitle>
        </mat-card-header>

        <mat-card-content class="!px-6 !pb-2">
          <div class="mb-4 flex flex-wrap gap-2">
            <mat-chip-set>
              <mat-chip highlighted>{{ ev.type }}</mat-chip>
              <mat-chip>{{ ev.status }}</mat-chip>
              <mat-chip>
                <mat-icon matChipAvatar>{{ ev.foodProvided ? 'restaurant' : 'no_meals' }}</mat-icon>
                {{
                  (ev.foodProvided ? 'EVENT.CARD.FOOD_PROVIDED' : 'EVENT.CARD.FOOD_NOT_PROVIDED')
                    | translate
                }}
              </mat-chip>
            </mat-chip-set>
          </div>

          <p class="font-base text-base leading-relaxed whitespace-pre-line text-brand-on-surface">
            {{ ev.description }}
          </p>

          <mat-divider class="!my-5"></mat-divider>

          <dl class="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div>
              <dt class="text-xs font-medium tracking-wide text-brand-on-muted uppercase">
                {{ 'EVENT.CARD.START_DATE' | translate }}
              </dt>
              <dd class="text-sm text-brand-on-surface">
                {{ ev.startDate | date: 'medium' }}
              </dd>
            </div>
            <div>
              <dt class="text-xs font-medium tracking-wide text-brand-on-muted uppercase">
                {{ 'EVENT.CARD.END_DATE' | translate }}
              </dt>
              <dd class="text-sm text-brand-on-surface">
                {{ ev.endDate | date: 'medium' }}
              </dd>
            </div>
            <div>
              <dt class="text-xs font-medium tracking-wide text-brand-on-muted uppercase">
                {{ 'EVENT.CARD.REGISTRATION_START' | translate }}
              </dt>
              <dd class="text-sm text-brand-on-surface">
                {{ ev.registrationStart | date: 'medium' }}
              </dd>
            </div>
            <div>
              <dt class="text-xs font-medium tracking-wide text-brand-on-muted uppercase">
                {{ 'EVENT.CARD.REGISTRATION_END' | translate }}
              </dt>
              <dd class="text-sm text-brand-on-surface">
                {{ ev.registrationEnd | date: 'medium' }}
              </dd>
            </div>
          </dl>
        </mat-card-content>
      </mat-card>
    }
  `,
})
export class EventCardView {
  readonly event = signal<AppEvent | null>(null);

  @Input({ required: true }) set eventData(value: AppEvent) {
    this.event.set(value);
  }

  readonly posterSrc = computed(() => {
    const image = this.event()?.imageBase64 ?? '';
    return image.startsWith('data:') ? image : `data:image/*;base64,${image}`;
  });
}
