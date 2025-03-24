import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-update-vendor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './update-vendor.component.html',
  styleUrls: ['./update-vendor.component.css'],
})
export class UpdateVendorComponent implements OnInit {
  vendorForm: FormGroup;
  vendorId!: string;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {
    this.vendorForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      number: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
      address: ['', Validators.required],
      gender: ['', Validators.required],
      interval: ['', [Validators.required, Validators.min(1)]],
    });
  }

  ngOnInit(): void {
    this.vendorId = this.route.snapshot.paramMap.get('vendorId')!;
    if (this.vendorId) {
      this.fetchVendorDetails();
    }
  }

  fetchVendorDetails(): void {
    this.http.get<any>(`http://localhost:8080/vendors/${this.vendorId}`).subscribe({
      next: (vendor) => {
        this.vendorForm.patchValue(vendor);
      },
      error: (err) => {
        console.error('Error fetching vendor details:', err);
        alert('Failed to load vendor details.');
      },
    });
  }

  onSubmit(): void {
    if (this.vendorForm.invalid) {
      alert('Please fill in all fields correctly.');
      return;
    }

    this.http.put(`http://localhost:8080/vendors/${this.vendorId}`, this.vendorForm.value).subscribe({
      next: () => {
        alert('Vendor updated successfully');
        this.router.navigate(['/admin-page']);
      },
      error: (err) => {
        console.error('Error updating vendor:', err);
        alert('Failed to update vendor.');
      },
    });
  }
}
