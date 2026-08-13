import { Component, computed, signal, OnInit, inject } from '@angular/core';
import { UserListView } from '../views/user-list.view';
import { TableColumn } from '../../../../core/models/table.column.model';
import { AdminService } from '../../../../core/services/admin-service';
import type { User } from '../../../../core/models/user.model';

@Component({
  selector: 'app-user-list-container',
  imports: [UserListView],
  template: `
    <app-user-list-view
      [users]="filteredUsers()"
      [columns]="tableColumns"
      [roles]="roles()"
      [locations]="locations()"
      (searchChange)="searchQuery.set($event)"
      (roleChange)="selectedRole.set($event)"
      (locationChange)="selectedLocation.set($event)"
      (cellAction)="onCellChange($event)"
    >
    </app-user-list-view>
  `,
})
export class UserListContainer {
  private userService = inject(AdminService);

  tableColumns: TableColumn<User>[] = [
    {
      key: 'fullName',
      label: 'Full Name',
      type: 'text',
      valueGetter: (user) => `${user.firstName} ${user.lastName}`,
    },
    {
      key: 'role',
      label: 'User Role',
      type: 'dropdown',
      options: ['Admin', 'Manager', 'Employee'],
    },
    {
      key: 'location',
      label: 'Location',
      type: 'text',
    },
  ];

  allUsers = signal<User[]>([]);
  roles = signal<string[]>(['Admin', 'Manager', 'Employee']);
  locations = signal<string[]>(['București', 'Cluj-Napoca', 'Timișoara']);

  searchQuery = signal<string>('');
  selectedRole = signal<string | null>(null);
  selectedLocation = signal<string | null>(null);

  filteredUsers = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    const role = this.selectedRole();
    const location = this.selectedLocation();

    return this.allUsers().filter((user) => {
      const fullName = `${user.firstName} ${user.lastName}`.toLowerCase();

      const matchesSearch = fullName.includes(query);
      const matchesRole = role ? user.role === role : true;
      const matchesLocation = location ? user.location === location : true;

      return matchesSearch && matchesRole && matchesLocation;
    });
  });

  ngOnInit() {
    this.userService.getAllUsers().subscribe({
      next: (users) => this.allUsers.set(users),
      error: (err) => console.error('Failed to load users', err),
    });
  }

  onCellChange(event: { row: User; key: string; newValue: unknown }) {}
}
