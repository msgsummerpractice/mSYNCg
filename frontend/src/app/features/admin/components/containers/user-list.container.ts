import { Component, computed, signal, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { PageEvent } from '@angular/material/paginator';
import { debounceTime, switchMap } from 'rxjs/operators';
import { UserListView } from '../views/user-list.view';
import { TableColumn } from '../../../../core/models/table.column.model';
import { AdminService } from '../../../../core/services/admin-service';
import { User } from '../../../../core/models/user.model';
import { UserFilterParams } from '../../../../core/models/user-filters.model';
import { UserRole, USER_ROLE_DISPLAY_VALUES } from '../../../../core/constants/role.constant';
import { UserLocation } from '../../../../core/constants/location.constant';

@Component({
  selector: 'app-user-list-container',
  imports: [UserListView],
  template: `
    <app-user-list-view
      [users]="pagedUsers()"
      [columns]="tableColumns"
      [roles]="roles()"
      [locations]="locations()"
      [selectedRoles]="selectedRoles()"
      [selectedLocations]="selectedLocations()"
      [selectedStatuses]="selectedStatuses()"
      [firstNameQuery]="firstNameQuery()"
      [lastNameQuery]="lastNameQuery()"
      [emailQuery]="emailQuery()"
      [totalItems]="totalFilteredItems()"
      [pageIndex]="pageIndex()"
      [pageSize]="pageSize()"
      [pageSizeOptions]="pageSizeOptions"
      (firstNameSearchChange)="onFirstNameSearchChange($event)"
      (lastNameSearchChange)="onLastNameSearchChange($event)"
      (emailSearchChange)="onEmailSearchChange($event)"
      (roleChange)="onRoleChange($event)"
      (locationChange)="onLocationChange($event)"
      (statusChange)="onStatusChange($event)"
      (resetFilters)="onResetFilters()"
      (pageChange)="onPageChange($event)"
      (cellAction)="onCellChange($event)"
    >
    </app-user-list-view>
  `,
})
export class UserListContainer {
  private adminService = inject(AdminService);

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

  private filterParams = computed<UserFilterParams>(() => ({
    firstName: this.firstNameQuery().trim(),
    lastName: this.lastNameQuery().trim(),
    email: this.emailQuery().trim(),
    roles: this.selectedRoles(),
    locations: this.selectedLocations(),
    statuses: this.selectedStatuses(),
    page: this.pageIndex(),
    size: this.pageSize(),
  }));

  constructor() {
    if (!isPlatformBrowser(inject(PLATFORM_ID))) {
      return;
    }

    toObservable(this.filterParams)
      .pipe(
        debounceTime(750),
        switchMap((params) => this.adminService.getUsers(params)),
        takeUntilDestroyed()
      )
      .subscribe({
        next: (response) => {
          this.pagedUsers.set(response.content);
          this.totalFilteredItems.set(response.totalElements);
        },
        error: (err) => console.error('Failed to load users', err),
      });
  }

  onFirstNameSearchChange(value: string): void {
    this.firstNameQuery.set(value);
    this.pageIndex.set(0);
  }

  onLastNameSearchChange(value: string): void {
    this.lastNameQuery.set(value);
    this.pageIndex.set(0);
  }

  onEmailSearchChange(value: string): void {
    this.emailQuery.set(value);
    this.pageIndex.set(0);
  }

  onRoleChange(value: UserRole[]): void {
    this.selectedRoles.set(value);
    this.pageIndex.set(0);
  }

  onLocationChange(value: UserLocation[]): void {
    this.selectedLocations.set(value);
    this.pageIndex.set(0);
  }

  onStatusChange(value: boolean[]): void {
    this.selectedStatuses.set(value);
    this.pageIndex.set(0);
  }

  onResetFilters(): void {
    this.firstNameQuery.set('');
    this.lastNameQuery.set('');
    this.emailQuery.set('');
    this.selectedRoles.set([]);
    this.selectedLocations.set([]);
    this.selectedStatuses.set([]);
    this.pageIndex.set(0);
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }

  onCellChange(event: { row: User; key: string; newValue: unknown }): void {}
}
