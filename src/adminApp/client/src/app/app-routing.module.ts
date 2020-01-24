import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DataSourceComponent } from './shared/component/data-source/data-source.component';
import { FieldComponent } from './shared/component/field/field.component';
import { TableComponent } from './shared/component/table/table.component';
import {MainViewComponent} from './shared/component/main-view/main-view.component';
import {TableListComponent} from './shared/component/table-list/table-list.component';
import {FieldListComponent} from './shared/component/field-list/field-list.component';

const routes: Routes = [
  { path: '', component: MainViewComponent},
  {path: 'tables', component: TableListComponent},
  {path: 'table/:id', component: TableComponent},
  {path: 'fields/:id', component: FieldListComponent},
  {path: 'field/:id', component: FieldComponent},
  {path: 'ds_fields/:dsId', component: FieldListComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
