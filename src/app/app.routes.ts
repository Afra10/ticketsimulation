import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ConfigurationComponent } from './configuration/configuration.component';
import { AdminComponent } from './admin/admin.component';
import { TransactionComponent } from './transaction/transaction.component';
import { NavBarComponent } from './navbar/navbar.component';
import { CreateCustomerComponent } from './create-customer/create-customer.component';
import { UpdateCustomerComponent } from './update-customer/update-customer.component';
import { CreateVendorComponent } from './create-vendor/create-vendor.component';
import { UpdateVendorComponent } from './update-vendor/update-vendor.component';
import { ViewLiveLogsComponent } from './view-live-logs/view-live-logs.component';

export const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'view-live-logs', component: ViewLiveLogsComponent },
  { path: 'view-live-logs', component: NavBarComponent },
  { path: 'configuration', component: ConfigurationComponent },
  { path: 'admin-page', component: AdminComponent },
  { path: 'transaction', component: TransactionComponent },
  { path: 'create-customer', component: CreateCustomerComponent },
  { path: 'update-customer/:customerId', component: UpdateCustomerComponent },
  { path: 'create-vendor', component: CreateVendorComponent },
  { path: 'update-vendor/:vendorId', component: UpdateVendorComponent }
]
