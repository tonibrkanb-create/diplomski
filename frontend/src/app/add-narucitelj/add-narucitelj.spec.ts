import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddNaruciteljComponent } from './add-narucitelj';

describe('AddNaruciteljComponent', () => {
  let component: AddNaruciteljComponent;
  let fixture: ComponentFixture<AddNaruciteljComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddNaruciteljComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddNaruciteljComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
