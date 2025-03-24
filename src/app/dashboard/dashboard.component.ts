import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgApexchartsModule } from 'ng-apexcharts';
import {
  ChartComponent,
  ApexChart,
  ApexNonAxisChartSeries,
  ApexResponsive,
} from 'ng-apexcharts';
import { DashboardService } from './dashboard.service';

export type ChartOptions = {
  series: ApexNonAxisChartSeries;
  chart: ApexChart;
  labels: string[];
  responsive: ApexResponsive[];
};

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NgApexchartsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  totalTickets = 0;
  maximumPoolSize = 0;
  availableTicketsInPool = 0;
  ticketsInserted = 0;

  ticketStatusChartOptions: Partial<ChartOptions> = {};
  poolStatusChartOptions: Partial<ChartOptions> = {};

  activeCustomers: any[] = [];
  activeVendors: any[] = [];

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.initializeCharts();
    this.fetchPoolDetails();
    setInterval(() => this.fetchPoolDetails(), 1000);
  }

  initializeCharts(): void {
    this.ticketStatusChartOptions = {
      series: [0, 0],
      chart: {
        type: 'donut',
        height: 350,
      },
      labels: ['Tickets Added', 'Tickets Remaining'],
      responsive: [
        {
          breakpoint: 480,
          options: {
            chart: {
              width: 300,
            },
            legend: {
              position: 'bottom',
            },
          },
        },
      ],
    };

    this.poolStatusChartOptions = {
      series: [0, 0],
      chart: {
        type: 'donut',
        height: 350,
      },
      labels: ['Tickets in Pool', 'Tickets Available'],
      responsive: [
        {
          breakpoint: 480,
          options: {
            chart: {
              width: 300,
            },
            legend: {
              position: 'bottom',
            },
          },
        },
      ],
    };
  }

  fetchPoolDetails(): void {
    this.dashboardService.getPoolDetails().subscribe((data) => {
      this.totalTickets = data.totalTickets;
      this.maximumPoolSize = data.maximumPoolSize;
      this.availableTicketsInPool = data.availableTicketsInPool;
      this.ticketsInserted = data.ticketsInserted;

      this.ticketStatusChartOptions.series = [
        this.ticketsInserted,
        this.totalTickets - this.ticketsInserted,
      ];
      this.poolStatusChartOptions.series = [
        this.availableTicketsInPool,
        this.maximumPoolSize - this.availableTicketsInPool,
      ];
    });

    this.dashboardService.getCustomers().subscribe((data) => {
      this.activeCustomers = data;
    });

    this.dashboardService.getVendors().subscribe((data) => {
      this.activeVendors = data;
    });
  }

  stopVendor(vendorId: string): void {
    this.dashboardService.stopVendor(vendorId).subscribe(() => this.fetchPoolDetails());
  }

  stopCustomer(customerId: string): void {
    this.dashboardService.stopCustomer(customerId).subscribe(() => this.fetchPoolDetails());
  }
}
