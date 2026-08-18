import { Component, computed, signal, inject, PLATFORM_ID, DestroyRef } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { PageEvent } from '@angular/material/paginator';
import { debounceTime, finalize, switchMap, tap } from 'rxjs/operators';
import { UserListView } from '../views/user-list.view';
import { TableColumn } from '../../../../core/models/table.column.model';
import { AdminService } from '../../../../core/services/admin-service';
import { User } from '../../../../core/models/user.model';
import { UserFilterParams } from '../../../../core/models/user-filters.model';
import { UserRole, USER_ROLE_DISPLAY_VALUES } from '../../../../core/constants/role.constant';
import { UserLocation } from '../../../../core/constants/location.constant';
import { ToastService } from '../../../../core/services/toast.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-user-list-container',
  imports: [UserListView],
  templateUrl: 'user-list.container.html',
})
export class UserListContainer {
  private adminService = inject(AdminService);
  private destroyRef = inject(DestroyRef);
  private toastService = inject(ToastService);
  private translateService = inject(TranslateService);

  tableColumns: TableColumn<User>[] = [
    {
      key: 'firstName',
      label: 'USER_LIST.TABLE.FIRST_NAME',
      type: 'text',
    },
    {
      key: 'lastName',
      label: 'USER_LIST.TABLE.LAST_NAME',
      type: 'text',
    },
    {
      key: 'email',
      label: 'USER_LIST.TABLE.EMAIL',
      type: 'text',
    },
    {
      key: 'role',
      label: 'USER_LIST.TABLE.USER_ROLE',
      type: 'dropdown',
      options: Object.values(USER_ROLE_DISPLAY_VALUES),
    },
    {
      key: 'location',
      label: 'USER_LIST.TABLE.LOCATION',
      type: 'text',
    },
    {
      key: 'status',
      label: 'USER_LIST.TABLE.STATUS',
      type: 'switch',
    },
  ];

  roles = signal<UserRole[]>([
    UserRole.ADMIN,
    UserRole.HR_USER,
    UserRole.PARTICIPANT,
    UserRole.MARKETING_ORGANIZER,
  ]);
  locations = signal<UserLocation[]>([
    UserLocation.TARGU_MURES,
    UserLocation.CLUJ_NAPOCA,
    UserLocation.TIMISOARA,
  ]);

  firstNameQuery = signal<string>('');
  lastNameQuery = signal<string>('');
  emailQuery = signal<string>('');

  selectedRoles = signal<UserRole[]>([]);
  selectedLocations = signal<UserLocation[]>([]);
  selectedStatuses = signal<boolean[]>([]);

  pageIndex = signal<number>(0);
  pageSize = signal<number>(10);
  pageSizeOptions: number[] = [10, 20, 50];
  pagedUsers = signal<User[]>([]);
  totalFilteredItems = signal<number>(0);
  isLoading = signal<boolean>(false);

  private filterParams = computed<UserFilterParams>(() => ({
    firstName: this.firstNameQuery().trim(),
    lastName: this.lastNameQuery().trim(),
    email: this.emailQuery().trim(),
    roles: this.selectedRoles(),
    locations: this.selectedLocations(),
    statuses: this.selectedStatuses(),
    pageId: this.pageIndex(),
    pageSize: this.pageSize(),
  }));

  private searchParams = computed(() => ({
    firstName: this.firstNameQuery().trim(),
    lastName: this.lastNameQuery().trim(),
    email: this.emailQuery().trim(),
    roles: this.selectedRoles(),
    locations: this.selectedLocations(),
    statuses: this.selectedStatuses(),
  }));

  constructor() {
    if (!isPlatformBrowser(inject(PLATFORM_ID))) {
      return;
    }

    this.isLoading.set(true);

    toObservable(this.searchParams)
      .pipe(
        debounceTime(750),
        tap(() => this.isLoading.set(true)),
        switchMap(() =>
          this.adminService
            .getUsers(this.filterParams())
            .pipe(finalize(() => this.isLoading.set(false)))
        ),
        takeUntilDestroyed()
      )
      .subscribe({
        next: (response) => {
          this.pagedUsers.set(response.content);
          this.totalFilteredItems.set(response.totalElements);
        },
        error: (err) => this.handleLoadError(err),
      });
  }

  onFirstNameSearchChange(value: string): void {
    this.isLoading.set(true);
    this.firstNameQuery.set(value);
    this.pageIndex.set(0);
  }

  onLastNameSearchChange(value: string): void {
    this.isLoading.set(true);
    this.lastNameQuery.set(value);
    this.pageIndex.set(0);
  }

  onEmailSearchChange(value: string): void {
    this.isLoading.set(true);
    this.emailQuery.set(value);
    this.pageIndex.set(0);
  }

  onRoleChange(value: UserRole[]): void {
    this.isLoading.set(true);
    this.selectedRoles.set(value);
    this.pageIndex.set(0);
  }

  onLocationChange(value: UserLocation[]): void {
    this.isLoading.set(true);
    this.selectedLocations.set(value);
    this.pageIndex.set(0);
  }

  onStatusChange(value: boolean[]): void {
    this.isLoading.set(true);
    this.selectedStatuses.set(value);
    this.pageIndex.set(0);
  }

  onResetFilters(): void {
    this.isLoading.set(true);
    this.firstNameQuery.set('');
    this.lastNameQuery.set('');
    this.emailQuery.set('');
    this.selectedRoles.set([]);
    this.selectedLocations.set([]);
    this.selectedStatuses.set([]);
    this.pageIndex.set(0);
  }

  onPageChange(event: PageEvent): void {
    this.isLoading.set(true);
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadUsers();
  }

  onCellChange(event: { row: User; key: string; newValue: unknown }): void {
    //TODO: Implement cell change logic, e.g., send an update request to the server
  }

  private loadUsers(): void {
    this.isLoading.set(true);
    this.adminService
      .getUsers(this.filterParams())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: (response) => {
          this.pagedUsers.set(response.content);
          this.totalFilteredItems.set(response.totalElements);
        },
        error: (err) => this.handleLoadError(err),
      });
  }

  private handleLoadError(error: unknown): void {
    this.toastService.showError(this.translateService.instant('USER_LIST.LOAD_ERROR'));
  }
}
