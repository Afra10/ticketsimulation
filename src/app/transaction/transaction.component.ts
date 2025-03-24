import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-transaction',
  standalone: true,
  imports: [CommonModule], // Import CommonModule here
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.css'],
})
export class TransactionComponent {
  histories: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchHistory();
  }

  fetchHistory(): void {
    this.http.get<any[]>('http://localhost:8080/transaction').subscribe({
      next: (data) => {
        this.histories = data;
      },
      error: (err) => {
        console.error('Error fetching history:', err);
      },
    });
  }

  formatDateTime(timestamp: string): string {
    const date = new Date(timestamp);
    return date.toLocaleString();
  }
}
