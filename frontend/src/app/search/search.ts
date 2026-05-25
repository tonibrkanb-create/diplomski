import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { NaruciteljaService } from '../services/narucitelj.service';
import { RadniNalogService } from '../services/radni-nalog.service';

interface SearchResult {
  narucitelji: any[];
  radniNalozi: any[];
}

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './search.html',
  styleUrls: ['./search.css']
})
export class SearchComponent {
  searchForm: FormGroup;
  searchResults: SearchResult = { narucitelji: [], radniNalozi: [] };
  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private naruciteljaService: NaruciteljaService,
    private radniNalogService: RadniNalogService
  ) {
    this.searchForm = this.fb.group({
      narucitelj: [''],
      name: [''],
      radniNalog: [''],
      opis: [''],
      radnik: ['']
    });
  }

  onSearch() {
    const criteria = this.searchForm.value;
    this.loading = true;
    this.error = null;
    this.searchResults = { narucitelji: [], radniNalozi: [] };

    // Search narucitelji
    if (criteria.narucitelj || criteria.name) {
      this.naruciteljaService.getAll().subscribe({
        next: (data) => {
          this.searchResults.narucitelji = data.filter(n =>
            (criteria.narucitelj === '' || n.name.toLowerCase().includes(criteria.narucitelj.toLowerCase())) &&
            (criteria.name === '' || n.name.toLowerCase().includes(criteria.name.toLowerCase()))
          );
          this.loading = false;
        },
        error: (err) => {
          console.error('Error searching narucitelji:', err);
          this.loading = false;
        }
      });
    }

    // Search radni nalozi
    if (criteria.radniNalog || criteria.opis) {
      this.radniNalogService.getAll().subscribe({
        next: (data) => {
          this.searchResults.radniNalozi = data.filter(n =>
            (criteria.radniNalog === '' || (n.id && n.id.toString().includes(criteria.radniNalog))) &&
            (criteria.opis === '' || (n.description && n.description.toLowerCase().includes(criteria.opis.toLowerCase())))
          );
          this.loading = false;
        },
        error: (err) => {
          console.error('Error searching radni nalozi:', err);
          this.loading = false;
        }
      });
    }

    if (!criteria.narucitelj && !criteria.name && !criteria.radniNalog && !criteria.opis && !criteria.radnik) {
      this.loading = false;
    }  }
}