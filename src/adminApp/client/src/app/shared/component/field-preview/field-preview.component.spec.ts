import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FieldPreviewComponent } from './field-preview.component';

describe('FieldPreviewComponent', () => {
  let component: FieldPreviewComponent;
  let fixture: ComponentFixture<FieldPreviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FieldPreviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FieldPreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
