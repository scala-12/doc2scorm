import { Component, OnInit}       from 'angular2/core';
import { User }     from './services/user';
import { UserService }     from './services/user.service';
import { ConverterService }     from './services/converter.service';
import { AdminComponent } from './admin.component';
import { DashboardComponent } from './dashboard.component';
import { UserEditComponent } from './user-edit.component';
import { ProfileComponent } from './profile.component';
import { Router, RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS } from 'angular2/router';

import {CORE_DIRECTIVES, NgClass} from 'angular2/common';

@RouteConfig([
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: DashboardComponent,
        useAsDefault: true
    },
    {
        path: '/admin',
        name: 'Admin',
        component: AdminComponent
    },
    {
        path: '/admin/:id',
        name: 'UserEdit',
        component: UserEditComponent
    },
    {
        path: '/profile',
        name: 'Profile',
        component: ProfileComponent
    }
])

@Component({
    selector: 'my-app',
    templateUrl: 'assets/app/app.component.html',
    styleUrls: ['assets/app/app.component.css'],
    inputs: ['route'],
    directives: [ROUTER_DIRECTIVES, CORE_DIRECTIVES, NgClass],
    providers: [
        ROUTER_PROVIDERS,
        UserService,
        ConverterService
    ]
})

export class AppComponent implements OnInit {
        
    title = 'Tour of Heroes';

    public user: User;
    public route = 'Dasboard';
    public showMenu = false;

    constructor(private _router: Router,
        private _userService: UserService) {
        _router.subscribe((val) => this.route = val);
    }

    ngOnInit() {
        this._userService.getCurrentUser()
            .then(user => this.user = user); ////console.log(user));
    }

    goto(component: String) {
        this._router.navigate([component, {}]);
    }

    isRouteActive(route: String) {
        return this._router.isRouteActive(this._router.generate([route]));
    }
    
}
