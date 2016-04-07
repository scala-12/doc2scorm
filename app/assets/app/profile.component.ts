import {Component, OnInit} from 'angular2/core';
import {User} from './services/user';
import {RouteParams} from 'angular2/router';
import {UserService} from './services/user.service';

@Component({
    selector: 'user-profile',
    templateUrl: 'assets/app/profile.component.html',
    styleUrls: ['assets/app/profile.component.css'],
})

export class ProfileComponent implements OnInit {
    public user: User;

    constructor(
        private _userService: UserService,
        private _routeParams: RouteParams) {
    }

    ngOnInit() {
        this._userService.getCurrentUser()
            .then(user => this.user = user);
    }

    formatDate(inMilliseconds: number) {
        return new Date(inMilliseconds).toLocaleDateString();
    }

    //    goBack() {
    //      window.history.back();
    //    }
}
