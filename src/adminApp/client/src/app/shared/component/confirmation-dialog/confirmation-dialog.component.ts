import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from "@angular/material/dialog";
import { MAT_DIALOG_DATA } from "@angular/material/dialog";
import { Inject } from "@angular/core";
import { ConfirmDialogData } from "src/app/shared/component/data-source/data-source.component";

@Component( {
    selector: 'app-confirmation-dialog',
    templateUrl: './confirmation-dialog.component.html',
    styleUrls: ['./confirmation-dialog.component.css']
} )
export class ConfirmationDialogComponent implements OnInit {
    constructor(
        public dialogRef: MatDialogRef<ConfirmationDialogComponent>,
        @Inject( MAT_DIALOG_DATA ) public data: ConfirmDialogData ) {}

    onNoClick(): void {
        this.dialogRef.close();
    }
    onOkayClick(): void {
        // this.data.buttonPicked=true;
        this.dialogRef.close(this.data);
    }

    ngOnInit() {
    }

}
