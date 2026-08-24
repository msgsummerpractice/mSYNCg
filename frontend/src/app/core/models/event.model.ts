import { EventStatusEnum, EventTypeEnum } from '../constants/event.constant';
import { FormControl } from '@angular/forms';
import { LocationEnum } from './location.model';
import { EventLocation } from '../constants/location.constant';



export const EVENT_TYPES = Object.values(EventTypeEnum);

export enum EventParticipationStatus {
  REGISTERED = 'REGISTERED',
  CHECKED_IN = 'CHECKED_IN',
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
  endTime: string;
  status: EventStatusEnum;
  type: EventTypeEnum;
  location: LocationEnum;
  participationStatus?: EventParticipationStatus;
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
  qrCode?: string | null;
  code?: string | null;
}

export interface EventCodesResponse {
  qrCode: string;
  code: string;
}

export interface EventResponse {
  id: number;
  status: EventStatusEnum;
}

export interface EventFilterParams {
  name: string;
  types: EventTypeEnum[];
  statuses: EventStatusEnum[];
  locations: EventLocation[];
  startTime: string;
  pageId: number;
  pageSize: number;
}
export { EventStatusEnum };
