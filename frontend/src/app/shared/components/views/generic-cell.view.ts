import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { TableColumn } from '../../../core/models/table.column.model';

@Component({
  selector: 'generic-cell-view',
  standalone: true,
  imports: [CommonModule, MatSelectModule, MatFormFieldModule],
  template: `
    @switch (column.type) {
      @case ('dropdown') {
        <mat-form-field appearance="outline" subscriptSizing="dynamic" class="cell-dropdown">
          <mat-select [value]="displayValue" (selectionChange)="onSelectionChange($event.value)">
            @for (option of column.options; track option) {
              <mat-option [value]="option">{{ option }}</mat-option>
            }
          </mat-select>
        </mat-form-field>
      }

      @default {
        <span>{{ displayValue }}</span>
      }
    }
  `,
  styles: [
    `
      .cell-dropdown {
        width: 100%;
        min-width: 120px;
      }
    `,
  ],
})
export class GenericCellView<T> {
  @Input({ required: true }) column!: TableColumn<T>;
  @Input({ required: true }) row!: T;

  @Output() valueChanged = new EventEmitter<{ row: T; key: string; newValue: unknown }>();

  get displayValue(): unknown {
    if (this.column.valueGetter) {
      return this.column.valueGetter(this.row);
    }
    return (this.row as Record<string, unknown>)[this.column.key];
  }

  onSelectionChange(newValue: unknown): void {
    this.valueChanged.emit({ row: this.row, key: this.column.key, newValue });
  }
}
