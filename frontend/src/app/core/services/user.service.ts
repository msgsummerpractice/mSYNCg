import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, tap } from 'rxjs';
import type { EditProfileRequest, EditProfileResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http: HttpClient = inject(HttpClient);
  private readonly userUrl = `${environment.apiUrl}/users`;

  readonly profileImageUrlSignal = signal<string>('');

  updateUserProfile(userId: number, data: EditProfileRequest): Observable<EditProfileResponse> {
    return this.http.put<EditProfileResponse>(`${this.userUrl}/${userId}/profile`, data).pipe(
      tap((profile) => this.setProfileImage(profile.imageMimeType, profile.imageBase64))
    );
  }

  getUserProfile(userId: number): Observable<EditProfileResponse> {
    return this.http.get<EditProfileResponse>(`${this.userUrl}/${userId}/profile`).pipe(
      tap((profile) => this.setProfileImage(profile.imageMimeType, profile.imageBase64))
    );
  }

  setProfileImage(imageMimeType: string | null, imageBase64: string | null): void {
    this.profileImageUrlSignal.set(
      imageMimeType && imageBase64 ? `data:${imageMimeType};base64,${imageBase64}` : ''
    );
  }
}
