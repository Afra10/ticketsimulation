import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-configuration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule], // Import required modules
  templateUrl: './configuration.component.html',
  styleUrls: ['./configuration.component.css'],
})
export class ConfigurationComponent {
  configForm: FormGroup;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.configForm = this.fb.group({
      totalTickets: ['', [Validators.required, Validators.min(1)]],
      ticketReleaseRate: ['', [Validators.required, Validators.min(1)]],
      customerRetrievalRate: ['', [Validators.required, Validators.min(1)]],
      maxTicketCapacity: ['', [Validators.required, Validators.min(1)]],
    });
  }

  ngOnInit(): void {
    this.fetchConfig();
  }

  fetchConfig(): void {
    this.http.get<any>('http://localhost:8080/configuration').subscribe({
      next: (config) => {
        this.configForm.patchValue({
          totalTickets: config.totalTickets,
          ticketReleaseRate: config.ticketReleaseRate,
          customerRetrievalRate: config.customerRetrievalRate,
          maxTicketCapacity: config.maxTicketCapacity,
        });
      },
      error: (err) => {
        console.error('Error fetching configuration:', err);
      },
    });
  }

  saveConfig(): void {
    if (this.configForm.invalid) {
      alert('Please fill all required fields correctly.');
      return;
    }

    this.http.post('http://localhost:8080/configuration', this.configForm.value, {
      headers: { 'Content-Type': 'application/json' },
    }).subscribe({
      next: () => {
        alert('Configuration saved successfully');
      },
      error: (err) => {
        console.error('Error saving configuration:', err);
        alert('Failed to save configuration');
      },
    });
  }
}
