import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse } from '../models/user-login.model';
import { UserRegisterRequest, UserRegisterResponse } from '../models/user-register.model';
import { UserRole } from '../constants/role.constant';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}`;

  constructor(private readonly http: HttpClient) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, credentials);
  }

  register(userData: UserRegisterRequest): Observable<UserRegisterResponse> {
    return this.http.post<UserRegisterResponse>(`${this.apiUrl}/users`, userData);
  }

  getCurrentUserRole(): UserRole | null {
    if (typeof localStorage === 'undefined') {
      return null;
    }

    const token = localStorage.getItem('accessToken');

    if (!token) {
      return null;
    }

    const payload = this.decodeTokenPayload(token);
    // Spring authorities may be prefixed with ROLE_.
    const role = String(payload?.['role'] ?? '').replace(/^ROLE_/, '');

    return Object.values(UserRole).includes(role as UserRole) ? (role as UserRole) : null;
  }

  private decodeTokenPayload(token: string): Record<string, unknown> | null {
    const payloadSegment = token.split('.')[1];

    if (!payloadSegment) {
      return null;
    }

    try {
      const base64 = payloadSegment.replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(base64)) as Record<string, unknown>;
    } catch {
      return null;
    }
  }
}
