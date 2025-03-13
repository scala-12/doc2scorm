import {Component, OnInit, Input} from 'angular2/core';
import {User} from './services/user';
import {UserService} from './services/user.service';

@Component({
    selector: 'user-profile',
    templateUrl: 'assets/app/profile.component.html',
    styleUrls: ['assets/app/profile.component.css'],
    providers: [],
    directives: []
})

export class ProfileComponent implements OnInit {
        
    public user: User;

    constructor(
        private _userService: UserService) {
    }

    ngOnInit(): Promise<any> {
        return this._userService.getCurrentUser()
            .then(user => this.user = user);
    }

    formatDate(inMilliseconds: number) {
        return new Date(inMilliseconds).toLocaleDateString();
    }

    //    goBack() {
    //      window.history.back();
    //    }
    
}
