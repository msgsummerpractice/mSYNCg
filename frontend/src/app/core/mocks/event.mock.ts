import { Event as AppEvent, EventStatus, EventType } from '../models/event.model';
import { LocationEnum } from '../models/location.model';

const POSTER_PLACEHOLDER =
  'data:image/svg+xml;base64,' +
  btoa(`<svg xmlns="http://www.w3.org/2000/svg" width="800" height="450">
  <defs>
    <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#a01441"/>
      <stop offset="100%" stop-color="#3f1123"/>
    </linearGradient>
  </defs>
  <rect width="800" height="450" fill="url(#g)"/>
  <text x="50%" y="50%" fill="#ffffff" font-family="sans-serif" font-size="48" font-weight="600"
        text-anchor="middle" dominant-baseline="middle">mSYNCg Summer Summit</text>
</svg>`);

export const MOCK_EVENT: AppEvent = {
  id: 1,
  name: 'mSYNCg Summer Summit',
  description: `Join us for a full day of talks, workshops and networking with the whole mSYNCg community.

The morning is dedicated to product and engineering deep dives, while the afternoon focuses on hands-on workshops where you can build alongside our teams. We close the day with an informal get-together on the terrace.

Bring your laptop, your questions and your ideas — everything else is taken care of.`,
  type: EventType.INTERNAL,
  status: EventStatus.PUBLISHED,
  imageBase64: POSTER_PLACEHOLDER,
  location: LocationEnum.CLUJ_NAPOCA,
  startDate: new Date('2026-09-12T09:00:00'),
  endDate: new Date('2026-09-12T18:30:00'),
  registrationStart: new Date('2026-08-01T00:00:00'),
  registrationEnd: new Date('2026-09-05T23:59:00'),
  foodProvided: true,
};

export const MOCK_EVENTS: AppEvent[] = [
  MOCK_EVENT,
  {
    ...MOCK_EVENT,
    id: 2,
    name: 'Open Source Meetup',
    description:
      'An evening meetup for everyone interested in open source. Short lightning talks, plenty of discussion and a relaxed atmosphere.',
    type: EventType.EXTERNAL,
    status: EventStatus.DRAFT,
    location: LocationEnum.TIMISOARA,
    startDate: new Date('2026-10-03T18:00:00'),
    endDate: new Date('2026-10-03T21:00:00'),
    registrationStart: new Date('2026-09-01T00:00:00'),
    registrationEnd: new Date('2026-10-01T23:59:00'),
    foodProvided: false,
  },
];
