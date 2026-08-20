import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  Event,
  EventDraftRequest,
  EventResponse,
  PageResponse,
  EventView,
} from '../models/event.model';
import { EventFilterParams } from '../models/event-filter.model';
import { formatDateTime, parseDateTime } from '../utils/date.util';

type DateTimeField = 'startTime' | 'endTime' | 'registrationStart' | 'registrationEnd';

type EventPayload = Omit<Event, DateTimeField> & Record<DateTimeField, string>;

type EventDraftPayload = Omit<EventDraftRequest, DateTimeField> & Record<DateTimeField, string>;

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = `${environment.apiUrl}/events`;

  getEvents(filters: EventFilterParams): Observable<PageResponse<EventView>> {
    let params = new HttpParams().set('page', filters.pageId).set('size', filters.pageSize);

    if (filters.name) {
      params = params.set('name', filters.name);
    }

    if (filters.startTime) {
      params = params.set('startTime', filters.startTime);
    }

    filters.types.forEach((type) => {
      params = params.append('type', type);
    });

    filters.statuses.forEach((status) => {
      params = params.append('status', status);
    });

    filters.locations.forEach((location) => {
      params = params.append('location', location);
    });

    return this.http.get<PageResponse<EventView>>(this.apiUrl, { params });
  }

  getEvent(id: number): Observable<Event> {
    return this.http
      .get<EventPayload>(`${this.apiUrl}/${id}`)
      .pipe(map((payload) => this.toEvent(payload)));
  }

  createDraft(event: EventDraftRequest): Observable<EventResponse> {
    return this.http.post<EventResponse>(this.apiUrl, this.toDraftPayload(event));
  }

  updateDraft(id: number, event: EventDraftRequest): Observable<EventResponse> {
    return this.http.put<EventResponse>(`${this.apiUrl}/${id}`, this.toDraftPayload(event));
  }

  private toEvent(payload: EventPayload): Event {
    return {
      ...payload,
      startTime: parseDateTime(payload.startTime),
      endTime: parseDateTime(payload.endTime),
      registrationStart: parseDateTime(payload.registrationStart),
      registrationEnd: parseDateTime(payload.registrationEnd),
    };
  }

  private toDraftPayload(event: EventDraftRequest): EventDraftPayload {
    return {
      ...event,
      startTime: formatDateTime(event.startTime),
      endTime: formatDateTime(event.endTime),
      registrationStart: formatDateTime(event.registrationStart),
      registrationEnd: formatDateTime(event.registrationEnd),
    };
  }
}
