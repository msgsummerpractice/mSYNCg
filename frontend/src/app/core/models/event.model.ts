import { EventStatusEnum } from '../constants/event-status.constant';
import { EventTypeEnum } from '../constants/event-type.constant';
import { FormControl } from '@angular/forms';
import { LocationEnum } from './location.model';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export const EVENT_TYPES = Object.values(EventTypeEnum);

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
  startTime: Date | null;
  endTime: Date | null;
  registrationStart: Date | null;
  registrationEnd: Date | null;
  type: EventTypeEnum;
  location: LocationEnum;
  foodProvided: boolean;
  image: string | null;
  status: EventStatusEnum;
}

export interface EventView {
  id: number;
  name: string;
  startTime: string;
  status: EventStatusEnum;
  type: EventTypeEnum;
  location: LocationEnum;
}

export interface Event {
  id: number;
  name: string;
  description: string;
  type: EventTypeEnum;
  status: EventStatusEnum;
  image: string | null;
  location: LocationEnum;
  startTime: Date | null;
  endTime: Date | null;
  registrationStart: Date | null;
  registrationEnd: Date | null;
  foodProvided: boolean | null;
}

export interface EventResponse {
  id: number;
  status: EventStatusEnum;
}
export { EventStatusEnum, EventTypeEnum };
