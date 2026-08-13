export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  location: 'CLUJ-NAPOCA' | 'TARGU-MURES' | 'TIMISOARA';
  role: 'ADMIN' | 'MARKETING_ORGANIZER' | 'PARTICIPANT' | 'HR_USER';
  status?: 'active' | 'inactive';
}
