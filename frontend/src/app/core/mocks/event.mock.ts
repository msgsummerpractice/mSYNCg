import { Event as AppEvent, EventStatusEnum, EventTypeEnum } from '../models/event.model';
import { LocationEnum } from '../models/location.model';

const POSTER_PLACEHOLDER =
  'https://tse1.mm.bing.net/th/id/OIP.2Vf2Ci2rhp2H5LKUlRlojgHaKe?r=0&rs=1&pid=ImgDetMain&o=7&rm=3';

export const MOCK_EVENT: AppEvent = {
  id: 1,
  name: 'mSYNCg Summer Summit',
  description: `Join us for a full day of talks, workshops and networking with the whole mSYNCg community.

The morning is dedicated to product and engineering deep dives, while the afternoon focuses on hands-on workshops where you can build alongside our teams. We close the day with an informal get-together on the terrace.

Bring your laptop, your questions and your ideas — everything else is taken care of.`,
  type: EventTypeEnum.INTERNAL,
  status: EventStatusEnum.PUBLISHED,
  image: POSTER_PLACEHOLDER,
  location: LocationEnum.CLUJ_NAPOCA,
  startTime: new Date('2026-09-12T09:00:00'),
  endTime: new Date('2026-09-12T18:30:00'),
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
    type: EventTypeEnum.EXTERNAL,
    status: EventStatusEnum.DRAFT,
    location: LocationEnum.TIMISOARA,
    startTime: new Date('2026-10-03T18:00:00'),
    endTime: new Date('2026-10-03T21:00:00'),
    registrationStart: new Date('2026-09-01T00:00:00'),
    registrationEnd: new Date('2026-10-01T23:59:00'),
    foodProvided: false,
  },
];
