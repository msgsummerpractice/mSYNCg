import { Component, computed, signal, inject } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { PageEvent } from '@angular/material/paginator';
import { debounceTime, switchMap } from 'rxjs/operators';
import { UserListView } from '../views/user-list.view';
import { TableColumn } from '../../../../core/models/table.column.model';
import { AdminService } from '../../../../core/services/admin-service';
import { User } from '../../../../core/models/user.model';
import { UserFilterParams } from '../../../../core/models/user-filters.model';

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
      [nameQuery]="nameQuery()"
      [emailQuery]="emailQuery()"
      [totalItems]="totalFilteredItems()"
      [pageIndex]="pageIndex()"
      [pageSize]="pageSize()"
      [pageSizeOptions]="pageSizeOptions"
      (nameSearchChange)="onNameSearchChange($event)"
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
      key: 'fullName',
      label: 'USER_LIST.TABLE.FULL_NAME',
      type: 'text',
      valueGetter: (user) => `${user.firstName} ${user.lastName}`,
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
      options: ['Admin', 'HR User', 'Participant', 'Marketing Organizer'],
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

  roles = signal<string[]>(['Admin', 'HR User', 'Participant', 'Marketing Organizer']);
  locations = signal<string[]>(['Targu Mures', 'Cluj-Napoca', 'Timisoara']);

  nameQuery = signal<string>('');
  emailQuery = signal<string>('');

  selectedRoles = signal<string[]>([]);
  selectedLocations = signal<string[]>([]);
  selectedStatuses = signal<boolean[]>([]);

  pageIndex = signal<number>(0);
  pageSize = signal<number>(10);
  pageSizeOptions: number[] = [10, 20, 50];
  pagedUsers = signal<User[]>([]);
  totalFilteredItems = signal<number>(0);

  private filterParams = computed<UserFilterParams>(() => ({
    name: this.nameQuery().trim(),
    email: this.emailQuery().trim(),
    roles: this.selectedRoles(),
    locations: this.selectedLocations(),
    statuses: this.selectedStatuses(),
    page: this.pageIndex(),
    size: this.pageSize(),
  }));

  constructor() {
    toObservable(this.filterParams)
      .pipe(
        debounceTime(2000),
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

  onNameSearchChange(value: string): void {
    this.nameQuery.set(value);
    this.pageIndex.set(0);
  }

  onEmailSearchChange(value: string): void {
    this.emailQuery.set(value);
    this.pageIndex.set(0);
  }

  onRoleChange(value: string[]): void {
    this.selectedRoles.set(value);
    this.pageIndex.set(0);
  }

  onLocationChange(value: string[]): void {
    this.selectedLocations.set(value);
    this.pageIndex.set(0);
  }

  onStatusChange(value: boolean[]): void {
    this.selectedStatuses.set(value);
    this.pageIndex.set(0);
  }

  onResetFilters(): void {
    this.nameQuery.set('');
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
