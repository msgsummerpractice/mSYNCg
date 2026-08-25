import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Event, EventDraftRequest, EventRegisterResponse, EventResponse, EventView, EventRegisterRequest } from '../models/event.model';
import { PageResponse } from '../models/page.model';
import { EventFilterParams } from '../models/event.model';
import { formatDateTime, parseDateTime } from '../utils/date.util';

@Injectable({
  providedIn: 'root',
})
export class RegistrationService {
  private readonly http = inject(HttpClient);

  private readonly eventsUrl = `${environment.apiUrl}/events`;

  registerForEvent(request: EventRegisterRequest): Observable<EventRegisterResponse> {
    return this.http.post<EventRegisterResponse>(`${environment.apiUrl}/registrations`, request);
  }
}
