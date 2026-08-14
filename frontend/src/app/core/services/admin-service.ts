import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { Page } from '../models/page.model';
import { UserFilterParams } from '../models/user-filters.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http: HttpClient = inject(HttpClient);

  private readonly apiUrl = `${environment.apiUrl}`;

  getUsers(filters: UserFilterParams): Observable<Page<User>> {
    let params = new HttpParams().set('page', filters.page).set('size', filters.size);
    if (filters.name) params = params.set('name', filters.name);
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

    return this.http.get<Page<User>>(this.apiUrl, { params });
  }
}
