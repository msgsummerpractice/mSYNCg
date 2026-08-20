import { EventStatusEnum, EventTypeEnum } from '../constants/event.constant';
import { EventLocation } from '../constants/location.constant';

export interface EventFilterParams {
  name: string;
  types: EventTypeEnum[];
  statuses: EventStatusEnum[];
  locations: EventLocation[];
  startTime: string;
  pageId: number;
  pageSize: number;
}
