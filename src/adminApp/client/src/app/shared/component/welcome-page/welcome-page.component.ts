import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { RestService, DBObjectSummary } from '../../services/rest.service';
import { UtilsService } from "src/app/shared/services/utils.service";

@Component( {
    selector: 'app-welcome-page',
    templateUrl: './welcome-page.component.html',
    styleUrls: ['./welcome-page.component.css']
} )
export class WelcomePageComponent implements OnInit {
    public dss = {} as DBObjectSummary;
    showPage: boolean;
    today: number ;
    
    constructor( public rest: RestService, public utils: UtilsService ) { }

    getDBSummary() {
        this.rest.getDBSummary().toPromise().then( res2 => {
            this.dss = res2;
            this.showPage = true;
        } );
    }

    ngOnInit() {
        this.today = new Date().getTime();
        this.getDBSummary();
    }
    hide() {
        this.showPage = false;
    }

}
