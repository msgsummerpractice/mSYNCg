import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { GenericCellView } from '../../../../shared/components/views/generic-cell.view';
import { TableColumn } from '../../../../core/models/table.column.model';
import { User } from '../../../../core/models/user.model';

@Component({
  selector: 'app-user-list-view',
  imports: [
    CommonModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    GenericCellView,
  ],
  templateUrl: './user-list.view.html',
})
export class UserListView {
  @Input() users: User[] = [];
  @Input() columns: TableColumn<User>[] = [];

  @Input() roles: string[] = [];
  @Input() locations: string[] = [];

  @Input() selectedRoles: string[] = [];
  @Input() selectedLocations: string[] = [];
  @Input() selectedStatuses: string[] = [];

  @Output() nameSearchChange = new EventEmitter<string>();
  @Output() emailSearchChange = new EventEmitter<string>();

  @Output() roleChange = new EventEmitter<string[]>();
  @Output() locationChange = new EventEmitter<string[]>();
  @Output() statusChange = new EventEmitter<string[]>();

  @Output() cellAction = new EventEmitter<{ row: User; key: string; newValue: unknown }>();

  get displayedColumnKeys(): string[] {
    return this.columns.map((col) => col.key);
  }

  onNameSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.nameSearchChange.emit(value);
  }

  onEmailSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.emailSearchChange.emit(value);
  }
}
