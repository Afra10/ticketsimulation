import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-update-customer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './update-customer.component.html',
  styleUrls: ['./update-customer.component.css'],
})
export class UpdateCustomerComponent implements OnInit {
  customerForm: FormGroup;
  customerId!: string;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {
    this.customerForm = this.fb.group({
      name: ['', Validators.required],
      number: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
      email: ['', [Validators.required, Validators.email]],
      address: ['', Validators.required],
      gender: ['', Validators.required],
      interval: ['', [Validators.required, Validators.min(1)]],
      specialAccess: [false],
    });
  }

  ngOnInit(): void {
    this.customerId = this.route.snapshot.paramMap.get('customerId')!;
    if (this.customerId) {
      this.fetchCustomer();
    }
  }

  fetchCustomer(): void {
    this.http.get<any>(`http://localhost:8080/customers/${this.customerId}`).subscribe({
      next: (customer) => {
        this.customerForm.patchValue(customer);
      },
      error: (err) => {
        console.error('Error fetching customer:', err);
        alert('Failed to load customer data');
      },
    });
  }

  onSubmit(): void {
    if (this.customerForm.invalid) {
      alert('Please fill in all fields correctly.');
      return;
    }

    this.http
      .put(`http://localhost:8080/customers/${this.customerId}`, this.customerForm.value)
      .subscribe({
        next: () => {
          alert('Customer updated successfully');
          this.router.navigate(['/admin-page']);
        },
        error: (err) => {
          console.error('Error updating customer:', err);
          alert('Failed to update customer');
        },
      });
  }
}
