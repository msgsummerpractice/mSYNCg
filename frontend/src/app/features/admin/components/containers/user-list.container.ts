import { Component, computed, signal, OnInit, inject, Signal } from '@angular/core';
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
      options: ['Admin', 'HR User', 'Participant', 'Marketing Organizer'],
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
  roles = signal<string[]>(['Admin', 'HR User', 'Participant', 'Marketing Organizer']);
  locations = signal<string[]>(['Targu Mures', 'Cluj-Napoca', 'Timisoara']);

  nameQuery = signal<string>('');
  emailQuery = signal<string>('');

  selectedRoles = signal<string[]>([]);
  selectedLocations = signal<string[]>([]);
  selectedStatuses = signal<boolean[]>([]);

  filteredUsers: Signal<User[]> = computed((): User[] => {
    const searchName: string = this.nameQuery().toLowerCase().trim();
    const searchEmail: string = this.emailQuery().toLowerCase().trim();

    const activeRoles: string[] = this.selectedRoles();
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

  ngOnInit(): void {
    // this.adminService.getAllUsers().subscribe({
    //   next: (users) => this.allUsers.set(users),
    //   error: (err) => console.error('Failed to load users', err),
    // });
    this.allUsers.set(MOCK_USERS);
  }

  onCellChange(event: { row: User; key: string; newValue: unknown }): void {}
}
