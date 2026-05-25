import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { RadniNalogService, UskoroIsticeItem } from '../services/radni-nalog.service';
import { finalize } from 'rxjs';

interface UskoroIsticeRow {
  narucitelj: string;
  radniNalog: string;
  aktivnost: string;
  datumIsteka: string;
}

@Component({
  selector: 'app-uskoro-istice',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './uskoro-istice.html',
  styleUrls: ['./uskoro-istice.css']
})
export class UskoroIsticeComponent implements OnInit {
  rows: UskoroIsticeRow[] = [];
  loading = false;
  error: string | null = null;

  private readonly radniNalogService = inject(RadniNalogService);
  private readonly route = inject(ActivatedRoute);

  ngOnInit() {
    this.route.data.subscribe((data) => {
      const resolved = (data['uskoroIstice'] as UskoroIsticeItem[] | undefined) ?? [];
      this.applyItems(resolved);
    });
  }

  loadData() {
    this.loading = true;
    this.error = null;

    this.radniNalogService.getUskoroIstice().pipe(
      finalize(() => {
        this.loading = false;
      })
    ).subscribe({
      next: (items) => {
        this.applyItems(items);
      },
      error: (err) => {
        console.error('Error loading uskoro-istice grid:', err);
        this.error = 'Neuspjelo učitavanje podataka.';
      }
    });
  }

  private applyItems(items: UskoroIsticeItem[] | UskoroIsticeItem | null | undefined) {
    const normalizedItems = this.normalizeItems(items);
    this.rows = normalizedItems.map((item) => this.mapRow(item));
  }

  private normalizeItems(items: UskoroIsticeItem[] | UskoroIsticeItem | null | undefined): UskoroIsticeItem[] {
    const asArray = Array.isArray(items) ? items : (items ? [items] : []);
    return asArray.filter((item): item is UskoroIsticeItem => !!item && item.isActive !== false);
  }

  private mapRow(item: UskoroIsticeItem): UskoroIsticeRow {
    return {
      narucitelj: String(
        item['narucitelj'] ??
        '-'
      ),
      radniNalog: String(
        item['radniNalog'] ??
        '-'
      ),
      aktivnost: String(
        item['aktivnost'] ??
        '-'
      ),
      datumIsteka: this.formatDate(
        String(
          item['datumIsteka'] ??
          ''
        )
      )
    };
  }

  private formatDate(value: string): string {
    if (!value) {
      return '-';
    }

    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
      return value;
    }

    return parsed.toLocaleDateString('hr-HR');
  }
}