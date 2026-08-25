import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface CheckInRequest {
  value: string;
}

export interface CheckInResponse {
  message?: string;
}

@Injectable({
  providedIn: 'root',
})
export class CheckInService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  checkIn(code: string): Observable<CheckInResponse> {
    const url = `${this.apiUrl}/check-in`;
    const payload: CheckInRequest = { value: code };
    return this.http.post<CheckInResponse>(url, payload);
  }
}
