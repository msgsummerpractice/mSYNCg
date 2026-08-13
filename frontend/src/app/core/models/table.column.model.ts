export type CellType = 'text' | 'dropdown';

export interface TableColumn<T> {
  key: string;
  label: string;
  type: CellType;
  options?: string[];
  valueGetter?: (row: T) => string;
}
