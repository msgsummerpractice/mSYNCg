import { inject, Injectable } from '@angular/core';
import { Observable, delay, of, throwError } from 'rxjs';
import { UserRegisterRequest, UserRegisterResponse } from '../models/user-register.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserRegisterService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/users`;
  register(userData: UserRegisterRequest): Observable<UserRegisterResponse> {
    return this.http.post<UserRegisterResponse>(this.apiUrl, userData);
  }
}
