import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Event, EventDraftRequest, EventResponse } from '../models/event.model';
import { formatDateTime, parseDateTime } from '../utils/date.util';

type DateTimeField = 'startTime' | 'endTime' | 'registrationStart' | 'registrationEnd';

type EventPayload = Omit<Event, DateTimeField> & Record<DateTimeField, string>;
type EventDraftPayload = Omit<EventDraftRequest, DateTimeField> & Record<DateTimeField, string>;

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private readonly eventsUrl = `${environment.apiUrl}/events`;

  constructor(private readonly http: HttpClient) {}

  getEvent(id: number): Observable<Event> {
    return this.http
      .get<EventPayload>(`${this.eventsUrl}/${id}`)
      .pipe(map((payload) => this.toEvent(payload)));
  }

  createDraft(event: EventDraftRequest): Observable<EventResponse> {
    return this.http.post<EventResponse>(`${this.eventsUrl}`, this.toDraftPayload(event));
  }

  updateDraft(id: number, event: EventDraftRequest): Observable<EventResponse> {
    return this.http.put<EventResponse>(`${this.eventsUrl}/${id}`, this.toDraftPayload(event));
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
