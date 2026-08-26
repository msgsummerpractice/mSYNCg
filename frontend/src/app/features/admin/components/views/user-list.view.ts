import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { GenericCellView } from '../../../../shared/components/views/generic-cell.view';
import { ButtonContainer } from '../../../../shared/components/containers/button.container';
import { TranslatePipe } from '@ngx-translate/core';
import { TableColumn } from '../../../../core/models/table.column.model';
import { User } from '../../../../core/models/user.model';
import { UserRole, USER_ROLE_TRANSLATION_KEYS } from '../../../../core/constants/role.constant';
import {
  UserLocation,
  USER_LOCATION_TRANSLATION_KEYS,
} from '../../../../core/constants/location.constant';
import { UserCellChangeEvent } from '../../../../core/models/layout.model';
import { CellChangeEvent } from '../../../../core/models/layout.model';

@Component({
  selector: 'app-user-list-view',
  imports: [
    CommonModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    GenericCellView,
    ButtonContainer,
    TranslatePipe,
  ],
  templateUrl: './user-list.view.html',
  host: { class: 'flex flex-1 flex-col' },
})
export class UserListView {
  @Input() users: User[] = [];
  @Input() columns: TableColumn<User>[] = [];

  @Input() roles: UserRole[] = [];
  @Input() locations: UserLocation[] = [];

  @Input() selectedRoles: UserRole[] = [];
  @Input() selectedLocations: UserLocation[] = [];
  @Input() selectedStatuses: boolean[] = [];
  @Input() firstNameQuery = '';
  @Input() lastNameQuery = '';
  @Input() emailQuery = '';
  @Input({ required: true }) totalItems!: number;
  @Input() pageIndex = 0;
  @Input() pageSize = 10;
  @Input() pageSizeOptions: number[] = [10, 20, 50];
  @Input() isLoading = false;

  @Output() firstNameSearchChange = new EventEmitter<string>();
  @Output() lastNameSearchChange = new EventEmitter<string>();
  @Output() emailSearchChange = new EventEmitter<string>();

  @Output() roleChange = new EventEmitter<UserRole[]>();
  @Output() locationChange = new EventEmitter<UserLocation[]>();
  @Output() statusChange = new EventEmitter<boolean[]>();
  @Output() resetFilters = new EventEmitter<void>();
  @Output() pageChange = new EventEmitter<PageEvent>();

  @Output() cellAction = new EventEmitter<UserCellChangeEvent>();

  get resolvedTotalItems(): number {
    return this.totalItems > 0 ? this.totalItems : this.users.length;
  }

  get displayedColumnKeys(): string[] {
    return this.columns.map((col) => col.key);
  }

  onFirstNameSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.firstNameSearchChange.emit(value);
  }

  onLastNameSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.lastNameSearchChange.emit(value);
  }

  onEmailSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.emailSearchChange.emit(value);
  }

  getRoleLabelKey(role: UserRole): string {
    return USER_ROLE_TRANSLATION_KEYS[role] ?? role;
  }

  getLocationLabelKey(location: UserLocation): string {
    return USER_LOCATION_TRANSLATION_KEYS[location] ?? location;
  }

  onCellValueChanged(event: CellChangeEvent<User, string, unknown>): void {
    if (
      event.key === 'role' &&
      this.isUserRole(event.oldValue) &&
      this.isUserRole(event.newValue)
    ) {
      this.cellAction.emit({
        row: event.row,
        key: 'role',
        oldValue: event.oldValue,
        newValue: event.newValue,
      });
      return;
    }

    if (
      event.key === 'status' &&
      typeof event.oldValue === 'boolean' &&
      typeof event.newValue === 'boolean'
    ) {
      this.cellAction.emit({
        row: event.row,
        key: 'status',
        oldValue: event.oldValue,
        newValue: event.newValue,
      });
    }
  }

  private isUserRole(value: unknown): value is UserRole {
    return Object.values(UserRole).some((role) => role === value);
  }
}
