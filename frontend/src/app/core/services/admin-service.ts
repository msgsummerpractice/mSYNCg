import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http: HttpClient = inject(HttpClient);

  private apiUrl: string = 'http://localhost:8080/admin/users';

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  updateUserRole(userId: string, newRole: User['role']): Observable<User> {
    const url = `${this.apiUrl}/${userId}/role`;
    return this.http.patch<User>(url, { role: newRole });
  }

  updateUserStatus(userId: string, newStatus: User['status']): Observable<User> {
    const url = `${this.apiUrl}/${userId}/status`;
    return this.http.patch<User>(url, { status: newStatus });
  }
}
