import { EventStatus } from '../constants/event-status.constant';
import { EventType } from '../constants/event-type.constant';
import { FormControl } from '@angular/forms';
import { LocationEnum } from './location.model';

export interface Event {
  id: number;
  name: string;
  startTime: string;
  status: EventStatus;
  type: EventType;
  location: LocationEnum;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export enum EventTypeEnum {
  INTERNAL = 'INTERNAL',
  EXTERNAL = 'EXTERNAL',
  LOCAL = 'LOCAL',
}

export const EVENT_TYPES = Object.values(EventTypeEnum);

export enum EventStatusEnum {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  COMPLETED = 'COMPLETED',
}

export type EventForm = {
  title: FormControl<string>;
  description: FormControl<string>;
  startDate: FormControl<Date | null>;
  startTime: FormControl<Date | null>;
  endDate: FormControl<Date | null>;
  endTime: FormControl<Date | null>;
  registrationStartDate: FormControl<Date | null>;
  registrationStartTime: FormControl<Date | null>;
  registrationEndDate: FormControl<Date | null>;
  registrationEndTime: FormControl<Date | null>;
  type: FormControl<EventTypeEnum | null>;
  location: FormControl<LocationEnum | null>;
  isFoodProvided: FormControl<boolean | null>;
};

export interface EventDraftRequest {
  name: string;
  description: string;
  startTime: string;
  endTime: string;
  registrationStart: string;
  registrationEnd: string;
  type: EventTypeEnum;
  location: LocationEnum;
  foodProvided: boolean;
  image: string | null;
  status: EventStatusEnum.DRAFT;
}

export interface EventResponse {
  id: number;
  status: EventStatusEnum;
}
