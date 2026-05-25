import { ChangeDetectorRef, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { RadniNalogService, RadniNalog, RadniNalogNote, Document } from '../services/radni-nalog.service';
import { AktivnostiService } from '../services/aktivnosti.service';
import { ManagementService, UserRecord } from '../services/management.service';
import { fromEvent } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-radni-nalog-details',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './radni-nalog-details.html',
  styleUrls: ['./radni-nalog-details.css']
})
export class RadniNalogDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private radniNalogService = inject(RadniNalogService);
  private aktivnostiService = inject(AktivnostiService);
  private cdr = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly sanitizer = inject(DomSanitizer);
  private readonly mgmt = inject(ManagementService);

  id!: number;
  nalog?: RadniNalog;
  documents: Document[] = [];
  notes: RadniNalogNote[] = [];
  newNote = '';
  loading = false;
  uploadingDocument = false;
  deletingDocument = false;
  documentError: string | null = null;
  error: string | null = null;
  showDeleteDocumentModal = false;
  documentToDelete: Document | null = null;
  showUpdateModal = false;
  updateModalUrl: SafeResourceUrl | null = null;
  showAttachDialog = false;
  attachDialogMode: 'choice' | 'url' = 'choice';
  attachmentName = '';
  attachmentUrl = '';
  users: UserRecord[] = [];
  selectedUserId: number | null = null;
  private pendingFileInput: HTMLInputElement | null = null;
  private aktivnostiNazivById = new Map<number, string>();

  ngOnInit() {
    if (!isPlatformBrowser(this.platformId)) {
      this.loading = false;
      return;
    }

    fromEvent<MessageEvent>(window, 'message')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((event) => {
        if (event.origin !== window.location.origin) {
          return;
        }

        if (event.data?.type === 'modal-close') {
          this.closeUpdateModal(!!event.data?.refresh);
        }
      });

    this.loadAktivnostiOptions();
    this.loadUsers();

    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      console.log('[radni-nalog-details] route id:', id);

      if (!Number.isFinite(id) || id <= 0) {
        this.id = 0;
        this.nalog = undefined;
        this.documents = [];
        this.loading = false;
        this.error = 'Neispravan ID radnog naloga.';
        this.cdr.detectChanges();
        return;
      }

      this.loadRadniNalog(id);
    });
  }

  private loadRadniNalog(id: number) {
    this.id = id;
    this.loading = true;
    this.error = null;
    this.cdr.detectChanges();
    console.log('[radni-nalog-details] loading started for id:', id);

    this.radniNalogService.getById(id).subscribe({
      next: (data) => {
        console.log('[radni-nalog-details] data arrived for id:', id, data);
        this.nalog = data;
        this.documents = data?.documents || [];
        this.selectedUserId = (data as any)?.assignedUser?.id || null;
        this.loadNotes(id);
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.nalog = undefined;
        this.documents = [];
        this.notes = [];
        this.error = `Neuspjelo učitavanje radnog naloga: ${err?.message || 'Nepoznata greška'}`;
        this.loading = false;
        this.cdr.detectChanges();
        console.error('Error loading radni nalog:', err);
      }
    });
  }

  private loadAktivnostiOptions() {
    this.aktivnostiService.getAll().subscribe({
      next: (data) => {
        this.aktivnostiNazivById.clear();
        (data ?? []).forEach((item) => {
          if (item.id && item.naziv) {
            this.aktivnostiNazivById.set(item.id, item.naziv);
          }
        });
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading aktivnosti options:', err);
      }
    });
  }

  private loadUsers() {
    this.mgmt.getUsers().subscribe({
      next: (data) => {
        this.users = (data ?? []).filter(u => u.isActive);
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  assignWorker() {
    if (!this.nalog?.id) return;
    this.mgmt.assignWorker(this.nalog.id, this.selectedUserId).subscribe({
      next: () => this.loadRadniNalog(this.id),
      error: (err) => console.error('Assign failed:', err)
    });
  }

  private loadNotes(radniNalogId: number) {
    this.radniNalogService.getNotesByRadniNalog(radniNalogId).subscribe({
      next: (notes) => {
        this.notes = notes ?? [];
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.notes = [];
        console.error('Error loading notes:', err);
      }
    });
  }

  addNote() {
    const text = this.newNote.trim();
    if (!text || !this.nalog?.id) {
      return;
    }

    this.radniNalogService.addNote(this.nalog.id, text).subscribe({
      next: (note) => {
        this.notes.push(note);
        this.newNote = '';
      },
      error: (err) => {
        console.error('Error adding note:', err);
      }
    });
  }

  downloadPdf() {
    if (!this.nalog?.id) { return; }
    this.radniNalogService.getPdf(this.nalog.id).subscribe({
      next: (response) => {
        const blob = response.body;
        if (!blob) {
          return;
        }

        const contentDisposition = response.headers.get('content-disposition') || response.headers.get('Content-Disposition');
        const fileNameMatch = contentDisposition?.match(/filename\*?=(?:UTF-8''|\")?([^\";]+)/i);
        const serverFileName = fileNameMatch?.[1] ? decodeURIComponent(fileNameMatch[1].replace(/\"/g, '').trim()) : null;

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = serverFileName || `radni-nalog-${this.nalog?.brojNaloga || `${this.nalog?.id}`}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: err => {
        console.error('Failed to download PDF', err);
      }
    });
  }

  openAttachDialog(fileInput: HTMLInputElement) {
    if (this.uploadingDocument || !this.nalog?.id) {
      return;
    }

    this.pendingFileInput = fileInput;
    this.attachDialogMode = 'choice';
    this.attachmentName = '';
    this.attachmentUrl = '';
    this.documentError = null;
    this.showAttachDialog = true;
  }

  beginLocalAttachment() {
    if (!this.pendingFileInput) {
      this.documentError = 'Ne mogu otvoriti dijalog za odabir datoteke.';
      return;
    }

    this.showAttachDialog = false;
    setTimeout(() => {
      this.pendingFileInput?.click();
    }, 0);
  }

  openUrlAttachForm() {
    this.attachDialogMode = 'url';
  }

  cancelAttachDialog() {
    if (this.uploadingDocument) {
      return;
    }

    this.showAttachDialog = false;
    this.attachDialogMode = 'choice';
    this.attachmentName = '';
    this.attachmentUrl = '';
    this.pendingFileInput = null;
  }

  submitUrlAttachment() {
    if (this.uploadingDocument || !this.nalog?.id) {
      return;
    }

    const name = (this.attachmentName || this.extractFileNameFromUrl(this.attachmentUrl)).trim();
    const url = this.attachmentUrl.trim();

    if (!url) {
      this.documentError = 'Unesite URL dokumenta.';
      return;
    }

    if (!name) {
      this.documentError = 'Unesite naziv dokumenta.';
      return;
    }

    this.uploadingDocument = true;
    this.documentError = null;

    let absoluteUrl: string;
    try {
      absoluteUrl = new URL(url, window.location.href).toString();
    } catch {
      this.documentError = 'Unesite valjani URL dokumenta.';
      this.uploadingDocument = false;
      return;
    }

    this.radniNalogService.uploadDocument(this.nalog!.id!, {
      name,
      url: absoluteUrl
    }).subscribe({
      next: () => {
        const currentId = this.nalog?.id;
        if (currentId) {
          this.loadRadniNalog(currentId);
        }
        this.uploadingDocument = false;
        this.showAttachDialog = false;
        this.attachDialogMode = 'choice';
        this.attachmentName = '';
        this.attachmentUrl = '';
      },
      error: (err) => {
        this.documentError = 'Neuspješno dodavanje dokumenta iz URL-a.';
        this.uploadingDocument = false;
        console.error('Failed to attach document from URL', err);
      }
    });
  }

  private extractFileNameFromUrl(url: string): string {
    try {
      const parsed = new URL(url, window.location.href);
      const path = parsed.pathname.split('/').filter(Boolean);
      return path.length > 0 ? path[path.length - 1] : '';
    } catch {
      return '';
    }
  }

  onDocumentSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file || !this.nalog?.id) {
      return;
    }

    this.uploadingDocument = true;
    this.documentError = null;

    this.fileToBase64(file)
      .then((base64) => {
        this.radniNalogService.uploadDocument(this.nalog!.id!, {
          name: file.name,
          blob: base64
        }).subscribe({
          next: () => {
            const currentId = this.nalog?.id;
            if (currentId) {
              this.loadRadniNalog(currentId);
            }
            this.uploadingDocument = false;
            input.value = '';
          },
          error: (err) => {
            this.documentError = 'Neuspješno dodavanje dokumenta.';
            this.uploadingDocument = false;
            input.value = '';
            console.error('Failed to attach document', err);
          }
        });
      })
      .catch((err) => {
        this.documentError = 'Neuspješno čitanje odabrane datoteke.';
        this.uploadingDocument = false;
        input.value = '';
        console.error('Failed to read file as base64', err);
      });
  }

  downloadDocument(event: Event, doc: Document) {
    event.preventDefault();

    if (doc.url) {
      window.open(doc.url, '_blank');
      return;
    }

    if (!doc?.id || !this.nalog?.id) {
      this.documentError = 'Nedostaje ID dokumenta.';
      return;
    }

    this.documentError = null;

    this.radniNalogService.downloadDocumentById(this.nalog.id, doc.id).subscribe({
      next: (response) => {
        const base64Payload = (response?.blob || '').replace(/\s/g, '');
        if (!base64Payload) {
          this.documentError = 'Preuzeti dokument je prazan.';
          return;
        }

        const fileBlob = this.base64ToBlob(base64Payload);
        const fileName = response?.name || doc.name || 'document';
        const blobUrl = window.URL.createObjectURL(fileBlob);
        const a = document.createElement('a');
        a.href = blobUrl;
        a.download = fileName;
        a.click();
        window.URL.revokeObjectURL(blobUrl);
      },
      error: (err) => {
        this.documentError = 'Neuspješno preuzimanje dokumenta.';
        console.error('Failed to download document', err);
      }
    });
  }

  openDeleteDocumentModal(event: Event, doc: Document) {
    event.preventDefault();
    event.stopPropagation();

    this.documentError = null;
    this.documentToDelete = doc;
    this.showDeleteDocumentModal = true;
  }

  closeDeleteDocumentModal() {
    if (this.deletingDocument) {
      return;
    }

    this.showDeleteDocumentModal = false;
    this.documentToDelete = null;
  }

  confirmDeleteDocument() {
    if (!this.nalog?.id || !this.documentToDelete?.id) {
      this.documentError = 'Nedostaje ID dokumenta.';
      this.closeDeleteDocumentModal();
      return;
    }

    this.deletingDocument = true;
    this.documentError = null;

    this.radniNalogService.deleteDocumentById(this.nalog.id, this.documentToDelete.id).subscribe({
      next: () => {
        const currentId = this.nalog?.id;
        if (currentId) {
          this.loadRadniNalog(currentId);
        }

        this.deletingDocument = false;
        this.showDeleteDocumentModal = false;
        this.documentToDelete = null;
      },
      error: (err) => {
        this.documentError = 'Neuspješno brisanje dokumenta.';
        this.deletingDocument = false;
        this.showDeleteDocumentModal = false;
        this.documentToDelete = null;
        console.error('Failed to delete document', err);
      }
    });
  }

  openUpdateModal() {
    const nalogId = this.nalog?.id;
    if (!nalogId || !isPlatformBrowser(this.platformId)) {
      return;
    }

    const popupUrl = new URL(`updateRadniNalog/${nalogId}`, document.baseURI);
    popupUrl.searchParams.set('embedded', '1');

    this.updateModalUrl = this.sanitizer.bypassSecurityTrustResourceUrl(popupUrl.toString());
    this.showUpdateModal = true;
  }

  closeUpdateModal(refresh = true) {
    this.showUpdateModal = false;
    this.updateModalUrl = null;

    if (refresh && this.id) {
      this.loadRadniNalog(this.id);
    }
  }

  private fileToBase64(file: File | Blob): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = () => {
        const result = reader.result;
        if (typeof result !== 'string') {
          reject(new Error('Invalid file read result'));
          return;
        }

        const parts = result.split(',');
        const base64 = parts.length > 1 ? parts[1] : '';
        if (!base64) {
          reject(new Error('Empty base64 payload'));
          return;
        }

        resolve(base64);
      };

      reader.onerror = (error) => reject(error);
      reader.readAsDataURL(file);
    });
  }

  private base64ToBlob(base64: string): Blob {
    const byteCharacters = atob(base64);
    const bytes = new Uint8Array(byteCharacters.length);

    for (let index = 0; index < byteCharacters.length; index += 1) {
      bytes[index] = byteCharacters.charCodeAt(index);
    }

    return new Blob([bytes]);
  }

  renderAktivnosti(aktivnosti: Array<string | number> | undefined): string {
    if (!Array.isArray(aktivnosti) || aktivnosti.length === 0) {
      return '';
    }

    return aktivnosti
      .map((item) => {
        if (typeof item === 'number') {
          return this.aktivnostiNazivById.get(item) ?? String(item);
        }

        const normalized = String(item ?? '').trim();
        if (!normalized) {
          return '';
        }

        const numericId = Number(normalized);
        if (Number.isFinite(numericId)) {
          return this.aktivnostiNazivById.get(numericId) ?? normalized;
        }

        return normalized;
      })
      .filter((value) => value.length > 0)
      .join(', ');
  }
}