import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ManagementService, SustavLogItem } from '../services/management.service';

@Component({
  selector: 'app-sustav-logovi',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sustav-logovi.html',
  styleUrl: './sustav-logovi.css',
})
export class SustavLogoviComponent implements OnInit {
  logs = signal<SustavLogItem[]>([]);
  filterEntity = '';
  filterAction = '';
  filterFrom = '';
  filterTo = '';

  constructor(private readonly mgmt: ManagementService) {}

  ngOnInit() { this.load(); }

  load() {
    const params: any = {};
    if (this.filterEntity) params.entity = this.filterEntity;
    if (this.filterAction) params.action = this.filterAction;
    if (this.filterFrom) params.from = this.filterFrom;
    if (this.filterTo) params.to = this.filterTo;
    this.mgmt.getLogs(params).subscribe({ next: (l) => this.logs.set(l) });
  }
}
