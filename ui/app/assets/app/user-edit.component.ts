import {Component, OnInit} from 'angular2/core';
import {User} from './services/user';
import {RouteParams, Router} from 'angular2/router';
import {UserService} from './services/user.service';

@Component({
    selector: 'my-user-edit',
    templateUrl: 'assets/app/user-edit.component.html',
    styleUrls: ['assets/app/user-edit.component.css'],
    providers: [],
    directives: []
})

export class UserEditComponent implements OnInit {

    currentUser: User;
    public model: User;

    constructor(
        private _router: Router,
        private _userService: UserService,
        private _routeParams: RouteParams) {
    }

    ngOnInit(): Promise<any> {
        let id = +this._routeParams.get('id');

        return new Promise((resolve, reject) => {
            this._userService.getCurrentUser()
                .then(user => {

                    this.currentUser = user;

                    if (user && user.role === "ADMIN") {
                        this._userService.getUserById(id).then(user => {
                            this.model = Object.assign({}, user);
                            resolve();
                        });
                    } else {
                        resolve();
                        this._router.navigate(['Dashboard', {}]);
                    }

                });
        });

    }

    save(user: User, event: any) {
        this._userService.save(this.model).then(result => {
            this._router.navigate(['Admin', {}]);
            event.preventDefault();
            event.stopPropagation();

        });
    }

}
