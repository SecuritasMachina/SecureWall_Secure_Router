import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FieldModel, FieldPreviewDataModel, RestService } from '../../services/rest.service';

export interface DialogData {
    lookup: FieldModel;
}

@Component( {
    selector: 'app-field-preview',
    templateUrl: './field-preview.component.html',
    styleUrls: ['./field-preview.component.css']
} )
export class FieldPreviewComponent implements OnInit {
    constructor(
        public rest: RestService,
        public dialogRef: MatDialogRef<FieldPreviewComponent>,
        @Inject( MAT_DIALOG_DATA ) public data: DialogData ) {
    }

    public pData: FieldPreviewDataModel[];

    onNoClick(): void {
        this.dialogRef.close();
    }

    load() {
        this.rest.getFieldPreview( this.data.lookup.id ).subscribe( data => this.pData = data );
    }

    ngOnInit() {
        this.load();
    }

}
