import {UserService} from './user.service';
import {User} from './user';
import {
    beforeEach,
    beforeEachProviders,
    describe,
    expect,
    it,
    inject,
    injectAsync
} from 'angular2/testing';
import {provide} from 'angular2/core';
import 'rxjs/Rx';
import {BaseRequestOptions, Response, ResponseOptions, Http} from 'angular2/http';
import {MockBackend, MockConnection} from 'angular2/http/testing';

class MockUserService extends UserService {
    public USERS: User[] = [
        { id: 1, name: "guest", email: "guest@email.com", role: "GUEST", registrationTime: 2000, docs: 2 },
        { id: 2, name: "user", email: "user@email.com", role: "USER", registrationTime: 2000, docs: 3 },
        { id: 3, name: "admin", email: "admin@email.com", role: "ADMIN", registrationTime: 2000, docs: 4 }]

    public CURRENT: User = { id: 4, name: "current", email: "current@email.com", role: "GUEST", registrationTime: 2000, docs: 5 }

    getCurrentUser() {
        return Promise.resolve(this.CURRENT);
    }

    getUsers() {
        return Promise.resolve(this.USERS);
    }

    getUserById(id: number) {
        var user = null;

        this.USERS.forEach(function(u, i, USERS) {
            if (u.id === id) {
                user = u;
            }
        });

        return Promise.resolve(user);
    }

    save(user: User) {
        return Promise.resolve(user);
    }
}

describe('UserService', () => {

    beforeEachProviders(() => [
        provide(UserService, { useClass: MockUserService }),
        BaseRequestOptions,
        MockBackend,
        provide(Http, {
            useFactory: (backend: MockBackend, defaultOptions: BaseRequestOptions) => {
                return new Http(backend, defaultOptions);
            },
            deps: [MockBackend, BaseRequestOptions]
        })
    ]);

    it('current user has name "current"', inject([UserService], (userService: UserService) => {
        //userService.getCurrentUser().then((u: User) => {expect(u.name).toBe("current")});
        expect(userService.CURRENT.name).toBe("current");
    }));

});