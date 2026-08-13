import { inject, Injectable } from '@angular/core';
import { Observable, delay, of, throwError } from 'rxjs';
import { UserRegisterRequest, UserRegisterResponse } from '../models/user-register.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class UserRegisterService {
  private readonly _http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/users';
  register(userData: UserRegisterRequest): Observable<UserRegisterResponse> {
    return this._http.post<UserRegisterResponse>(this.apiUrl, userData);
  }
}
