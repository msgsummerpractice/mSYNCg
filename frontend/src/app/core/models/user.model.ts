import { UserRole } from '../constants/role.constant';

export interface User {
  firstName: string;
  lastName: string;
  email: string;
  location: 'Cluj-Napoca' | 'Targu Mures' | 'Timisoara';
  role: UserRole;
  status: boolean;
}
