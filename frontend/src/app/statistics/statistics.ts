import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManagementService, DashboardStats, RevenueItem, PerformanceItem, MonthlyItem } from '../services/management.service';

@Component({
  selector: 'app-statistics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './statistics.html',
  styleUrl: './statistics.css',
})
export class StatisticsComponent implements OnInit {
  stats = signal<DashboardStats | null>(null);
  revenue = signal<RevenueItem[]>([]);
  performance = signal<PerformanceItem[]>([]);
  monthly = signal<MonthlyItem[]>([]);

  constructor(private readonly mgmt: ManagementService) {}

  ngOnInit() {
    this.mgmt.getDashboardStats().subscribe({ next: (s) => this.stats.set(s) });
    this.mgmt.getRevenueByAktivnost().subscribe({ next: (r) => this.revenue.set(r) });
    this.mgmt.getPerformanceByWorker().subscribe({ next: (p) => this.performance.set(p) });
    this.mgmt.getIssuedByMonth().subscribe({ next: (m) => this.monthly.set(m) });
  }

  totalRevenue(): number {
    return this.revenue().reduce((sum, r) => sum + r.ukupno, 0);
  }
}
