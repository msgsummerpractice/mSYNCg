//Used only for testing should be delted and replaced with actual data from backend
import { User } from '../models/user.model';

export const MOCK_USERS: User[] = [
  {
    firstName: 'Ion',
    lastName: 'Popescu',
    email: 'ion.popescu@example.com',
    role: 'ADMIN',
    location: 'CLUJ-NAPOCA',
    status: true,
  },
  {
    firstName: 'Maria',
    lastName: 'Ionescu',
    email: 'maria.ionescu@example.com',
    role: 'HR_USER',
    location: 'CLUJ-NAPOCA',
    status: true,
  },
  {
    firstName: 'Andrei',
    lastName: 'Mureșan',
    email: 'andrei.muresan@example.com',
    role: 'PARTICIPANT',
    location: 'CLUJ-NAPOCA',
    status: true,
  },
  {
    firstName: 'Elena',
    lastName: 'Dumitrescu',
    email: 'elena.dumitrescu@example.com',
    role: 'MARKETING_ORGANIZER',
    location: 'TIMISOARA',
    status: true,
  },
  {
    firstName: 'Mihai',
    lastName: 'Stan',
    email: 'mihai.stan@example.com',
    role: 'PARTICIPANT',
    location: 'TARGU-MURES',
    status: true,
  },
  {
    firstName: 'Ana',
    lastName: 'Radu',
    email: 'ana.radu@example.com',
    role: 'HR_USER',
    location: 'TIMISOARA',
    status: true,
  },
  {
    firstName: 'George',
    lastName: 'Enescu',
    email: 'george.enescu@example.com',
    role: 'ADMIN',
    location: 'CLUJ-NAPOCA',
    status: true,
  },
  {
    firstName: 'Cristina',
    lastName: 'Neagu',
    email: 'cristina.neagu@example.com',
    role: 'MARKETING_ORGANIZER',
    location: 'TARGU-MURES',
    status: true,
  },
];
