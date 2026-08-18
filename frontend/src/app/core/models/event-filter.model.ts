import { EventStatus } from '../constants/event-status.constant';
import { EventType } from '../constants/event-type.constant';
import { LocationEnum } from './location.model';

export interface EventFilterParams {
  name: string;
  types: EventType[];
  statuses: EventStatus[];
  locations: LocationEnum[];
  startTime: string;
  pageId: number;
  pageSize: number;
}
