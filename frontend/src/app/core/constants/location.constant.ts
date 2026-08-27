export enum UserLocation {
  CLUJ_NAPOCA = 'Cluj-Napoca',
  TARGU_MURES = 'Târgu Mureș',
  TIMISOARA = 'Timișoara',
}

export const USER_LOCATION_TRANSLATION_KEYS: Record<UserLocation, string> = {
  [UserLocation.TARGU_MURES]: 'USER_LIST.LOCATIONS.TARGU_MURES',
  [UserLocation.CLUJ_NAPOCA]: 'USER_LIST.LOCATIONS.CLUJ_NAPOCA',
  [UserLocation.TIMISOARA]: 'USER_LIST.LOCATIONS.TIMISOARA',
};

export enum EventLocation {
  CLUJ_NAPOCA = 'CLUJ_NAPOCA',
  TARGU_MURES = 'TARGU_MURES',
  TIMISOARA = 'TIMISOARA',
  ALL = 'ALL',
}

export const EVENT_LOCATION_TRANSLATION_KEYS: Record<EventLocation, string> = {
  [EventLocation.TARGU_MURES]: 'EVENT_LIST.LOCATIONS.TARGU_MURES',
  [EventLocation.CLUJ_NAPOCA]: 'EVENT_LIST.LOCATIONS.CLUJ_NAPOCA',
  [EventLocation.TIMISOARA]: 'EVENT_LIST.LOCATIONS.TIMISOARA',
  [EventLocation.ALL]: 'EVENT_LIST.LOCATIONS.ALL',
};
