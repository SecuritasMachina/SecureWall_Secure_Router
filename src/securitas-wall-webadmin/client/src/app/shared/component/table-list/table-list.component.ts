import { Component, OnInit } from '@angular/core';
import { RestService, TableModel } from '../../services/rest.service';
import { UtilsService } from '../../services/utils.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, BehaviorSubject } from "rxjs";
import { DataSource } from "@angular/cdk/collections";

@Component( {
    selector: 'app-table-list',
    templateUrl: './table-list.component.html',
    styleUrls: ['./table-list.component.css']
} )
export class TableListComponent implements OnInit {
    displayedColumns = ['id', 'name', 'description', 'tags'];
    dataSource: ComponentDataSource;
    showPage = false;

    constructor( private router: Router, private route: ActivatedRoute,
        public rest: RestService, public utils: UtilsService ) {

    }

    refresh() {
        // future this.rest.getAllTables().subscribe(tables => this.tables = tables);
    }

    ngOnInit() {
    }
    hide() {
        this.showPage=false;
    }

    load( id: string ) {
        this.rest.getTables( id ).subscribe( tables => {
            this.dataSource = new ComponentDataSource( tables );
            this.showPage = true;
        } );
    }


    edit( id ) {
        this.router.navigate( ['/table/' + id], { relativeTo: this.route } );
    }


    update( field: string, el: TableModel, pValue: string ) {
        const copy = this.dataSource.data().slice()
        el[field] = pValue;
        this.dataSource.update( copy );
        const table: TableModel = Object.assign( {}, el );
        return this.rest.updateTable( table ).toPromise().then( res2 => {
        } );
    }
}

export class ComponentDataSource extends DataSource<TableModel> {

    private dataSubject = new BehaviorSubject<TableModel[]>( [] );

    data() {
        return this.dataSubject.value;
    }

    update( data ) {
        this.dataSubject.next( data );
    }

    constructor( data: TableModel[] ) {
        super();
        this.dataSubject.next( data );
    }

    /** Connect function called by the table to retrieve one stream containing the data to render. */
    connect(): Observable<TableModel[]> {
        return this.dataSubject;
    }

    disconnect() { }
}
