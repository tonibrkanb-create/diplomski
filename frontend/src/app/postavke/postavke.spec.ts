import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostavkeComponent } from './postavke';

describe('PostavkeComponent', () => {
  let component: PostavkeComponent;
  let fixture: ComponentFixture<PostavkeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostavkeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostavkeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
