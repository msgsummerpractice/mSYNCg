import { EventStatus } from '../constants/event-status.constant';
import { EventType } from '../constants/event-type.constant';
import { Location } from '../constants/location.constant';

export interface EventFilterParams {
  name: string;
  types: EventType[];
  statuses: EventStatus[];
  locations: Location[];
  startTime: string;
  pageId: number;
  pageSize: number;
}
