import { EventStatusEnum } from '../constants/event-status.constant';
import { EventTypeEnum } from '../constants/event-type.constant';
import { LocationEnum } from './location.model';

export interface EventFilterParams {
  name: string;
  types: EventTypeEnum[];
  statuses: EventStatusEnum[];
  locations: LocationEnum[];
  startTime: string;
  pageId: number;
  pageSize: number;
}
