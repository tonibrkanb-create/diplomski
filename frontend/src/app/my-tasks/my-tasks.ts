import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManagementService } from '../services/management.service';

interface MyTask {
  id: number;
  datum: string;
  status: string;
  naruciteljNaziv: string;
  aktivnostiCount: number;
}

@Component({
  selector: 'app-my-tasks',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-tasks.html',
  styleUrl: './my-tasks.css',
})
export class MyTasksComponent implements OnInit {
  tasks = signal<MyTask[]>([]);

  constructor(private readonly mgmt: ManagementService) {}

  ngOnInit() {
    this.mgmt.getMyTasks().subscribe({ next: (t) => this.tasks.set(t) });
  }

  getStatusClass(status: string): string {
    if (status === 'zavrsen') return 'status-zavrsen';
    if (status === 'fakturiran') return 'status-fakturiran';
    return 'status-aktivan';
  }
}
