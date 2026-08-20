import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';

import { TranslatePipe } from '@ngx-translate/core';

import { GenericCellView } from '../../../../shared/components/views/generic-cell.view';
import { ButtonContainer } from '../../../../shared/components/containers/button.container';

import { EventView } from '../../../../core/models/event.model';
import { TableColumn } from '../../../../core/models/table.column.model';

import { EventTypeEnum, EventStatusEnum } from '../../../../core/constants/event.constant';
import {
  EVENT_LOCATION_TRANSLATION_KEYS,
  EventLocation,
} from '../../../../core/constants/location.constant';
import { UserRole } from '../../../../core/constants/role.constant';
import { EVENT_MANAGEMENT_ROLES } from '../../../../core/constants/role.constant';

@Component({
  selector: 'app-event-list-view',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatTooltipModule,
    MatButtonModule,
    GenericCellView,
    ButtonContainer,
    TranslatePipe,
  ],
  templateUrl: './event-list.view.html',
})
export class EventListView {
  @Input() events: EventView[] = [];
  @Input() columns: TableColumn<EventView>[] = [];

  @Input() types: EventTypeEnum[] = [];
  @Input() statuses: EventStatusEnum[] = [];
  @Input() locations: EventLocation[] = [];

  @Input() selectedTypes: EventTypeEnum[] = [];
  @Input() selectedStatuses: EventStatusEnum[] = [];
  @Input() selectedLocations: EventLocation[] = [];

  @Input() nameQuery = '';
  @Input() startTimeQuery = '';

  @Input({ required: true }) totalItems!: number;

  @Input() pageIndex = 0;
  @Input() pageSize = 10;
  @Input() pageSizeOptions: number[] = [10, 20, 50];

  @Input() isLoading = false;

  @Input() userRole: UserRole | null = null;

  @Output() nameSearchChange = new EventEmitter<string>();
  @Output() startTimeChange = new EventEmitter<string>();

  @Output() typeChange = new EventEmitter<EventTypeEnum[]>();
  @Output() statusChange = new EventEmitter<EventStatusEnum[]>();
  @Output() locationChange = new EventEmitter<EventLocation[]>();

  @Output() resetFilters = new EventEmitter<void>();
  @Output() pageChange = new EventEmitter<PageEvent>();

  @Output() viewEvent = new EventEmitter<number>();
  @Output() editEvent = new EventEmitter<number>();
  @Output() publishEvent = new EventEmitter<number>();
  @Output() completeEvent = new EventEmitter<number>();

  get canManageEvents(): boolean {
    return this.userRole !== null && EVENT_MANAGEMENT_ROLES.includes(this.userRole);
  }

  get resolvedTotalItems(): number {
    return this.totalItems > 0 ? this.totalItems : this.events.length;
  }

  get displayedColumnKeys(): string[] {
    return [...this.columns.map((column) => column.key), 'actions'];
  }

  getLocationLabelKey(location: EventLocation): string {
    return EVENT_LOCATION_TRANSLATION_KEYS[location] ?? location;
  }
}
