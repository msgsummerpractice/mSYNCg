import { Component, computed, signal, OnInit, inject } from '@angular/core';
import { UserListView } from '../views/user-list.view';
import { TableColumn } from '../../../../core/models/table.column.model';
import { AdminService } from '../../../../core/services/admin-service';
import { User } from '../../../../core/models/user.model';
import { MOCK_USERS } from '../../../../core/models/user.mocks';

@Component({
  selector: 'app-user-list-container',
  imports: [UserListView],
  template: `
    <app-user-list-view
      [users]="filteredUsers()"
      [columns]="tableColumns"
      [roles]="roles()"
      [locations]="locations()"
      [selectedRoles]="selectedRoles()"
      [selectedLocations]="selectedLocations()"
      [selectedStatuses]="selectedStatuses()"
      (nameSearchChange)="nameQuery.set($event)"
      (emailSearchChange)="emailQuery.set($event)"
      (roleChange)="selectedRoles.set($event)"
      (locationChange)="selectedLocations.set($event)"
      (statusChange)="selectedStatuses.set($event)"
      (cellAction)="onCellChange($event)"
    >
    </app-user-list-view>
  `,
})
export class UserListContainer implements OnInit {
  private adminService = inject(AdminService);

  tableColumns: TableColumn<User>[] = [
    {
      key: 'fullName',
      label: 'Full Name',
      type: 'text',
      valueGetter: (user) => `${user.firstName} ${user.lastName}`,
    },
    {
      key: 'email',
      label: 'Email',
      type: 'text',
    },
    {
      key: 'role',
      label: 'User Role',
      type: 'dropdown',
      options: ['ADMIN', 'HR_USER', 'PARTICIPANT', 'MARKETING_ORGANIZER'],
    },
    {
      key: 'location',
      label: 'Location',
      type: 'text',
    },
    {
      key: 'status',
      label: 'Status',
      type: 'switch',
    },
  ];

  allUsers = signal<User[]>([]);
  roles = signal<string[]>(['ADMIN', 'HR_USER', 'PARTICIPANT', 'MARKETING_ORGANIZER']);
  locations = signal<string[]>(['TARGU-MURES', 'CLUJ-NAPOCA', 'TIMISOARA']);

  nameQuery = signal<string>('');
  emailQuery = signal<string>('');

  selectedRoles = signal<string[]>([]);
  selectedLocations = signal<string[]>([]);
  selectedStatuses = signal<string[]>([]);

  filteredUsers = computed(() => {
    const searchName = this.nameQuery().toLowerCase().trim();
    const searchEmail = this.emailQuery().toLowerCase().trim();

    const activeRoles = this.selectedRoles();
    const activeLocations = this.selectedLocations();
    const activeStatuses = this.selectedStatuses();

    return this.allUsers().filter((user) => {
      const fullName = `${user.firstName} ${user.lastName}`.toLowerCase();
      const reverseName = `${user.lastName} ${user.firstName}`.toLowerCase();
      const matchesName =
        searchName === '' || fullName.includes(searchName) || reverseName.includes(searchName);

      const email = (user.email || '').toLowerCase();
      const matchesEmail = searchEmail === '' || email.includes(searchEmail);

      const matchesRole = activeRoles.length === 0 || activeRoles.includes(user.role);
      const matchesLocation =
        activeLocations.length === 0 || activeLocations.includes(user.location);

      const userStatus = (user as any).status || 'inactive';
      const matchesStatus = activeStatuses.length === 0 || activeStatuses.includes(userStatus);

      return matchesName && matchesEmail && matchesRole && matchesLocation && matchesStatus;
    });
  });

  ngOnInit() {
    // this.adminService.getAllUsers().subscribe({
    //   next: (users) => this.allUsers.set(users),
    //   error: (err) => console.error('Failed to load users', err),
    // });
    this.allUsers.set(MOCK_USERS);
  }

  onCellChange(event: { row: User; key: string; newValue: unknown }) {}
}
