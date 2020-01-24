import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RestService, TableModel } from '../../services/rest.service';
import { FieldListComponent } from '../field-list/field-list.component';
import { MatSnackBar } from '@angular/material';
import { OnDestroy } from "@angular/core";
import { FormControl, Validators } from '@angular/forms';

@Component( {
    selector: 'app-table',
    templateUrl: './table.component.html',
    styleUrls: ['./table.component.css']
} )
export class TableComponent implements OnInit, OnDestroy {
    ngOnDestroy(): void {
        this.snackBar.dismiss();
    }

    @ViewChild( FieldListComponent )
    private fieldListComponent: FieldListComponent;

    public id: any;
    public table = {} as TableModel;
    public showPage = false;
    public showScanSpinner = false;
    description = new FormControl();
    tags = new FormControl();
    constructor( private route: ActivatedRoute,
        private router: Router,
        private rest: RestService, private snackBar: MatSnackBar ) {
    }
    onEditParentDS( p: TableModel ) {
    }

    ngOnInit() {
        this.table.description = "Loading";
        this.id = this.route.snapshot.paramMap.get( 'id' );
        if ( this.id ) {
            this.load( this.id );
        }

    }

    load( id: string ) {
        this.showPage = true;
        this.rest.getTable( id )
        .subscribe( table => {
            this.table = table;
            this.description.setValue(table.description);
            this.tags.setValue(table.tags);
            this.fieldListComponent.load( id );
        } );
        
    }

    save() {
        this.showScanSpinner = true;
        this.table.description=this.description.value;
        this.table.tags=this.tags.value;
        
        return this.rest.updateTable( this.table ).toPromise().then( res2 => {
            this.showScanSpinner = false;
            this.snackBar.open( this.table.name + ' Updated' );
        } );
    }

    hide() {
        this.showPage = false;
       // this.fieldListComponent.hide();
    }
}
