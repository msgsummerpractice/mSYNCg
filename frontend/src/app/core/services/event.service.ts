import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EventDraftRequest, EventResponse } from '../models/event.model';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  createDraft(event: EventDraftRequest): Observable<EventResponse> {
    return this.http.post<EventResponse>(`${this.apiUrl}/events`, event);
  }

  updateDraft(id: number, event: EventDraftRequest): Observable<EventResponse> {
    return this.http.put<EventResponse>(`${this.apiUrl}/events/${id}`, event);
  }
}