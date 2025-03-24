import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-vendor',
  standalone: true,
  imports: [ReactiveFormsModule, HttpClientModule, CommonModule], // Import necessary modules
  templateUrl: './create-vendor.component.html',
  styleUrls: ['./create-vendor.component.css'],
})
export class CreateVendorComponent {
  vendorForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.vendorForm = this.fb.group({
      name: ['', Validators.required],
      number: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
      email: ['', [Validators.required, Validators.email]],
      address: ['', Validators.required],
      gender: ['', Validators.required],
      interval: ['', [Validators.required, Validators.min(1)]],
    });
  }

  onSubmit(): void {
    if (this.vendorForm.invalid) {
      alert('Please fill in all fields correctly.');
      return;
    }

    const newVendor = this.vendorForm.value;
    this.http.post('http://localhost:8080/vendors', newVendor).subscribe({
      next: () => {
        alert('Vendor added successfully');
        this.router.navigate(['/admin-page']);
      },
      error: (err) => {
        console.error('Error adding vendor:', err);
        alert('Failed to add vendor');
      },
    });
  }
}
