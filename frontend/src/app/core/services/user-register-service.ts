import { Injectable } from '@angular/core';
import { Observable, delay, of, throwError } from 'rxjs';
import { UserRegisterRequest } from '../models/user-register.model';

@Injectable({
  providedIn: 'root'
})
export class UserRegisterService {
  
  register(userData:UserRegisterRequest): Observable<unknown> {
 
    
    if (userData.email === 'test@test.com') {
      return throwError(() => ({ error: 'This email is already registered.' })).pipe(delay(500));
    }

    return of({ status: 'success', user: userData }).pipe(delay(1000));
  }
}