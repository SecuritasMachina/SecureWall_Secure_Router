import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms'; // <-- NgModel lives here
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MDBBootstrapModule } from 'angular-bootstrap-md';
import { HttpClientModule } from '@angular/common/http';
import { ToastrModule } from 'ngx-toastr';
import { TreeviewModule } from 'ngx-treeview';
import { MainViewComponent } from './shared/component/main-view/main-view.component';
import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';

import {
    MAT_DIALOG_DEFAULT_OPTIONS,
    MAT_SNACK_BAR_DEFAULT_OPTIONS,
    MatButtonModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDialog, MatDialogModule, MatToolbarModule, MatSelectModule, MatInputModule, MatFormFieldModule
} from '@angular/material';

import { FormsModule } from "@angular/forms";
import { ScrollDispatchModule } from "@angular/cdk/scrolling";
import { SatPopoverModule } from '@ncstate/sat-popover';
import { AppMaterialModules } from './material.module';

import { TableEditorComponent } from "src/app/shared/component/table-editor/table-editor.component";
import { InlineEditComponent } from './shared/component/inline-edit/inline-edit.component';
import { FieldPreviewComponent } from './shared/component/field-preview/field-preview.component';
import { ConfirmationDialogComponent } from './shared/component/confirmation-dialog/confirmation-dialog.component';
import { WelcomePageComponent } from "./shared/component/welcome-page/welcome-page.component";

import { DataSourceComponent } from './shared/component/data-source/data-source.component';
import { TableComponent } from './shared/component/table/table.component';
import { FieldComponent } from './shared/component/field/field.component';
import { TableListComponent } from './shared/component/table-list/table-list.component';
import { DataSourceListComponent } from './shared/component/data-source-list/data-source-list.component';
import { FieldListComponent } from './shared/component/field-list/field-list.component';

@NgModule( {
    declarations: [
        AppComponent,
        DataSourceComponent,
        TableComponent,
        FieldComponent,
        TableListComponent,
        DataSourceListComponent,
        FieldListComponent,
        MainViewComponent,
        FieldPreviewComponent,
        WelcomePageComponent,
        TableEditorComponent,
        InlineEditComponent,
        ConfirmationDialogComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MDBBootstrapModule.forRoot(),
        ToastrModule.forRoot(),
        MatButtonModule,
        MatCheckboxModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatDialogModule,
        MatListModule,
        TreeviewModule.forRoot(),
        MatCardModule,
        MatToolbarModule,
        MatSelectModule,
        MatInputModule,
        MatFormFieldModule,
        FormsModule,
        ReactiveFormsModule,
        MatGridListModule,
        ScrollDispatchModule,
        SatPopoverModule,
        AppMaterialModules
    ],
    entryComponents: [
        FieldPreviewComponent,
        ConfirmationDialogComponent
    ],
    providers: [{ provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: { duration: 2500 } },
    { provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: { hasBackdrop: false } }],
    bootstrap: [AppComponent]
} )
export class AppModule {
}
