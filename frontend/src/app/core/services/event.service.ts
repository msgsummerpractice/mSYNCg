import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Event, PageResponse } from '../models/event.model';
import { EventFilterParams } from '../models/event-filter.model';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = `${environment.apiUrl}/events`;

  getEvents(filters: EventFilterParams): Observable<PageResponse<Event>> {
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

    return this.http.get<PageResponse<Event>>(this.apiUrl, { params });
  }
}
