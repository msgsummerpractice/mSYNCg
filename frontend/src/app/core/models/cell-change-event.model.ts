export type CellChangeEvent<T, K extends string, V> = {
  row: T;
  key: K;
  oldValue: V;
  newValue: V;
};
