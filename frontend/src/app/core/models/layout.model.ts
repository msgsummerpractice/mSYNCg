import { UserRole } from '../constants/role.constant';
import { User } from './user.model';

export type CellChangeEvent<T, K extends string, V> = {
  row: T;
  key: K;
  oldValue: V;
  newValue: V;
};

export interface TableSelectOption<T = unknown> {
  value: T;
  label: string;
}

export type UserCellChangeEvent =
  CellChangeEvent<User, 'role', UserRole> | CellChangeEvent<User, 'status', boolean>;
