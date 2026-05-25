import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddRadniNalogComponent } from './add-radni-nalog';

describe('AddRadniNalogComponent', () => {
  let component: AddRadniNalogComponent;
  let fixture: ComponentFixture<AddRadniNalogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddRadniNalogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddRadniNalogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
