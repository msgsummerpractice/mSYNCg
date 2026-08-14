export interface UserFilterParams {
  name: string;
  email: string;
  roles: string[];
  locations: string[];
  statuses: boolean[];
  page: number;
  size: number;
}
