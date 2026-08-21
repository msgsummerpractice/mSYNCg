import { Component, computed, signal, inject, PLATFORM_ID, DestroyRef } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { PageEvent } from '@angular/material/paginator';
import { debounceTime, finalize, switchMap, tap } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';

import { EventListView } from '../views/event-list/event-list.view';
import { EventCardContainer } from './event-card.container';
import { EventService } from '../../../../core/services/event.service';
import { AuthService } from '../../../../core/services/auth.service';
import { EventView } from '../../../../core/models/event.model';
import { EventFilterParams } from '../../../../core/models/event.model';
import { TableColumn } from '../../../../core/models/table.column.model';

import { EventTypeEnum, EventStatusEnum } from '../../../../core/constants/event.constant';
import { EventLocation } from '../../../../core/constants/location.constant';
import { UserRole } from '../../../../core/constants/role.constant';
import { ToastService } from '../../../../core/services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { OnInit } from '@angular/core';

@Component({
  selector: 'app-event-list-container',
  standalone: true,
  imports: [EventListView, EventCardContainer],
  templateUrl: './event-list.container.html',
})
export class EventListContainer implements OnInit {
  private readonly eventService = inject(EventService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);
  private readonly toastService = inject(ToastService);
  private readonly translateService = inject(TranslateService);

  tableColumns: TableColumn<EventView>[] = [
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
        event.startTime
          ? new Date(event.startTime).toLocaleString('ro-RO', {
              day: '2-digit',
              month: '2-digit',
              year: 'numeric',
              hour: '2-digit',
              minute: '2-digit',
            })
          : '-',
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

  types = signal<EventTypeEnum[]>([
    EventTypeEnum.INTERNAL,
    EventTypeEnum.EXTERNAL,
    EventTypeEnum.LOCAL,
  ]);

  statuses = signal<EventStatusEnum[]>([
    EventStatusEnum.DRAFT,
    EventStatusEnum.PUBLISHED,
    EventStatusEnum.COMPLETED,
  ]);

  locations = signal<EventLocation[]>([
    EventLocation.CLUJ_NAPOCA,
    EventLocation.TARGU_MURES,
    EventLocation.TIMISOARA,
  ]);

  nameQuery = signal<string>('');
  startTimeQuery = signal<string>('');

  selectedTypes = signal<EventTypeEnum[]>([]);
  selectedStatuses = signal<EventStatusEnum[]>([]);
  selectedLocations = signal<EventLocation[]>([]);

  pageIndex = signal<number>(0);
  pageSize = signal<number>(10);
  pageSizeOptions: number[] = [10, 20, 50];

  isLoading = signal<boolean>(false);

  userRole = signal<UserRole | null>(this.authService.getRole());

  pagedEvents = signal<EventView[]>([]);
  totalFilteredItems = signal<number>(0);

  readonly selectedEventId = signal<number | null>(null);

  private filterParams = computed<EventFilterParams>(() => ({
    name: this.nameQuery().trim(),
    types: this.selectedTypes(),
    statuses: this.selectedStatuses(),
    locations: this.selectedLocations(),
    startTime: this.startTimeQuery(),
    pageId: this.pageIndex(),
    pageSize: this.pageSize(),
  }));

  private readonly filterParams$ = toObservable(this.filterParams);

  ngOnInit(): void {
    this.route.queryParamMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      const raw = params.get('eventId');
      const parsed = raw !== null ? Number(raw) : NaN;
      this.selectedEventId.set(Number.isInteger(parsed) && parsed > 0 ? parsed : null);
    });

    this.filterParams$
      .pipe(
        debounceTime(750),
        tap(() => this.isLoading.set(true)),
        switchMap((filters) =>
          this.getEventsForCurrentUser(filters).pipe(finalize(() => this.isLoading.set(false)))
        ),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (response) => {
          this.pagedEvents.set(response.content);
          this.totalFilteredItems.set(response.totalElements);
        },
        error: () => this.handleLoadError(),
      });
  }

  onNameSearchChange(value: string): void {
    this.nameQuery.set(value);
    this.pageIndex.set(0);
  }

  onTypeChange(value: EventTypeEnum[]): void {
    this.selectedTypes.set(value);
    this.pageIndex.set(0);
  }

  onStatusChange(value: EventStatusEnum[]): void {
    this.selectedStatuses.set(value);
    this.pageIndex.set(0);
  }

  onLocationChange(value: EventLocation[]): void {
    this.selectedLocations.set(value);
    this.pageIndex.set(0);
  }

  onStartTimeChange(value: string): void {
    this.startTimeQuery.set(value);
    this.pageIndex.set(0);
  }

  onResetFilters(): void {
    this.isLoading.set(true);
    this.nameQuery.set('');
    this.startTimeQuery.set('');
    this.selectedTypes.set([]);
    this.selectedStatuses.set([]);
    this.selectedLocations.set([]);
    this.pageIndex.set(0);
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }

  onViewEvent(eventId: number): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { eventId },
      queryParamsHandling: 'merge',
    });
  }

  onCloseEventCard(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { eventId: null },
      queryParamsHandling: 'merge',
    });
  }

  onEditEvent(eventId: number): void {
    this.router.navigate([`/events/update/${eventId}`]);
  }

  onPublishEvent(eventId: number): void {
    // TODO: Implement backend call to publish event
    this.toastService.showSuccess('Event published!');
  }

  onCompleteEvent(eventId: number): void {
    // TODO: Implement backend call to complete event
    this.toastService.showSuccess('Event completed!');
  }

  private handleLoadError(): void {
    this.toastService.showError(this.translateService.instant('EVENT_LIST.LOAD_ERROR'));
  }

  private getEventsForCurrentUser(filters: EventFilterParams) {
    if (this.userRole() === UserRole.PARTICIPANT) {
      // TODO: use getEligibleEvents(filters) when the backend endpoint is implemented
      // return this.eventService.getEligibleEvents(filters);

      return this.eventService.getEvents(filters);
    }

    return this.eventService.getEvents(filters);
  }
}
