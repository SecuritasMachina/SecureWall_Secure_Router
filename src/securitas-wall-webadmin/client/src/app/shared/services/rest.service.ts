import { Injectable, isDevMode } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export interface DataSource {
    id: string;
    ds_name: string;
    dsn: string;
    username: string;
    password: string;
    driver: string;
    description: string;
    tags: string;
    version: string;
    owner: string;
    enteredDate: number;
    changedDate: number;
    changedBy: string;
    expirationDate: number;
    effectiveDate: number;
    lob: string;
    informationOwner: string;
    responsibleDBA: string;
    applicationOwner: string;
    objectOwner: string;
    sor: string;
    soa: string;
    lastScanned: number;
    error: string;
    availability_control: string;
    confidentiality_control: string;
    integrity_control: string;
}
export interface DBObjectSummary {
    dscount: number;
    tableCount: number;
    fieldCount: number;
}

export interface TableModel {
    id: string;
    data_source_id_fk: string;
    name: string;
    description: string;
    tags: string;
    rowCount: number;
    enteredDate: number;
    changedDate: number;
    changedBy: string;
    expirationDate: number;
    effectiveDate: number;
    lob: string;
    informationOwner: string;
    responsibleDBA: string;
    applicationOwner: string;
    objectOwner: string;
    sor: string;
    soa: string;
}

export interface FieldModel {
    id: string;
    table_def_id_fk: string;
    dsName: string;
    tableName: string;
    name: string;
    description: string;
    dbdescription: string;
    defaultvalue: string;
    type: string;
    isautoincrement: boolean;
    isprimarykey: boolean;
    isrequired: boolean;
    tags: string;
    enteredDate: number;
    expirationDate: number;
    effectiveDate: number;
    changedDate: number;
    changedBy: string;
    availability_control: string;
    confidentiality_control: string;
    integrity_control: string;
    lob: string;
    informationOwner: string;
    responsibleDBA: string;
    applicationOwner: string;
    objectOwner: string;
    sor: string;
    soa: string;
}
export interface TableFieldsModel {
    columns: string[];
    columnData: string[][];
}

export interface FieldPreviewDataModel {
    value: string;
}

@Injectable( {
    providedIn: 'root'
} )


export class RestService {
    baseURL = '';

    constructor( private http: HttpClient ) {
        if ( isDevMode() ) {
            this.baseURL = '//localhost:8080/rest';
        } else {
            this.baseURL = 'rest';
        }
    }

    scanDataSource( pDS: DataSource ) {
        return this.http.post( this.baseURL + '/scanDS', pDS );
    }

    updateDataSource( pDS: DataSource ) {
        return this.http.post( this.baseURL + '/dataSource', pDS );
    }

    getDBSummary(): Observable<DBObjectSummary> {
        return this.http.get<DBObjectSummary>( this.baseURL + '/getCounts' );
    }

    getDataSources(): Observable<DataSource[]> {
        return this.http.get<DataSource[]>( this.baseURL + '/dataSources/false' );
    }

    getDataSource( id: string ): Observable<DataSource> {
        return this.http.get<DataSource>( this.baseURL + '/dataSource/' + id );
    }
    getFieldPreview( fieldId: string ): Observable<FieldPreviewDataModel[]> {
        return this.http.get<FieldPreviewDataModel[]>( this.baseURL + '/previewField/' + fieldId );
    }
    getPreviewTableFields( fieldId: string ): Observable<TableFieldsModel> {
        return this.http.get<TableFieldsModel>( this.baseURL + '/previewTableFields/' + fieldId );
    }
    getPreviewDSFields( DSid: string ): Observable<FieldPreviewDataModel[]> {
        return this.http.get<FieldPreviewDataModel[]>( this.baseURL + '/previewDSFields/' + DSid );
    }


    /* future
      getAllTables(): Observable<TableModel> {
        return this.http.get<TableModel>(this.baseURL + '/allTables/false');
      }
    */

    getTables( fk: string ): Observable<TableModel[]> {
        return this.http.get<TableModel[]>( this.baseURL + '/tables/' + fk + '/false' );
    }

    getTable( id: string ): Observable<TableModel> {
        return this.http.get<TableModel>( this.baseURL + '/table/' + id );
    }

    updateTable( field: TableModel ) {
        return this.http.post( this.baseURL + '/table', field );
    }

    getFields( fk: string ): Observable<FieldModel[]> {
        return this.http.get<FieldModel[]>( this.baseURL + '/fields/' + fk + '/false' );
    }

    fieldSearch( term: string ): Observable<FieldModel[]> {
        return this.http.get<FieldModel[]>( this.baseURL + '/fieldSearch/' + term + '/false' );
    }


    getFieldsByDS( fk: string ): Observable<FieldModel[]> {
        return this.http.get<FieldModel[]>( this.baseURL + '/fieldsByDS/' + fk + '/false' );
    }
    getAllFields(): Observable<FieldModel[]> {
        return this.http.get<FieldModel[]>( this.baseURL + '/allFields/false' );
    }


    getField( id: string ): Observable<FieldModel> {
        return this.http.get<FieldModel>( this.baseURL + '/field/' + id );
    }

    updateField( field: FieldModel ) {
        return this.http.post( this.baseURL + '/field', field );
    }

    deleteDataSource( id: string ) {
        return this.http.delete( this.baseURL + '/dataSource/' + id );
    }

    deleteTable( id: string ) {
        return this.http.delete( this.baseURL + '/table/' + id );
    }

    deleteField( id: string ) {
        return this.http.delete( this.baseURL + '/field/' + id );
    }


}
