import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ManagementService, NaloziReportItem, NaruciteljiReportItem } from '../services/management.service';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reports.html',
  styleUrl: './reports.css',
})
export class ReportsComponent implements OnInit {
  activeTab = signal<'nalozi' | 'narucitelji'>('nalozi');

  nalozi = signal<NaloziReportItem[]>([]);
  narucitelji = signal<NaruciteljiReportItem[]>([]);

  filterFrom = '';
  filterTo = '';
  filterStatus = '';

  constructor(private readonly mgmt: ManagementService) {}

  ngOnInit() {
    this.loadNalozi();
    this.loadNarucitelji();
  }

  switchTab(tab: 'nalozi' | 'narucitelji') {
    this.activeTab.set(tab);
  }

  loadNalozi() {
    const params: any = {};
    if (this.filterFrom) params.from = this.filterFrom;
    if (this.filterTo) params.to = this.filterTo;
    if (this.filterStatus) params.status = this.filterStatus;
    this.mgmt.getNaloziReport(params).subscribe({ next: (d) => this.nalozi.set(d) });
  }

  loadNarucitelji() {
    this.mgmt.getNaruciteljiReport().subscribe({ next: (d) => this.narucitelji.set(d) });
  }

  applyFilters() {
    this.loadNalozi();
  }

  clearFilters() {
    this.filterFrom = '';
    this.filterTo = '';
    this.filterStatus = '';
    this.loadNalozi();
  }
}
