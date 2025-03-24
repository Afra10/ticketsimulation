import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private baseUrl = 'http://localhost:8080/tickets';

  constructor(private http: HttpClient) {}

  getPoolDetails(): Observable<any> {
    return this.http.get(`${this.baseUrl}/ticket-pool-details`);
  }

  getCustomers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/customers`);
  }

  getVendors(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/vendors`);
  }

  stopVendor(vendorId: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/stop-active-vendor/${vendorId}`, {});
  }

  stopCustomer(customerId: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/stop-active-customer/${customerId}`, {});
  }
}
