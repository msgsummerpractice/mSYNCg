import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

import { LoginRequest, LoginResponse } from '../models/user-login.model';
import { UserRegisterRequest, UserRegisterResponse } from '../models/user-register.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/users`;

  constructor(private readonly http: HttpClient) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials);
  }

  register(userData: UserRegisterRequest): Observable<UserRegisterResponse> {
    return this.http.post<UserRegisterResponse>(this.apiUrl, userData);
  }
}
