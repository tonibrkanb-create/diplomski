import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManagementService, DashboardStats, RevenueItem, PerformanceItem, MonthlyItem } from '../services/management.service';

type StatisticsTab = 'prihodi' | 'ucinak' | 'izdano';

@Component({
  selector: 'app-statistics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './statistics.html',
  styleUrl: './statistics.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StatisticsComponent implements OnInit {
  readonly tabs: Array<{ id: StatisticsTab; label: string }> = [
    { id: 'prihodi', label: 'Prihodi' },
    { id: 'ucinak', label: 'Učinak' },
    { id: 'izdano', label: 'Izdano' },
  ];

  readonly activeTab = signal<StatisticsTab>('prihodi');
  readonly stats = signal<DashboardStats | null>(null);
  readonly revenue = signal<RevenueItem[]>([]);
  readonly performance = signal<PerformanceItem[]>([]);
  readonly monthly = signal<MonthlyItem[]>([]);
  readonly totalRevenue = computed(() => this.revenue().reduce((sum, item) => sum + item.ukupno, 0));

  private readonly mgmt = inject(ManagementService);

  ngOnInit() {
    this.mgmt.getDashboardStats().subscribe({ next: (s) => this.stats.set(s) });
    this.mgmt.getRevenueByAktivnost().subscribe({ next: (r) => this.revenue.set(r) });
    this.mgmt.getPerformanceByWorker().subscribe({ next: (p) => this.performance.set(p) });
    this.mgmt.getIssuedByMonth().subscribe({ next: (m) => this.monthly.set(m) });
  }

  setTab(tab: StatisticsTab) {
    this.activeTab.set(tab);
  }
}
