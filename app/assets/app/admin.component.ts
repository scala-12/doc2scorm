import { Component, OnInit} from 'angular2/core';
import { User } from './services/user';
import { UserService } from './services/user.service';
import { Router } from 'angular2/router';

@Component({
    selector: 'my-admin',
    templateUrl: 'assets/app/admin.component.html',
    styleUrls: ['assets/app/admin.component.css']
})

export class AdminComponent implements OnInit {
    public currentUser: User;
    public users: User[];

    constructor(
        private _router: Router,
        private _userService: UserService) { }

    ngOnInit() {
        this._userService.getCurrentUser()
            .then(user => {
            this.currentUser = user;
                if (!user || user.role !== "ADMIN") {
                    this._router.navigate(['Dashboard', {}]);
                } else {
                    this._userService.getUsers()
                        .then(users => this.users = users);
                }
            });
    }

    permission(user: User): String {
        if (user && user.role !== "GUEST") {
            return "Доступ разрешен";
        } else {
            return "Доступ запрещен";
        }
    }

    edit(user: User) {
        let link = ['UserEdit', { id: user.id }];
        this._router.navigate(link);
    }
    
    formatDate(inMilliseconds: number) {
    	return new Date(inMilliseconds).toLocaleDateString();
    }

}