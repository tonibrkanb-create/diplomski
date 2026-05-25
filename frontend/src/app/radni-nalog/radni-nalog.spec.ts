import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RadniNalogComponent } from './radni-nalog';

describe('RadniNalogComponent', () => {
  let component: RadniNalogComponent;
  let fixture: ComponentFixture<RadniNalogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RadniNalogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RadniNalogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
