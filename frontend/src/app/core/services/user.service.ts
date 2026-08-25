import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import type { EditProfileRequest, EditProfileResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http: HttpClient = inject(HttpClient);
  private readonly userUrl = `${environment.apiUrl}/user`;

  updateUserProfile(userId: number, data: EditProfileRequest): Observable<EditProfileResponse> {
    return this.http.put<EditProfileResponse>(`${this.userUrl}/${userId}`, data);
  }

  getUserProfile(userId: number): Observable<EditProfileResponse> {
    return this.http.get<EditProfileResponse>(`${this.userUrl}/${userId}`);
  }
}
