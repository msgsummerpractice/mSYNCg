import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { Page } from '../models/page.model';
import { UserFilterParams } from '../models/user-filters.model';
import { environment } from '../../../environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { UserRole } from '../constants/role.constant';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http: HttpClient = inject(HttpClient);

  private readonly adminUrl = `${environment.apiUrl}/admin`;

  getUsers(filters: UserFilterParams): Observable<Page<User>> {
    let params = new HttpParams().set('page', filters.pageId).set('size', filters.pageSize);
    if (filters.firstName) params = params.set('firstName', filters.firstName);
    if (filters.lastName) params = params.set('lastName', filters.lastName);
    if (filters.email) params = params.set('email', filters.email);

    filters.roles.forEach((role) => {
      params = params.append('role', role);
    });

    filters.locations.forEach((location) => {
      params = params.append('location', location);
    });

    filters.statuses.forEach((status) => {
      params = params.append('status', status.toString());
    });

    return this.http.get<Page<User>>(`${this.adminUrl}/users`, { params });
  }

  updateUserRole(userId: number, newRole: UserRole): Observable<User> {
    const url = `${this.adminUrl}/users/${userId}/role`;
    return this.http.patch<User>(url, { userRole: newRole });
  }

  updateUserStatus(userId: number, newStatus: boolean): Observable<User> {
    const url = `${this.adminUrl}/users/${userId}/status`;
    return this.http.patch<User>(url, { status: newStatus });
  }
}
