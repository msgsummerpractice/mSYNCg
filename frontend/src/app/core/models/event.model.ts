import { EventStatus } from '../constants/event-status.constant';
import { EventType } from '../constants/event-type.constant';
import { Location } from '../constants/location.constant';

export interface Event {
  id: number;
  name: string;
  startTime: string;
  status: EventStatus;
  type: EventType;
  location: Location;
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
