import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RadniNalogDetailsComponent } from './radni-nalog-details';

describe('RadniNalogDetailsComponent', () => {
  let component: RadniNalogDetailsComponent;
  let fixture: ComponentFixture<RadniNalogDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RadniNalogDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RadniNalogDetailsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
