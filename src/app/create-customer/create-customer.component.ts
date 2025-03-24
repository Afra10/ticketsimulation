import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http'; // Import HttpClient and HttpClientModule

@Component({
  selector: 'app-create-customer',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, HttpClientModule], // Add HttpClientModule here
  templateUrl: './create-customer.component.html',
  styleUrls: ['./create-customer.component.css'],
})
export class CreateCustomerComponent {
  customerForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.customerForm = this.fb.group({
      name: ['', [Validators.required]],
      number: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
      email: ['', [Validators.required, Validators.email]],
      address: ['', [Validators.required]],
      gender: ['', [Validators.required]],
      interval: ['', [Validators.required, Validators.min(1)]],
      specialAccess: [false],
    });
  }

  onSubmit(): void {
    if (this.customerForm.invalid) {
      alert('Please fill in all fields correctly.');
      return;
    }

    const newCustomer = this.customerForm.value;
    this.http.post('http://localhost:8080/customers', newCustomer).subscribe({
      next: () => {
        alert('Customer added successfully');
        this.router.navigate(['/admin-page']);
      },
      error: (err) => {
        console.error('Error adding customer:', err);
        alert('Failed to add customer');
      },
    });
  }
}
