import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataSourceComponent } from './data-source.component';

describe('DataSourceComponent', () => {
  let component: DataSourceComponent;
  let fixture: ComponentFixture<DataSourceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataSourceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataSourceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
