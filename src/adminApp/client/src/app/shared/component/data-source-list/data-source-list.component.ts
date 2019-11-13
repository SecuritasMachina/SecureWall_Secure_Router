import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {RestService, DataSource} from '../../services/rest.service';
import {UtilsService} from '../../services/utils.service';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-data-source-list',
  templateUrl: './data-source-list.component.html',
  styleUrls: ['./data-source-list.component.css']
})


export class DataSourceListComponent implements OnInit {
  @Output() editParentDS = new EventEmitter<DataSource>();

  public dss: DataSource[] = [];

  constructor(public rest: RestService, public utils: UtilsService) {

  }

  editField: string;

  updateList(id: number, property: string, event: any) {
    const editField = event.target.textContent;
    this.dss[id][property] = editField;
    return this.rest.updateDataSource(this.dss[id]).toPromise().then(res2 => {
      // this.toastr.success(this.dataSource.ds_name + ' saved', '');
      // this.dataSourceListComponent.refresh();
    });

  }

  remove(id: any) {
    // this.awaitingPersonList.push(this.personList[id]);
    // this.personList.splice(id, 1);
  }

  add() {
    /*if (this.awaitingPersonList.length > 0) {
      const person = this.awaitingPersonList[0];
      this.personList.push(person);
      this.awaitingPersonList.splice(0, 1);
    }
    */
  }

  changeValue(id: number, property: string, event: any) {
    this.editField = event.target.textContent;
  }

  getDataSources(): Observable<any> {
    return this.rest.getDataSources();
  }

  editDS(pDS) {
    this.editParentDS.emit(pDS);
  }

  refresh() {
    console.log('refresh');
    this.getDataSources().subscribe(dss => this.dss = dss);
  }

  ngOnInit() {
    this.getDataSources().subscribe(dss => this.dss = dss);
  }
}
