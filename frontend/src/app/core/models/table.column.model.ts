import { TableSelectOption } from './layout.model';

export type CellType = 'text' | 'dropdown' | 'switch';

export interface TableColumn<T> {
  key: string;
  label: string;
  type: CellType;
  options?: TableSelectOption[];
  valueGetter?: (row: T) => string;
}
