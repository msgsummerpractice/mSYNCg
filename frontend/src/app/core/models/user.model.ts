export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  location: string;
  role: 'ADMIN' | 'MARKETING_ORGANIZER' | 'PARTICIPANT' | 'HR_USER';
}
