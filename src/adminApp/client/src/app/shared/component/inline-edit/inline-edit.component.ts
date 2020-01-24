import { Component, Input, Optional, Host } from '@angular/core';
import { SatPopover } from '@ncstate/sat-popover';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'inline-edit',
  styleUrls: ['inline-edit.component.scss'],
  template: `
    <form (ngSubmit)="onSubmit()">
      <div class="mat-subheading-2"></div>
      
      <mat-form-field>
        <input matInput maxLength="140" name="_dataFieldValue" [(ngModel)]="_dataFieldValue">
<!--        <mat-hint align="end">{{tags?.length || 0}}/140</mat-hint>-->
      </mat-form-field>

      <div class="actions">
        <button mat-button type="button" color="primary" (click)="onCancel()">CANCEL</button>
        <button mat-button type="submit" color="primary">SAVE</button>
      </div>
    </form>
  `
})
export class InlineEditComponent {

  @Input()
  get value(): string { return this._value; }
  set value(x: string) {
    this._dataFieldValue = this._value = x;
  }
  private _value = '';

  public _dataFieldValue = '';
  /** Form model for the input. */

  constructor(@Optional() @Host() public popover: SatPopover) { }

  ngOnInit() {
    // subscribe to cancellations and reset form value
    if (this.popover) {
      this.popover.closed.pipe(filter(val => val == null))
        .subscribe(() => this._dataFieldValue = this.value || '');
    }
  }

  onSubmit() {
    if (this.popover) {
      this.popover.close(this._dataFieldValue);
    }
  }

  onCancel() {
    if (this.popover) {
      this.popover.close(this._dataFieldValue);
    }
  }
}
