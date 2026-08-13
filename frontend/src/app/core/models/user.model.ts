export interface User {
  firstName: string;
  lastName: string;
  email: string;
  location: 'Cluj-Napoca' | 'Targu Mures' | 'Timisoara';
  role: 'Admin' | 'Marketing Organizer' | 'Participant' | 'HR User';
  status: boolean;
}
