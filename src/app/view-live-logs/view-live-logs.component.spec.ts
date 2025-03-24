import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewLiveLogsComponent } from './view-live-logs.component';

describe('ViewLiveLogsComponent', () => {
  let component: ViewLiveLogsComponent;
  let fixture: ComponentFixture<ViewLiveLogsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ViewLiveLogsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewLiveLogsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
