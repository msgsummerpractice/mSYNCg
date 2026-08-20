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
import { ConfirmationDialogView } from '../../../../shared/components/views/confirmation-dialog/confirmation-dialog.view';
import { MatDialog } from '@angular/material/dialog';
import { ToastContainer } from '../../../../shared/components/containers/toast.container';
import { AuthService } from '../../../../core/services/auth.service';
import { UserCellChangeEvent } from '../../../../core/models/user-cell-change-event.model';

@Component({
  selector: 'app-user-list-container',
  imports: [UserListView, ToastContainer],
  templateUrl: 'user-list.container.html',
})
export class UserListContainer {
  private adminService = inject(AdminService);
  private destroyRef = inject(DestroyRef);
  private toastService = inject(ToastService);
  private translateService = inject(TranslateService);
  private readonly dialog = inject(MatDialog);
  private authService = inject(AuthService);

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
      options: Object.values(USER_ROLE_DISPLAY_VALUES).map((label) => ({
        value: label,
        label,
      }))
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

  allUsers = signal<User[]>([]);
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

  private searchParams1 = toObservable(this.searchParams);

  private confirmRoleChange(event: {
    row: User;
    key: string;
    oldValue: unknown;
    newValue: unknown;
  }): boolean {
    return event.key === 'role' && typeof event.newValue === 'string';
  }

  private confirmStatusChange(event: {
    row: User;
    key: string;
    oldValue: unknown;
    newValue: unknown;
  }): boolean {
    return event.key === 'status' && typeof event.newValue === 'boolean';
  }

  private ifAdminTriesToModifyOwnRole(event: {
    row: User;
    key: string;
    oldValue: unknown;
    newValue: unknown;
  }): boolean {
    return (
      event.row.email === this.authService.getEmail() &&
      this.authService.getRole() === UserRole.ADMIN
    );
  }

  private updateUserRoleInDialog(event: UserCellChangeEvent): void {
    if (event.key === 'role') {
      this.adminService.updateUserRole(event.row.id!, event.newValue).subscribe({
        next: () => {
          this.updateUserRole(event.row.id!, event.newValue);
          const successMsg = this.translateService.instant('ROLE_UPDATE.SUCCESS');
          this.toastService.showSuccess(successMsg, 5000);
        },
        error: () => {
          this.updateUserRole(event.row.id!, event.oldValue);
          const errorMsg = this.translateService.instant('ROLE_UPDATE.FAILURE');
          this.toastService.showError(errorMsg);
        },
      });
      return;
    }
  }

  private updateUserStatusInDialog(event: UserCellChangeEvent): void {
    if (event.key === 'status') {
      this.adminService.updateUserStatus(event.row.id!, event.newValue).subscribe({
        next: () => {
          const successMsg = this.translateService.instant('STATUS_UPDATE.SUCCESS');
          this.updateUserStatus(event.row.id!, event.newValue);
          this.toastService.showSuccess(successMsg, 5000);
        },
        error: () => {
          this.updateUserStatus(event.row.id!, event.oldValue);
          const errorMsg = this.translateService.instant('STATUS_UPDATE.FAILURE');
          this.toastService.showError(errorMsg);
        },
      });
    }
  }

  ngOnInit(): void {
    this.isLoading.set(true);

    this.searchParams1
      .pipe(
        debounceTime(750),
        tap(() => this.isLoading.set(true)),
        switchMap(() =>
          this.adminService
            .getUsers(this.filterParams())
            .pipe(finalize(() => this.isLoading.set(false)))
        ),
        takeUntilDestroyed(this.destroyRef)
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

  updateUserRole(userId: number, newRole: UserRole): void {
    this.pagedUsers.update((users) => {
      return users.map((user) => (user.id === userId ? { ...user, role: newRole } : user));
    });
  }

  updateUserStatus(userId: number, newStatus: boolean): void {
    this.pagedUsers.update((users) => {
      return users.map((user) => (user.id === userId ? { ...user, status: newStatus } : user));
    });
  }

  onCellChange(event: UserCellChangeEvent): void {
    let isRoleChange = false;
    let isStatusChange = false;

    if (this.ifAdminTriesToModifyOwnRole(event)) {
      const errorMsg = this.translateService.instant('ROLE_UPDATE.ADMIN_CHANGE_ERROR');
      this.toastService.showError(errorMsg);
      if (event.key === 'role') {
        this.updateUserRole(event.row.id!, event.oldValue);
      }
      return;
    } else {
      isRoleChange = this.confirmRoleChange(event);
      isStatusChange = this.confirmStatusChange(event);
    }

    if (!isRoleChange && !isStatusChange) return;

    this.dialog
      .open(ConfirmationDialogView, {
        data: { message: this.translateService.instant('CONFIRMATION_DIALOG.MESSAGE') },
        disableClose: true,
        width: '70px',
        maxWidth: '100px',
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) {
          if (event.key === 'role') {
            this.updateUserRole(event.row.id!, event.oldValue);
          } else if (event.key === 'status') {
            this.updateUserStatus(event.row.id!, event.oldValue);
          }
          return;
        }

        if (isRoleChange) {
          this.updateUserRoleInDialog(event);
        }

        if (isStatusChange) {
          this.updateUserStatusInDialog(event);
        }
      });
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
