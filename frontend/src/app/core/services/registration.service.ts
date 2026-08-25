import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  Event,
  EventDraftRequest,
  EventRegisterResponse,
  EventResponse,
  EventView,
  EventRegisterRequest,
} from '../models/event.model';
import { PageResponse } from '../models/page.model';
import { EventFilterParams } from '../models/event.model';
import { formatDateTime, parseDateTime } from '../utils/date.util';
import { DeleteRegistrationRequest } from '../models/registration.model';

@Injectable({
  providedIn: 'root',
})
export class RegistrationService {
  private readonly http = inject(HttpClient);

  private readonly eventsUrl = `${environment.apiUrl}/events`;

  registerForEvent(request: EventRegisterRequest): Observable<EventRegisterResponse> {
    return this.http.post<EventRegisterResponse>(`${environment.apiUrl}/registrations`, request);
  }

  getRegistration(eventId: number, userId: number): Observable<EventRegisterResponse> {
    const params = new HttpParams()
      .set('eventId', eventId.toString())
      .set('userId', userId.toString());
    return this.http.get<EventRegisterResponse>(`${environment.apiUrl}/registrations`, { params });
  }

  updateRegistration(request: EventRegisterRequest): Observable<EventRegisterResponse> {
    return this.http.put<EventRegisterResponse>(`${environment.apiUrl}/registrations`, request);
  }

  withdrawRegistration(request: DeleteRegistrationRequest): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/registrations`, { body: request });
  }
}
