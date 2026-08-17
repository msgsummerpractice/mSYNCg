import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse } from '../models/user-login.model';
import { UserRegisterRequest, UserRegisterResponse } from '../models/user-register.model';
import { UserRole } from '../constants/role.constant';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'accessToken';
  private readonly apiUrl = environment.apiUrl;

  constructor(
    private readonly http: HttpClient,
    @Inject(PLATFORM_ID) private readonly platformId: object
  ) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(tap((response) => this.setToken(response.accessToken)));
  }

  register(userData: UserRegisterRequest): Observable<UserRegisterResponse> {
    return this.http.post<UserRegisterResponse>(`${this.apiUrl}/users`, userData);
  }

  getToken(): string | null {
    return isPlatformBrowser(this.platformId) ? localStorage.getItem(this.tokenKey) : null;
  }

  hasToken(): boolean {
    return !!this.getToken();
  }

  getRole(): UserRole | null {
    const token = this.getToken();
    const payload = this.decodeToken(token);

    if (!payload?.['role']) {
      return null;
    }

    const role = payload['role'].replace(/^ROLE_/, '') as UserRole;

    return Object.values(UserRole).includes(role) ? role : null;
  }

  hasRole(role: UserRole): boolean {
    return this.getRole() === role;
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    const payload = this.decodeToken(token);

    if (!payload?.['exp']) {
      return false;
    }

    const expirationTime = payload['exp'] * 1000;
    return Date.now() >= expirationTime;
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem(this.tokenKey);
    }
  }

  private setToken(token: string): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(this.tokenKey, token);
    }
  }

  private decodeToken(token: string | null): Record<string, any> | null {
    if (!token) {
      return null;
    }

    try {
      const payload = token.split('.')[1];

      if (!payload) {
        return null;
      }

      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');

      return JSON.parse(atob(padded));
    } catch {
      return null;
    }
  }

  hasValidSession(): boolean {
    const token = this.getToken();

    if (!token) {
      return false;
    }

    const payload = this.decodeToken(token);

    if (!payload) {
      return false;
    }

    return !this.isTokenExpired();
  }

  validateSession(): Observable<boolean> {
    return this.http.get<void>(`${this.apiUrl}/auth/validate`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }
}
