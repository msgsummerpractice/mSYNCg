import { EventStatusEnum } from '../constants/event-status.constant';
import { EventTypeEnum } from '../constants/event-type.constant';
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
