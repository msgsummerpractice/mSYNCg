import { Component, computed, signal, OnInit, inject, Signal } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { UserListView } from '../views/user-list.view';
import { TableColumn } from '../../../../core/models/table.column.model';
import { AdminService } from '../../../../core/services/admin-service';
import { User } from '../../../../core/models/user.model';
import { MOCK_USERS } from '../../../../core/models/user.mocks';
import { UserRole } from '../../../../core/constants/role.constant';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogView } from '../../../../shared/components/views/confirmation-dialog/confirmation-dialog.view';
import { ToastService } from '../../../../core/services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { ToastContainer } from '../../../../shared/components/containers/toast.container';

@Component({
  selector: 'app-user-list-container',
  imports: [UserListView, ToastContainer],
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
    <app-toast-container></app-toast-container>
  `,
})
export class UserListContainer implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly dialog = inject(MatDialog);
  private readonly toastService = inject(ToastService);
  private readonly translate = inject(TranslateService);

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
      options: [
        UserRole.ADMIN,
        UserRole.HR_USER,
        UserRole.PARTICIPANT,
        UserRole.MARKETING_ORGANIZER,
      ],
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
  locations = signal<string[]>(['Targu Mures', 'Cluj-Napoca', 'Timisoara']);

  nameQuery = signal<string>('');
  emailQuery = signal<string>('');

  selectedRoles = signal<UserRole[]>([]);
  selectedLocations = signal<string[]>([]);
  selectedStatuses = signal<boolean[]>([]);

  pageIndex = signal<number>(0);
  pageSize = signal<number>(10);
  pageSizeOptions: number[] = [10, 20, 50];

  filteredUsers: Signal<User[]> = computed((): User[] => {
    const searchName: string = this.nameQuery().toLowerCase().trim();
    const searchEmail: string = this.emailQuery().toLowerCase().trim();

    const activeRoles: UserRole[] = this.selectedRoles();
    const activeLocations: string[] = this.selectedLocations();
    const activeStatuses: boolean[] = this.selectedStatuses();

    return this.allUsers().filter((user) => {
      const fullName: string = `${user.firstName} ${user.lastName}`.toLowerCase();
      const reverseName: string = `${user.lastName} ${user.firstName}`.toLowerCase();
      const matchesName: boolean =
        searchName === '' || fullName.includes(searchName) || reverseName.includes(searchName);

      const email: string = (user.email || '').toLowerCase();
      const matchesEmail: boolean = searchEmail === '' || email.includes(searchEmail);

      const matchesRole: boolean = activeRoles.length === 0 || activeRoles.includes(user.role);
      const matchesLocation: boolean =
        activeLocations.length === 0 || activeLocations.includes(user.location);

      const userStatus = (user as any).status;
      const matchesStatus: boolean =
        activeStatuses.length === 0 || activeStatuses.includes(userStatus);

      return matchesName && matchesEmail && matchesRole && matchesLocation && matchesStatus;
    });
  });

  totalFilteredItems: Signal<number> = computed((): number => this.filteredUsers().length);

  pagedUsers: Signal<User[]> = computed((): User[] => {
    const start: number = this.pageIndex() * this.pageSize();
    const end: number = start + this.pageSize();
    return this.filteredUsers().slice(start, end);
  });

  ngOnInit(): void {
    // this.adminService.getAllUsers().subscribe({
    //   next: (users) => this.allUsers.set(users),
    //   error: (err) => console.error('Failed to load users', err),
    // });
    this.allUsers.set(MOCK_USERS);
  }

  onNameSearchChange(value: string): void {
    this.nameQuery.set(value);
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

  updateUserRole(userId: string, newRole: UserRole): void {
    this.allUsers.update((users) => {
      return users.map((user) => (user.email === userId ? { ...user, role: newRole } : user));
    });
  }

  updateUserStatus(userId: string, newStatus: boolean): void {
    this.allUsers.update((users) => {
      return users.map((user) => (user.email === userId ? { ...user, status: newStatus } : user));
    });
  }

  onCellChange(event: { row: User; key: string; oldValue: unknown; newValue: unknown }): void {
    const oldRole = event.oldValue as UserRole;
    const oldStatus = event.oldValue as boolean;

    let isRoleChange = null;

    if (event.key === 'role' && oldRole === UserRole.ADMIN) {
      const errorMsg = this.translate.instant('ROLE_UPDATE.ADMIN_CHANGE_ERROR');
      this.toastService.showError(errorMsg);
      this.updateUserRole(event.row.email, oldRole);
      return;
    } else {
      isRoleChange =
        event.key === 'role' && Object.values(UserRole).includes(event.newValue as UserRole);
    }

    const isStatusChange = event.key === 'status' && typeof event.newValue === 'boolean';

    if (!isRoleChange && !isStatusChange) return;

    this.dialog
      .open(ConfirmationDialogView, {
        data: { message: this.translate.instant('CONFIRMATION_DIALOG.MESSAGE') },
        disableClose: true,
        width: '70 px',
        maxWidth: '100px',
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) {
          if (event.key === 'role') {
            this.updateUserRole(event.row.email, oldRole);
          } else if (event.key === 'status') {
            this.updateUserStatus(event.row.email, oldStatus);
          }
          return;
        }

        if (isRoleChange) {
          this.adminService.updateUserRole(event.row.email, event.newValue as UserRole).subscribe({
            next: () => {
              this.updateUserRole(event.row.email, event.newValue as UserRole);
              const successMsg = this.translate.instant('ROLE_UPDATE.SUCCESS');
              this.toastService.showSuccess(successMsg, 5000);
            },
            error: () => {
              this.updateUserRole(event.row.email, oldRole);
              const errorMsg = this.translate.instant('ROLE_UPDATE.FAILURE');
              this.toastService.showError(errorMsg);
            },
          });
          return;
        }

        if (isStatusChange) {
          this.adminService.updateUserStatus(event.row.email, event.newValue as boolean).subscribe({
            next: () => {
              const successMsg = this.translate.instant('STATUS_UPDATE.SUCCESS');
              this.updateUserStatus(event.row.email, event.newValue as boolean);
              this.toastService.showSuccess(successMsg, 5000);
            },
            error: () => {
              const errorMsg = this.translate.instant('STATUS_UPDATE.FAILURE');
              this.toastService.showError(errorMsg);
            },
          });
        }
      });
  }
}
