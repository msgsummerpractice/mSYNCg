import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { TranslatePipe } from '@ngx-translate/core';

import { GenericCellView } from '../../../../shared/components/views/generic-cell.view';
import { ButtonContainer } from '../../../../shared/components/containers/button.container';

import { Event } from '../../../../core/models/event.model';
import { TableColumn } from '../../../../core/models/table.column.model';

import { EventStatus } from '../../../../core/constants/event-status.constant';
import { EventType } from '../../../../core/constants/event-type.constant';
import { Location } from '../../../../core/constants/location.constant';

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

  @Output() nameSearchChange = new EventEmitter<string>();
  @Output() startTimeChange = new EventEmitter<string>();

  @Output() typeChange = new EventEmitter<EventType[]>();
  @Output() statusChange = new EventEmitter<EventStatus[]>();
  @Output() locationChange = new EventEmitter<Location[]>();

  @Output() resetFilters = new EventEmitter<void>();
  @Output() pageChange = new EventEmitter<PageEvent>();

  @Output() viewEvent = new EventEmitter<number>();

  get resolvedTotalItems(): number {
    return this.totalItems > 0 ? this.totalItems : this.events.length;
  }

  get displayedColumnKeys(): string[] {
    return [...this.columns.map((column) => column.key), 'actions'];
  }
}
