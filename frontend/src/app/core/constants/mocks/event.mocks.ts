// Used only for testing. Should be deleted and replaced with actual data from backend.

import { EventStatus } from '../event-status.constant';
import { EventType } from '../event-type.constant';
import { Location } from '../location.constant';
import { Event } from '../../models/event.model';

export const MOCK_EVENTS: Event[] = [
  {
    id: 1,
    name: 'Summer Party',
    startTime: '2026-08-20T18:00:00',
    status: EventStatus.PUBLISHED,
    type: EventType.INTERNAL,
    location: Location.CLUJ_NAPOCA,
  },
  {
    id: 2,
    name: 'Tech Conference',
    startTime: '2026-09-10T09:30:00',
    status: EventStatus.DRAFT,
    type: EventType.EXTERNAL,
    location: Location.TIMISOARA,
  },
  {
    id: 3,
    name: 'Autumn Meetup',
    startTime: '2026-10-05T17:00:00',
    status: EventStatus.COMPLETED,
    type: EventType.LOCAL,
    location: Location.TARGU_MURES,
  },
  {
    id: 4,
    name: 'Team Building',
    startTime: '2026-10-15T10:00:00',
    status: EventStatus.PUBLISHED,
    type: EventType.INTERNAL,
    location: Location.TIMISOARA,
  },
  {
    id: 5,
    name: 'Christmas Celebration',
    startTime: '2026-12-18T18:30:00',
    status: EventStatus.DRAFT,
    type: EventType.LOCAL,
    location: Location.CLUJ_NAPOCA,
  },
];
