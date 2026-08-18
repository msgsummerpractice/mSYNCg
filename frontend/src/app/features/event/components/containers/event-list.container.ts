import { Component, computed, signal, inject, PLATFORM_ID, DestroyRef } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { PageEvent } from '@angular/material/paginator';
import { debounceTime, finalize, switchMap, tap } from 'rxjs/operators';
import { Router } from '@angular/router';

import { EventListView } from '../views/event-list.view';
import { EventService } from '../../../../core/services/event.service';
import { AuthService } from '../../../../core/services/auth.service';
import { Event } from '../../../../core/models/event.model';
import { EventFilterParams } from '../../../../core/models/event-filter.model';
import { TableColumn } from '../../../../core/models/table.column.model';

import { EventStatus } from '../../../../core/constants/event-status.constant';
import { EventType } from '../../../../core/constants/event-type.constant';
import { LocationEnum } from '../../../../core/models/location.model';
import { UserRole } from '../../../../core/constants/role.constant';
import { MOCK_EVENTS } from '../../../../core/constants/mocks/event.mocks';

@Component({
  selector: 'app-event-list-container',
  standalone: true,
  imports: [EventListView],
  templateUrl: './event-list.container.html',
})
export class EventListContainer {
  private readonly eventService = inject(EventService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  tableColumns: TableColumn<Event>[] = [
    {
      key: 'name',
      label: 'EVENT_LIST.TABLE.NAME',
      type: 'text',
    },
    {
      key: 'startTime',
      label: 'EVENT_LIST.TABLE.DATE',
      type: 'text',
      valueGetter: (event) =>
        new Date(event.startTime).toLocaleString('ro-RO', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit',
        }),
    },
    {
      key: 'status',
      label: 'EVENT_LIST.TABLE.STATUS',
      type: 'text',
    },
    {
      key: 'type',
      label: 'EVENT_LIST.TABLE.TYPE',
      type: 'text',
    },
    {
      key: 'location',
      label: 'EVENT_LIST.TABLE.LOCATION',
      type: 'text',
    },
  ];

  types = signal<EventType[]>([EventType.INTERNAL, EventType.EXTERNAL, EventType.LOCAL]);

  statuses = signal<EventStatus[]>([
    EventStatus.DRAFT,
    EventStatus.PUBLISHED,
    EventStatus.COMPLETED,
  ]);

  locations = signal<LocationEnum[]>([LocationEnum.CLUJ_NAPOCA, LocationEnum.TARGU_MURES, LocationEnum.TIMISOARA]);

  nameQuery = signal<string>('');
  startTimeQuery = signal<string>('');

  selectedTypes = signal<EventType[]>([]);
  selectedStatuses = signal<EventStatus[]>([]);
  selectedLocations = signal<LocationEnum[]>([]);

  pageIndex = signal<number>(0);
  pageSize = signal<number>(10);
  pageSizeOptions: number[] = [10, 20, 50];

  isLoading = signal<boolean>(false);

  userRole = signal<UserRole | null>(this.authService.getRole());

  // Real backend state:
  // pagedEvents = signal<Event[]>([]);
  // totalFilteredItems = signal<number>(0);

  // Temporary mock state:
  pagedEvents = signal<Event[]>(MOCK_EVENTS.slice(0, this.pageSize()));
  totalFilteredItems = signal<number>(MOCK_EVENTS.length);

  private filterParams = computed<EventFilterParams>(() => ({
    name: this.nameQuery().trim(),
    types: this.selectedTypes(),
    statuses: this.selectedStatuses(),
    locations: this.selectedLocations(),
    startTime: this.startTimeQuery(),
    pageId: this.pageIndex(),
    pageSize: this.pageSize(),
  }));

  private searchParams = computed(() => ({
    name: this.nameQuery().trim(),
    types: this.selectedTypes(),
    statuses: this.selectedStatuses(),
    locations: this.selectedLocations(),
    startTime: this.startTimeQuery(),
  }));

  constructor() {
    if (!isPlatformBrowser(inject(PLATFORM_ID))) {
      return;
    }

    // BACKEND VERSION - KEEP FOR LATER
    /*
    this.isLoading.set(true);

    toObservable(this.searchParams)
      .pipe(
        debounceTime(750),
        tap(() => this.isLoading.set(true)),
        switchMap(() =>
          this.eventService
            .getEvents(this.filterParams())
            .pipe(finalize(() => this.isLoading.set(false)))
        ),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (response) => {
          this.pagedEvents.set(response.content);
          this.totalFilteredItems.set(response.totalElements);
        },
        error: (error) => {
          console.error('Failed to load events', error);
        },
      });
    */
  }

  onNameSearchChange(value: string): void {
    // BACKEND VERSION
    /*
    this.isLoading.set(true);
    this.nameQuery.set(value);
    this.pageIndex.set(0);
    */

    this.nameQuery.set(value);
    this.pageIndex.set(0);
    this.applyMockFilters();
  }

  onTypeChange(value: EventType[]): void {
    // BACKEND VERSION
    /*
    this.isLoading.set(true);
    this.selectedTypes.set(value);
    this.pageIndex.set(0);
    */

    this.selectedTypes.set(value);
    this.pageIndex.set(0);
    this.applyMockFilters();
  }

  onStatusChange(value: EventStatus[]): void {
    // BACKEND VERSION
    /*
    this.isLoading.set(true);
    this.selectedStatuses.set(value);
    this.pageIndex.set(0);
    */

    this.selectedStatuses.set(value);
    this.pageIndex.set(0);
    this.applyMockFilters();
  }

  onLocationChange(value: LocationEnum[]): void {
    // BACKEND VERSION
    /*
    this.isLoading.set(true);
    this.selectedLocations.set(value);
    this.pageIndex.set(0);
    */

    this.selectedLocations.set(value);
    this.pageIndex.set(0);
    this.applyMockFilters();
  }

  onStartTimeChange(value: string): void {
    // BACKEND VERSION
    /*
    this.isLoading.set(true);
    this.startTimeQuery.set(value);
    this.pageIndex.set(0);
    */

    this.startTimeQuery.set(value);
    this.pageIndex.set(0);
    this.applyMockFilters();
  }

  onResetFilters(): void {
    // BACKEND VERSION
    /*
    this.isLoading.set(true);
    */

    this.nameQuery.set('');
    this.startTimeQuery.set('');
    this.selectedTypes.set([]);
    this.selectedStatuses.set([]);
    this.selectedLocations.set([]);
    this.pageIndex.set(0);

    this.applyMockFilters();
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);

    // BACKEND VERSION
    /*
    this.isLoading.set(true);
    this.loadEvents();
    */

    this.applyMockFilters();
  }

  onViewEvent(eventId: number): void {
    this.router.navigate(['/events', eventId]);
  }

  onEditEvent(eventId: number): void {
    this.router.navigate(['/events', eventId, 'edit']);
  }

  onPublishEvent(eventId: number): void {
    this.updateMockEventStatus(eventId, EventStatus.PUBLISHED);
  }

  onCompleteEvent(eventId: number): void {
    this.updateMockEventStatus(eventId, EventStatus.COMPLETED);
  }

  private updateMockEventStatus(eventId: number, status: EventStatus): void {
    this.pagedEvents.update((events) =>
      events.map((event) => (event.id === eventId ? { ...event, status } : event))
    );
  }

  private applyMockFilters(): void {
    const name = this.nameQuery().trim().toLowerCase();
    const selectedTypes = this.selectedTypes();
    const selectedStatuses = this.selectedStatuses();
    const selectedLocations = this.selectedLocations();
    const startTime = this.startTimeQuery();

    const filteredEvents = MOCK_EVENTS.filter((event) => {
      const matchesName = !name || event.name.toLowerCase().includes(name);

      const matchesType = selectedTypes.length === 0 || selectedTypes.includes(event.type);

      const matchesStatus =
        selectedStatuses.length === 0 || selectedStatuses.includes(event.status);

      const matchesLocation =
        selectedLocations.length === 0 || selectedLocations.includes(event.location);

      const matchesStartTime = !startTime || event.startTime.startsWith(startTime);

      return matchesName && matchesType && matchesStatus && matchesLocation && matchesStartTime;
    });

    this.totalFilteredItems.set(filteredEvents.length);

    const start = this.pageIndex() * this.pageSize();
    const end = start + this.pageSize();

    this.pagedEvents.set(filteredEvents.slice(start, end));
    this.isLoading.set(false);
  }

  // BACKEND VERSION - KEEP FOR LATER
  /*
  private loadEvents(): void {
    this.isLoading.set(true);

    this.eventService
      .getEvents(this.filterParams())
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe({
        next: (response) => {
          this.pagedEvents.set(response.content);
          this.totalFilteredItems.set(response.totalElements);
        },
        error: (error) => {
          console.error('Failed to load events', error);
        },
      });
  }
  */
}
