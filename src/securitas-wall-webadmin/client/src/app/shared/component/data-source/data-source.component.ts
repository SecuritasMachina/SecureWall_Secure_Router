import { Component, EventEmitter, Output, ViewChild } from '@angular/core';
import { DataSource, RestService } from '../../services/rest.service';
import { UtilsService } from '../../services/utils.service';
import { TreeviewConfig, TreeviewItem } from 'ngx-treeview';
import { TableListComponent } from '../table-list/table-list.component';
import { MatSnackBar } from '@angular/material';
import { OnDestroy } from "@angular/core";
import { FormControl, Validators } from '@angular/forms';
import { ConfirmationDialogComponent } from "src/app/shared/component/confirmation-dialog/confirmation-dialog.component";
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormGroup } from "@angular/forms";
import { OnInit } from "@angular/core";


export function determineId( id: any ): string {
    if ( id.constructor.name === 'array' && id.length > 0 ) {
        return '' + id[0];
    }
    return '' + id;
}
export interface ConfirmDialogData {
    id: string;
    name: string;
}

@Component( {
    selector: 'app-data-source',
    templateUrl: './data-source.component.html',
    styleUrls: ['./data-source.component.css']
} )

export class DataSourceComponent implements OnDestroy, OnInit {
    ngOnInit(): void {
        this.ownerForm = new FormGroup( {
            ds_name: new FormControl( '', [Validators.required] ),
            dsn: new FormControl( '', [Validators.required] ),
            username: new FormControl(),
            password: new FormControl(),
            driver: new FormControl(),
            description: new FormControl(),
            tags: new FormControl(),
            lob: new FormControl(),
            informationOwner: new FormControl(),
            responsibleDBA: new FormControl(),
            applicationOwner: new FormControl(),
            objectOwner: new FormControl(),
            sor: new FormControl(),
            soa: new FormControl(),
            availability_control: new FormControl(),
            confidentiality_control: new FormControl(),
            integrity_control: new FormControl()
        } );
    }
    
    public hasError = (controlName: string, errorName: string) =>{
        return this.ownerForm.controls[controlName].hasError(errorName);
      }
     
    ngOnDestroy(): void {
        this.snackBar.dismiss();
    }

    @ViewChild( TableListComponent )
    private tableListComponent: TableListComponent;

    @Output() editParentDS = new EventEmitter<DataSource>();

    public showPage: boolean;
    public dataSource = {} as DataSource;
    public showScanSpinner: boolean;
    public isNotSaved: boolean;
    confirmDialog: ConfirmationDialogComponent;
    constructor( public rest: RestService, public utils: UtilsService,
        private snackBar: MatSnackBar, public dialog: MatDialog ) {
    }

    items: TreeviewItem[] = [];

    values: number[];
    config = TreeviewConfig.create( {
        hasAllCheckBox: false,
        hasFilter: true,
        hasCollapseExpand: true,
        decoupleChildFromParent: false
    } );
    public ownerForm: FormGroup;


    getErrorMessage() {
        return this.ownerForm.controls['ds_name'].hasError( 'required' ) ? 'You must enter a value' :
            '';
    }
    handleError( error: any ) {
        this.showScanSpinner = false;
        document.body.style.cursor = 'default';
        this.snackBar.open( 'Scanning failed, check data source configuration', null, {
            duration: 4000
        } );
    }
    openConfirmDialog(): void {
        const dialogRef = this.dialog.open( ConfirmationDialogComponent, {
            width: '250px',
            data: { id: this.dataSource.id, name: this.dataSource.ds_name }
        } );

        dialogRef.afterClosed().subscribe( result => {
            console.log( result );
            if ( result ) {
                this.rest.deleteDataSource( result.id ).toPromise().then( res2 => {
                    this.showScanSpinner = false;
                    this.editParentDS.emit( this.dataSource );
                    this.snackBar.open( 'Deleted ' + result.ds_name );
                    this.hide();
                } );
            }
        } );
    }
    save() {
        if ( this.ownerForm.valid ) {
            this.showScanSpinner = true;
            this.dataSource.ds_name = this.ownerForm.controls['ds_name'].value;
            this.dataSource.dsn = this.ownerForm.controls['dsn'].value;
            this.dataSource.username = this.ownerForm.controls['username'].value;
            this.dataSource.password = this.ownerForm.controls['password'].value;
            this.dataSource.driver = this.ownerForm.controls['driver'].value;
            this.dataSource.description = this.ownerForm.controls['description'].value;
            this.dataSource.tags = this.ownerForm.controls['tags'].value;
            this.dataSource.lob = this.ownerForm.controls['lob'].value;
            this.dataSource.informationOwner = this.ownerForm.controls['informationOwner'].value;
            this.dataSource.responsibleDBA = this.ownerForm.controls['responsibleDBA'].value;
            this.dataSource.applicationOwner = this.ownerForm.controls['applicationOwner'].value;
            this.dataSource.objectOwner = this.ownerForm.controls['objectOwner'].value;
            this.dataSource.sor = this.ownerForm.controls['sor'].value;
            this.dataSource.soa = this.ownerForm.controls['soa'].value;
            this.dataSource.availability_control = this.ownerForm.controls['availability_control'].value;
            this.dataSource.confidentiality_control = this.ownerForm.controls['confidentiality_control'].value;
            this.dataSource.integrity_control = this.ownerForm.controls['integrity_control'].value;

            this.rest.updateDataSource( this.dataSource ).toPromise().then( res2 => {
                this.snackBar.open( this.dataSource.ds_name + ' Updated ' );
                this.showScanSpinner = false;
                this.editParentDS.emit( <DataSource>res2 );
                this.isNotSaved = false;
                this.dataSource = <DataSource>res2;
            } );
        } else {
            this.snackBar.open( 'Please fill in all required fields' );
        }
    }

    scan( ds ) {
        this.showScanSpinner = true;
        this.snackBar.open( 'Scanning initiated for ' + ds.ds_name + ', please wait and refresh page in a minutes depending on size of database' );
        // causes snackbar to fail: document.body.style.cursor = 'wait';
        this.rest.scanDataSource( ds ).toPromise().then( res2 => {
        } );


        setTimeout(() => {
            this.showScanSpinner = false;
            this.editParentDS.emit( ds );
            // this.snackBar.open( 'Completed scanning ' + ds.ds_name );
            this.showPage = false;
        }, 4000 );
        //TODO poll occasionally
    }

    delete( ds ) {
        this.openConfirmDialog();
    }
    load( id: string ) {
        this.showPage = true;
        this.rest.getDataSource( id ).toPromise().then( dataSource => {
            this.dataSource = dataSource;
            this.ownerForm.controls['ds_name'].setValue( dataSource.ds_name );
            this.ownerForm.controls['dsn'].setValue( dataSource.dsn );
            this.ownerForm.controls['username'].setValue( dataSource.username );
            this.ownerForm.controls['password'].setValue( dataSource.password );
            this.ownerForm.controls['driver'].setValue( dataSource.driver );
            this.ownerForm.controls['description'].setValue( dataSource.description );
            this.ownerForm.controls['tags'].setValue( dataSource.tags );
            this.ownerForm.controls['lob'].setValue( dataSource.lob );
            this.ownerForm.controls['informationOwner'].setValue( dataSource.informationOwner );
            this.ownerForm.controls['responsibleDBA'].setValue( dataSource.responsibleDBA );
            this.ownerForm.controls['applicationOwner'].setValue( dataSource.applicationOwner );
            this.ownerForm.controls['objectOwner'].setValue( dataSource.objectOwner );
            this.ownerForm.controls['sor'].setValue( dataSource.sor );
            this.ownerForm.controls['soa'].setValue( dataSource.soa );

            this.ownerForm.controls['availability_control'].setValue( dataSource.availability_control );
            this.ownerForm.controls['confidentiality_control'].setValue( dataSource.confidentiality_control );
            this.ownerForm.controls['integrity_control'].setValue( dataSource.integrity_control );

            this.tableListComponent.load( id );
            this.isNotSaved = false;

        } );
    }

    addnew() {
        this.snackBar.open( 'Enter details for data source, then save' );
        this.showPage = true;
        this.ownerForm.controls['ds_name'].setValue( '' );
        this.ownerForm.controls['dsn'].setValue( '' );
        this.ownerForm.controls['username'].setValue( '' );
        this.ownerForm.controls['password'].setValue( '' );
        this.ownerForm.controls['driver'].setValue( '' );
        this.ownerForm.controls['description'].setValue( '' );
        this.ownerForm.controls['tags'].setValue( '' );
        this.ownerForm.controls['lob'].setValue( '' );
        this.ownerForm.controls['informationOwner'].setValue( '' );
        this.ownerForm.controls['responsibleDBA'].setValue( '' );
        this.ownerForm.controls['applicationOwner'].setValue( '' );
        this.ownerForm.controls['objectOwner'].setValue( '' );
        this.ownerForm.controls['sor'].setValue( '' );
        this.ownerForm.controls['soa'].setValue( '' );

        this.ownerForm.controls['availability_control'].setValue( '' );
        this.ownerForm.controls['confidentiality_control'].setValue( '' );
        this.ownerForm.controls['integrity_control'].setValue( '' );

        this.dataSource = {} as DataSource;
        this.isNotSaved = true;
    }
    hide() {
        this.showPage = false;
        this.tableListComponent.hide();
    }
    compareIds( id1: any, id2: any ): boolean {
        const a1 = determineId( id1 );
        const a2 = determineId( id2 );
        return a1 === a2;
    }

}
