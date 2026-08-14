import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { GenericCellView } from '../../../../shared/components/views/generic-cell.view';
import { ButtonContainer } from '../../../../shared/components/containers/button.container';
import { TranslatePipe } from '@ngx-translate/core';
import { TableColumn } from '../../../../core/models/table.column.model';
import { User } from '../../../../core/models/user.model';
import { ToolbarContainer } from '../../../../shared/components/containers/toolbar.container';
import { UserRole } from '../../../../core/constants/role.constant';
import { UserLocation } from '../../../../core/constants/location.constant';

@Component({
  selector: 'app-user-list-view',
  imports: [
    CommonModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatPaginatorModule,
    GenericCellView,
    ButtonContainer,
    TranslatePipe,
    ToolbarContainer,
  ],
  templateUrl: './user-list.view.html',
})
export class UserListView {
  private readonly roleLabelKeys: Record<UserRole, string> = {
    [UserRole.ADMIN]: 'USER_LIST.ROLES.ADMIN',
    [UserRole.HR_USER]: 'USER_LIST.ROLES.HR_USER',
    [UserRole.PARTICIPANT]: 'USER_LIST.ROLES.PARTICIPANT',
    [UserRole.MARKETING_ORGANIZER]: 'USER_LIST.ROLES.MARKETING_ORGANIZER',
  };

  private readonly locationLabelKeys: Record<UserLocation, string> = {
    [UserLocation.TARGU_MURES]: 'USER_LIST.LOCATIONS.TARGU_MURES',
    [UserLocation.CLUJ_NAPOCA]: 'USER_LIST.LOCATIONS.CLUJ_NAPOCA',
    [UserLocation.TIMISOARA]: 'USER_LIST.LOCATIONS.TIMISOARA',
  };

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
  @Input() totalItems = 0;
  @Input() pageIndex = 0;
  @Input() pageSize = 10;
  @Input() pageSizeOptions: number[] = [10, 20, 50];

  @Output() firstNameSearchChange = new EventEmitter<string>();
  @Output() lastNameSearchChange = new EventEmitter<string>();
  @Output() emailSearchChange = new EventEmitter<string>();

  @Output() roleChange = new EventEmitter<UserRole[]>();
  @Output() locationChange = new EventEmitter<UserLocation[]>();
  @Output() statusChange = new EventEmitter<boolean[]>();
  @Output() resetFilters = new EventEmitter<void>();
  @Output() pageChange = new EventEmitter<PageEvent>();

  @Output() cellAction = new EventEmitter<{ row: User; key: string; newValue: unknown }>();

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
    return this.roleLabelKeys[role] ?? role;
  }

  getLocationLabelKey(location: UserLocation): string {
    return this.locationLabelKeys[location] ?? location;
  }
}
