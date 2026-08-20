import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { TableColumn } from '../../../core/models/table.column.model';
import type { TableSelectOption } from '../../../core/models/table-select-option.model';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { USER_ROLE_DISPLAY_VALUES } from '../../../core/constants/role.constant';
import { CellChangeEvent } from '../../../core/models/cell-change-event.model';

@Component({
  selector: 'generic-cell-view',
  standalone: true,
  imports: [CommonModule, MatSelectModule, MatFormFieldModule, MatSlideToggleModule],
  template: `
    @switch (column.type) {
      @case ('dropdown') {
        <mat-form-field
          appearance="outline"
          subscriptSizing="dynamic"
          class="w-full min-w-0 max-w-full text-xs sm:min-w-[120px] sm:text-sm ![--mdc-outlined-text-field-outline-color:transparent] ![--mdc-outlined-text-field-hover-outline-color:transparent] ![--mdc-outlined-text-field-focus-outline-color:transparent] ![--mat-sys-outline:transparent] ![--mat-sys-outline-variant:transparent] [&_.mdc-notched-outline__leading]:!border-transparent [&_.mdc-notched-outline__notch]:!border-transparent [&_.mdc-notched-outline__trailing]:!border-transparent [&_.mat-mdc-select-value]:max-w-full [&_.mat-mdc-select-value-text]:truncate"
        >
          <mat-select [value]="displayValue" (selectionChange)="onSelectionChange($event.value)">
            @for (option of column.options; track option) {
              <mat-option [value]="option.value">{{ option.label }}</mat-option>
            }
          </mat-select>
        </mat-form-field>
      }

      @case ('switch') {
        <mat-slide-toggle [checked]="!!displayValue" (change)="onSwitchChange($event.checked)">
        </mat-slide-toggle>
      }

      @default {
        <span>{{ displayValue }}</span>
      }
    }
  `,
  styles: [],
})
export class GenericCellView<T> {
  @Input({ required: true }) column!: TableColumn<T>;
  @Input({ required: true }) row!: T;

  @Output() valueChanged = new EventEmitter<CellChangeEvent<T, string, unknown>>();

  get displayValue(): unknown {
    if (this.column.valueGetter) {
      return this.column.valueGetter(this.row);
    }
    return (this.row as Record<string, unknown>)[this.column.key];
  }

  onSelectionChange(newValue: unknown): void {
    this.valueChanged.emit({
      row: this.row,
      key: this.column.key,
      oldValue: this.displayValue,
      newValue,
    });
  }
  onSwitchChange(newValue: boolean): void {
    this.valueChanged.emit({
      row: this.row,
      key: this.column.key,
      oldValue: this.displayValue,
      newValue,
    });
  }
}
