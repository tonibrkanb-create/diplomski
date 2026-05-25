import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateRadniNalogComponent } from './update-radni-nalog';

describe('UpdateRadniNalogComponent', () => {
  let component: UpdateRadniNalogComponent;
  let fixture: ComponentFixture<UpdateRadniNalogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateRadniNalogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateRadniNalogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
