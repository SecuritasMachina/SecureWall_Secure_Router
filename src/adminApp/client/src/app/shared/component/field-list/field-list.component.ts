import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FieldModel, RestService, FieldPreviewDataModel } from '../../services/rest.service';
import { UtilsService } from '../../services/utils.service';
import { MatSnackBar } from '@angular/material';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatSort, MatPaginator } from '@angular/material';
import { FieldPreviewComponent } from '../field-preview/field-preview.component';
import { OnDestroy } from "@angular/core";
import { Observable, BehaviorSubject } from "rxjs";
import { DataSource } from "@angular/cdk/collections";
import { ViewChild } from "@angular/core";

@Component( {
    selector: 'app-field-list',
    templateUrl: './field-list.component.html',
    styleUrls: ['./field-list.component.css']
} )
export class FieldListComponent implements OnInit, OnDestroy {
//    @ViewChild(MatSort) sort: MatSort;
//    @ViewChild(MatPaginator) paginator: MatPaginator;

    ngOnDestroy(): void {
        this.snackBar.dismiss();
    }
    
    public tableFK: string;
    public dsFK: string;
    public editField: string;
    public showScanSpinner: boolean;
    public pData: FieldModel;
    public filterTerm = "";
    public showPage = false;
    displayedColumns = [];
    dataSource: ComponentDataSource;


    constructor( private router: Router, private route: ActivatedRoute,
        public rest: RestService, public utils: UtilsService,
        private snackBar: MatSnackBar, public dialog: MatDialog ) {
    }
    filter() {
        this.rest.fieldSearch( this.filterTerm ).subscribe( fields => {
            this.dataSource = new ComponentDataSource( fields );
            this.showScanSpinner = false;
        } );
    }
    clearFilter() {
        this.refresh();
    }

    previewData( field: Element ): void {
        const dialogRef = this.dialog.open( FieldPreviewComponent, {
            width: '250px',
            data: { lookup: field.id }
        } );

        dialogRef.afterClosed().subscribe( result => {
        } );
    }
    previewHoverData( field: Element ) {
        // causes snackbar to fail: document.body.style.cursor = 'wait';
        return this.rest.getFieldPreview( field.id ).toPromise().then(( res2 ) => {
            let previewData: string = ' preview: ';
            for ( const ds of res2 ) {
                previewData += ' ' + ds.value;
            }
            this.snackBar.open( previewData );
            // document.body.style.cursor = 'default';
        } );
    }
    refresh() {

        if ( this.tableFK ) {
            this.showScanSpinner = true;
            this.rest.getFields( this.tableFK ).subscribe( fields => {
                this.dataSource = new ComponentDataSource( fields );
                this.setDisplayColumns();
                this.showScanSpinner = false;
            } );
        } else {
            this.showScanSpinner = true;
            if ( this.dsFK === 'all' ) {
                this.rest.getAllFields().subscribe( fields => {
                    this.dataSource = new ComponentDataSource( fields );
                    this.setDisplayColumns();
                    this.showScanSpinner = false;
                } );
            } else {
                this.rest.getFieldsByDS( this.dsFK ).subscribe( fields => {
                    this.dataSource = new ComponentDataSource( fields );

                    this.setDisplayColumns();
                    this.showScanSpinner = false;
                } );
            }
        }
        this.showPage = true;
    }
    setDisplayColumns() {
        this.displayedColumns = ['id', 'name', 'description',
            'tags', 'sor', 'confidentiality_control', 'integrity_control', 'availability_control'];
    }

    ngOnInit() {
        this.tableFK = this.route.snapshot.paramMap.get( 'id' );
        this.dsFK = this.route.snapshot.paramMap.get( 'dsId' );
        if ( this.tableFK || this.dsFK ) {
            this.refresh();
        }
    }

    load( id: string ) {
        this.tableFK=id;
        this.refresh();
    }

    edit( id ) {
        this.router.navigate( ['/field/' + id], { relativeTo: this.route } );
    }
    update( row: string, el: FieldModel, pValue: string ) {
        const copy = this.dataSource.data().slice()
        el[row] = pValue;
        this.dataSource.update( copy );
        const rowToUpdate: FieldModel = Object.assign( {}, el );
        return this.rest.updateField( rowToUpdate ).toPromise().then( res2 => {
        } );
    }
}
export class ComponentDataSource extends DataSource<FieldModel> {

    private dataSubject = new BehaviorSubject<FieldModel[]>( [] );

    data() {
        return this.dataSubject.value;
    }

    update( data ) {
        this.dataSubject.next( data );
    }

    constructor( data: FieldModel[] ) {
        super();
        this.dataSubject.next( data );
    }

    /** Connect function called by the table to retrieve one stream containing the data to render. */
    connect(): Observable<FieldModel[]> {
        return this.dataSubject;
    }

    disconnect() { }
}

