import { Injectable } from '@angular/core';
import { Observable, delay, of, throwError } from 'rxjs';
import { UserRegisterRequest, UserRegisterResponse } from '../models/user-register.model';

@Injectable({
  providedIn: 'root',
})
export class UserRegisterService {
  register(userData: UserRegisterRequest): Observable<UserRegisterResponse> {
    //To Do: Use http requests when connecting it to the BE
    const response: UserRegisterResponse = {
      id: Date.now(),
      firstName: userData.firstName,
      lastName: userData.lastName,
      email: userData.email,
      location: userData.location,
      status: true,
      imageUrlString: userData.imageBase64 ?? '',
      role: 'PARTICIPANT',
    };

    return of(response);
  }
}
