import { Component, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavBarComponent {
  isStarted = false;

  constructor(private http: HttpClient, private ngZone: NgZone) {}

  handleStart() {
    if (this.isStarted) return;
    this.isStarted = true
    this.http.post('http://localhost:8080/tickets/start-ticket-simulation', {}).subscribe({
      next: () => {
        this.ngZone.run(() => (this.isStarted = true)); // Ensure UI updates
      },
      error: (err) => console.error('Error starting service:', err),
    });
  }

  handleStop() {
    if (!this.isStarted) return;
    this.isStarted = false;
    this.http.post('http://localhost:8080/tickets/stop-ticket-simulation', {}).subscribe({
      next: () => {
        this.ngZone.run(() => (this.isStarted = false)); // Ensure UI updates
      },
      error: (err) => console.error('Error stopping service:', err),
    });
  }

  handleReset() {
    if (this.isStarted) return;
    this.http.post('http://localhost:8080/tickets/reset-ticket-pool', {}).subscribe({
      next: () => {
        this.ngZone.run(() => (this.isStarted = false)); // Ensure UI updates
      },
      error: (err) => console.error('Error resetting service:', err),
    });
  }
}
