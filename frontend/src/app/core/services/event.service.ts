import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Event as AppEvent, EventDraftRequest, EventResponse } from '../models/event.model';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private readonly eventsUrl = `${environment.apiUrl}/events`;

  constructor(private readonly http: HttpClient) {}

  createDraft(event: EventDraftRequest): Observable<EventResponse> {
    return this.http.post<EventResponse>(`${this.eventsUrl}`, event);
  }

  updateDraft(id: number, event: EventDraftRequest): Observable<EventResponse> {
    return this.http.put<EventResponse>(`${this.eventsUrl}/${id}`, event);
  }

  getEventById(id: number): Observable<AppEvent> {
    return this.http.get<AppEvent>(`${this.eventsUrl}/${id}`);
  }
}
