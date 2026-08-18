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
      <div
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-[3vh] backdrop-blur-[2px]"
      >
        <mat-card
          appearance="raised"
          class="flex h-[94vh] w-[94vw] !flex-col overflow-hidden rounded-2xl shadow-2xl lg:!flex-row"
        >
          <div
            class="relative h-56 w-full shrink-0 overflow-hidden bg-brand-muted sm:h-72 lg:h-full lg:w-2/5 lg:max-w-lg"
          >
            <img
              [src]="posterSrc()"
              alt=""
              aria-hidden="true"
              class="absolute inset-0 h-full w-full scale-110 object-cover blur-xl lg:hidden"
            />
            <img
              [src]="posterSrc()"
              [alt]="ev.name"
              class="relative h-full w-full object-contain lg:object-cover"
            />
          </div>

          <div class="min-h-0 min-w-0 flex-1 overflow-y-auto pt-4">
            <mat-card-header class="!block !p-6 !pb-2">
              <mat-card-title class="font-ui text-2xl  font-semibold text-brand-on-surface">
                {{ ev.name }}
              </mat-card-title>
              <mat-card-subtitle class="mt-1 !flex items-center gap-2 text-brand-on-muted">
                <mat-icon class="!h-5 !w-5 !text-xl !leading-5">place</mat-icon>
                <span class="font-base text-sm">{{ ev.location }}</span>
              </mat-card-subtitle>
            </mat-card-header>

            <mat-card-content class="!px-6 !pb-6">
              <div class="mb-4 flex flex-wrap gap-2">
                <mat-chip-set>
                  <mat-chip class="chip-static" highlighted>{{ ev.type }}</mat-chip>
                  <mat-chip class="chip-static">{{ ev.status }}</mat-chip>
                  <mat-chip
                    class="chip-static"
                    [class.chip-success]="ev.foodProvided"
                    [class.chip-danger]="!ev.foodProvided"
                  >
                    <mat-icon matChipAvatar>{{
                      ev.foodProvided ? 'restaurant' : 'no_meals'
                    }}</mat-icon>
                    {{
                      (ev.foodProvided
                        ? 'EVENT.CARD.FOOD_PROVIDED'
                        : 'EVENT.CARD.FOOD_NOT_PROVIDED'
                      ) | translate
                    }}
                  </mat-chip>
                </mat-chip-set>
              </div>

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

              <mat-divider class="!my-5"></mat-divider>
              <p
                class="font-base text-base leading-relaxed whitespace-pre-line text-brand-on-surface"
              >
                {{ ev.description }}
              </p>
            </mat-card-content>
          </div>
        </mat-card>
      </div>
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
    return /^(data:|https?:\/\/|\/)/.test(image) ? image : `data:image/*;base64,${image}`;
  });
}
