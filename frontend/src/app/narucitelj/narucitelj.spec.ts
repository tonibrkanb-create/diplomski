import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NaruciteljiComponent } from './narucitelj';

describe('NaruciteljiComponent', () => {
  let component: NaruciteljiComponent;
  let fixture: ComponentFixture<NaruciteljiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NaruciteljiComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NaruciteljiComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
