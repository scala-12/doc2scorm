import { Component, OnInit, ElementRef } from 'angular2/core';
import { User } from './services/user';
import { UserService } from './services/user.service';
import { ConverterService } from './services/converter.service';
import { FORM_DIRECTIVES } from 'angular2/common';

@Component({
    selector: 'my-dashboard',
    templateUrl: 'assets/app/dashboard.component.html',
    styleUrls: ['assets/app/dashboard.component.css'],
    directives: [FORM_DIRECTIVES],
    providers: []
})

export class DashboardComponent implements OnInit {
    public user: User;
    public images: String = "/images/";

    public headers = ['Заголовок 1', 'Заголовок 2', 'Заголовок 3',
        'Заголовок 4', 'Заголовок 5', 'Заголовок 6',
        'Заголовок 7', 'Заголовок 8'];

    public selectedHeader: String = 'Заголовок 1';
    public courseName: String = '';
    public disableInput: any;
    public uploadStatus: String = '';
    public conversionStatus: String = '';
    public inputFile: any;
    public filename: String = '';

    constructor(
        private _userService: UserService,
        private _converterService: ConverterService,
        public _element: ElementRef) {
    }

    ngOnInit(): Promise<any> {
        return this._userService.getCurrentUser()
            .then(user => this.user = user);
    }

    onOpenFile(event: any) {
        this.uploadStatus = ''
        var files: Array<File> = files = event.srcElement.files;
        if (files.length > 0) {
            this.uploadStatus = "loading";
            this.filename = files[0].name;
            this.conversionStatus = "";
            this._converterService.uploadDoc(files[0])
                .then(data => this.uploadStatus = "success")
                .catch(error => this.uploadStatus = "fail");
        }

    }

    convert() {
        this.conversionStatus = "running";

        var name: String;
        if (this.disableInput) {
            name = this.filename.substring(this.filename.lastIndexOf('/') + 1, this.filename.lastIndexOf('.'));
        } else {
            name = this.courseName
        }
        var header = 0
        for (var i = 0; i < this.headers.length; i++) {
            if (this.selectedHeader === this.headers[i]) {
                header = (i + 1)
            }
        }

        this.uploadStatus = "";
        this._converterService.downloadScorm(name, header)
            .then(data => {
                this.conversionStatus = "success";
            })
            .catch(error => {
                this.conversionStatus = "fail";
            });
    }

}