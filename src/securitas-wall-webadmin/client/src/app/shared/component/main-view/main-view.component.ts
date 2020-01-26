import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { DataSource, RestService } from '../../services/rest.service';
import { UtilsService } from '../../services/utils.service';
import { ToastrService } from 'ngx-toastr';
import { DownlineTreeviewItem, TreeviewConfig, TreeviewItem } from 'ngx-treeview';
import { DataSourceComponent } from '../data-source/data-source.component';
import { TableComponent } from '../table/table.component';
import { FieldComponent } from '../field/field.component';
import { MatSnackBar } from '@angular/material';
import { WelcomePageComponent } from "../welcome-page/welcome-page.component";


@Component( {
    selector: 'app-main-view',
    templateUrl: './main-view.component.html',
    styleUrls: ['./main-view.component.css']
} )

export class MainViewComponent implements OnInit, AfterViewInit {

    @ViewChild( DataSourceComponent )
    private dataSourceComponent: DataSourceComponent;

    @ViewChild( WelcomePageComponent )
    private welcomePageComponent: WelcomePageComponent;


    @ViewChild( TableComponent )
    private tableComponent: TableComponent;
    @ViewChild( FieldComponent )
    private fieldComponent: FieldComponent;

    dataSource: DataSource;

    constructor( public rest: RestService,
        public utils: UtilsService,
        private toastr: ToastrService,
        private snackBar: MatSnackBar ) {
    }

    items: TreeviewItem[] = [];

    values: number[];
    config = TreeviewConfig.create( {
        hasAllCheckBox: false,
        hasFilter: true,
        hasCollapseExpand: false,
        decoupleChildFromParent: false,
        maxHeight: 800
    } );


    delete() {
    }

    newDataSource() {
        this.dataSourceComponent.addnew();
    }

    hideAllDetails() {
        this.dataSourceComponent.hide();
        
        this.tableComponent.hide();
        this.fieldComponent.hide();
        this.welcomePageComponent.hide();
    }

    onEditParentDS( pDS: DataSource ) {
        this.items = [];
        this.getDataSources();
    }

    getDataSources() {
        this.rest.getDataSources().toPromise().then( dsRes => {
            for ( const ds of dsRes ) {
                const key = 'dataSource:' + ds.id;
                const item = new TreeviewItem( { text: ds.ds_name, value: key, checked: false } );
                this.items.push( item );
            }
        } );
    }

    onNodeSelect( item: TreeviewItem ) {
        this.hideAllDetails();
        const nodeType = item.value.split( ':' )[0];
        const id = item.value.split( ':' )[1];
        if ( nodeType === 'dataSource' ) {
            this.dataSourceComponent.load( id );
            if ( !item.children ) {
                this.rest.getTables( id ).toPromise().then( res => {
                    const treeItems: TreeviewItem[] = [];
                    for ( const nodeitem of res ) {
                        const key = 'table:' + nodeitem.id;
                        const tableItem = new TreeviewItem( { text: nodeitem.name, value: key, checked: false } );
                        treeItems.push( tableItem );
                    }
                    if ( treeItems.length > 0 ) {
                        item.children = treeItems;
                    }
                } );
            }
        }
        if ( nodeType === 'table' ) {
            this.tableComponent.load( id );
            if ( !item.children ) {
                this.rest.getFields( id ).toPromise().then( res => {
                    const fieldItems: TreeviewItem[] = [];
                    for ( const nodeItem of res ) {
                        const key = 'field:' + nodeItem.id;
                        const fieldItem = new TreeviewItem( { text: nodeItem.name, value: key, checked: false } );
                        fieldItems.push( fieldItem );
                    }
                    if ( fieldItems.length > 0 ) {
                        item.children = fieldItems;
                    }
                } );
            }
        }
        if ( nodeType === 'field' ) {
            this.fieldComponent.load( id );
        }
    }

    onSelectedChange( downlineItems: DownlineTreeviewItem[] ) {
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        this.getDataSources();
        this.hideAllDetails();
    }

    onFilterChange( value: string ) {
    }
}
