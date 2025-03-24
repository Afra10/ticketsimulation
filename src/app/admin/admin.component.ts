import { Component } from '@angular/core';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, HttpClientModule], // Add CommonModule and RouterModule
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
})
export class AdminComponent {
  vendors: any[] = [];
  customers: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchVendors();
    this.fetchCustomers();
  }

  fetchVendors(): void {
    this.http.get('http://localhost:8080/vendors').subscribe({
      next: (data: any) => (this.vendors = data),
      error: (err) => console.error('Error fetching vendors:', err),
    });
  }

  fetchCustomers(): void {
    this.http.get('http://localhost:8080/customers').subscribe({
      next: (data: any) => (this.customers = data),
      error: (err) => console.error('Error fetching customers:', err),
    });
  }

  handleDelete(entity: string, entityId: number, type: string): void {
    const isConfirmed = window.confirm(`Are you sure you want to delete this ${type}?`);
    if (isConfirmed) {
      this.http.delete(`http://localhost:8080/${entity}s/${entityId}`).subscribe({
        next: () => {
          alert(`${type} deleted`);
          this.fetchVendors();
          this.fetchCustomers();
        },
        error: (err) => {
          console.error(`Error deleting ${type}:`, err);
          alert(`Failed to delete ${type}`);
        },
      });
    }
  }
}
