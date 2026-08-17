import { FormControl } from '@angular/forms';
import { EventTypeEnum } from './event-type.model';
import { LocationEnum } from './location.model';

export enum FoodProvidedEnum {
  YES = 'YES',
  NO = 'NO',
}

export enum EventStatusEnum {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  COMPLETED = 'COMPLETED',
}

export type EventForm = {
  title: FormControl<string>;
  description: FormControl<string>;
  startDate: FormControl<Date | null>;
  endDate: FormControl<Date | null>;
  type: FormControl<EventTypeEnum | null>;
  location: FormControl<LocationEnum | null>;
  foodProvided: FormControl<FoodProvidedEnum | null>;
};

export interface EventDraftRequest {
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  type: EventTypeEnum;
  location: LocationEnum;
  foodProvided: FoodProvidedEnum | null;
  posterBase64: string | null;
  status: EventStatusEnum.DRAFT;
}

export interface EventResponse {
  id: number;
  status: EventStatusEnum;
}
