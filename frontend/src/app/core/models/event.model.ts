import { LocationEnum } from './location.model';

export interface Event {
  id: number;
  name: string;
  description: string;
  type: EventType;
  status: EventStatus;
  imageBase64: string;
  location: LocationEnum;
  startDate: Date;
  endDate: Date;
  registrationStart: Date;
  registrationEnd: Date;
  foodProvided: boolean;
}

export enum EventStatus {
  DRAFT = 'Draft',
  PUBLISHED = 'Published',
  COMPLETED = 'Completed',
}

export enum EventType {
  INTERNAL = 'Internal',
  EXTERNAL = 'External',
  LOCAL = 'Local',
}
