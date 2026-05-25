import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NaruciteljDetailsComponent } from './narucitelj-details';

describe('NaruciteljDetailsComponent', () => {
  let component: NaruciteljDetailsComponent;
  let fixture: ComponentFixture<NaruciteljDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NaruciteljDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NaruciteljDetailsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
