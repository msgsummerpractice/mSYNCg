//Used only for testing should be delted and replaced with actual data from backend
import { User } from '../models/user.model';

export const MOCK_USERS: User[] = [
  {
    firstName: 'Ion',
    lastName: 'Popescu',
    email: 'ion.popescu@example.com',
    role: 'Admin',
    location: 'Cluj-Napoca',
    status: true,
  },
  {
    firstName: 'Maria',
    lastName: 'Ionescu',
    email: 'maria.ionescu@example.com',
    role: 'HR User',
    location: 'Cluj-Napoca',
    status: true,
  },
  {
    firstName: 'Andrei',
    lastName: 'Mureșan',
    email: 'andrei.muresan@example.com',
    role: 'Participant',
    location: 'Cluj-Napoca',
    status: true,
  },
  {
    firstName: 'Elena',
    lastName: 'Dumitrescu',
    email: 'elena.dumitrescu@example.com',
    role: 'Marketing Organizer',
    location: 'Timisoara',
    status: true,
  },
  {
    firstName: 'Mihai',
    lastName: 'Stan',
    email: 'mihai.stan@example.com',
    role: 'Participant',
    location: 'Targu Mures',
    status: true,
  },
  {
    firstName: 'Ana',
    lastName: 'Radu',
    email: 'ana.radu@example.com',
    role: 'HR User',
    location: 'Timisoara',
    status: true,
  },
  {
    firstName: 'George',
    lastName: 'Enescu',
    email: 'george.enescu@example.com',
    role: 'Admin',
    location: 'Cluj-Napoca',
    status: true,
  },
  {
    firstName: 'Cristina',
    lastName: 'Neagu',
    email: 'cristina.neagu@example.com',
    role: 'Marketing Organizer',
    location: 'Targu Mures',
    status: true,
  },
];
