import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FieldModel, RestService } from '../../services/rest.service';
import { MatSnackBar } from '@angular/material';
import { OnDestroy } from "@angular/core";
import { FormControl, Validators } from '@angular/forms';


@Component( {
    selector: 'app-field',
    templateUrl: './field.component.html',
    styleUrls: ['./field.component.css']
} )
export class FieldComponent implements OnInit, OnDestroy {
    ngOnDestroy(): void {
        this.snackBar.dismiss();
    }
    public showPage: boolean;
    public data = {} as FieldModel;
    public id: string;
    public showScanSpinner: boolean;

    description = new FormControl();
    tags = new FormControl();
    availability_control = new FormControl();
    confidentiality_control = new FormControl();
    integrity_control = new FormControl();
    lob = new FormControl();
    informationOwner = new FormControl();
    responsibleDBA = new FormControl();
    applicationOwner = new FormControl();
    objectOwner = new FormControl();
    sor = new FormControl();
    soa = new FormControl();

    constructor( private route: ActivatedRoute,
        private router: Router,
        private rest: RestService, private snackBar: MatSnackBar ) {
    }

    ngOnInit() {
        this.id = this.route.snapshot.paramMap.get( 'id' );
        this.data.description = "Loading";
        if ( this.id ) {
            this.load( this.id );
        }
    }
    previewHoverData( data: FieldModel ) {
        // causes snackbar to fail: document.body.style.cursor = 'wait';
        return this.rest.getFieldPreview( data.id ).toPromise().then(( res2 ) => {
            let previewData: string = ' preview: ';
            for ( const ds of res2 ) {
                previewData += ' ' + ds.value;
            }
            this.snackBar.open( previewData );
            // document.body.style.cursor = 'default';
        } );
    }

    load( id: string ) {
        console.log( 'load field', id );
        this.showPage = true;

        this.rest.getField( id ).toPromise().then( data => {
            this.data = data;
            this.description.setValue( data.description );
            this.tags.setValue( data.tags );
            this.availability_control.setValue( data.availability_control );
            this.confidentiality_control.setValue( data.confidentiality_control );
            this.integrity_control.setValue( data.integrity_control );
            this.lob.setValue( data.lob );
            this.informationOwner.setValue( data.informationOwner );
            this.responsibleDBA.setValue( data.responsibleDBA );
            this.applicationOwner.setValue( data.applicationOwner );
            this.objectOwner.setValue( data.objectOwner );
            this.sor.setValue( data.sor );
            this.soa.setValue( data.soa );

        } );
    }

    save() {
        this.showScanSpinner = true;
        this.data.description = this.description.value;
        this.data.tags = this.tags.value;
        this.data.availability_control = this.availability_control.value;
        this.data.confidentiality_control = this.confidentiality_control.value;
        this.data.integrity_control = this.integrity_control.value;
        this.data.lob = this.lob.value;
        this.data.informationOwner = this.informationOwner.value;
        this.data.responsibleDBA = this.responsibleDBA.value;
        this.data.applicationOwner = this.applicationOwner.value;
        this.data.objectOwner = this.objectOwner.value;
        this.data.sor = this.sor.value;
        this.data.soa = this.soa.value;

        return this.rest.updateField( this.data ).toPromise().then( res2 => {
            this.snackBar.open( this.data.name + ' Updated' );
            this.showScanSpinner = false;
        } );
    }

    hide() {
        this.showPage = false;
    }
}
