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

import { Event } from '../../../../core/models/event.model';
import { TableColumn } from '../../../../core/models/table.column.model';

import { EventStatus } from '../../../../core/constants/event-status.constant';
import { EventType } from '../../../../core/constants/event-type.constant';
import { Location } from '../../../../core/constants/location.constant';
import { UserRole } from '../../../../core/constants/role.constant';

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
  @Input() events: Event[] = [];
  @Input() columns: TableColumn<Event>[] = [];

  @Input() types: EventType[] = [];
  @Input() statuses: EventStatus[] = [];
  @Input() locations: Location[] = [];

  @Input() selectedTypes: EventType[] = [];
  @Input() selectedStatuses: EventStatus[] = [];
  @Input() selectedLocations: Location[] = [];

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

  @Output() typeChange = new EventEmitter<EventType[]>();
  @Output() statusChange = new EventEmitter<EventStatus[]>();
  @Output() locationChange = new EventEmitter<Location[]>();

  @Output() resetFilters = new EventEmitter<void>();
  @Output() pageChange = new EventEmitter<PageEvent>();

  @Output() viewEvent = new EventEmitter<number>();
  @Output() editEvent = new EventEmitter<number>();
  @Output() publishEvent = new EventEmitter<number>();
  @Output() completeEvent = new EventEmitter<number>();

  get canManageEvents(): boolean {
    return (
      this.userRole === UserRole.ADMIN ||
      this.userRole === UserRole.MARKETING_ORGANIZER ||
      this.userRole === UserRole.HR_USER
    );
  }

  get resolvedTotalItems(): number {
    return this.totalItems > 0 ? this.totalItems : this.events.length;
  }

  get displayedColumnKeys(): string[] {
    return [...this.columns.map((column) => column.key), 'actions'];
  }
}
