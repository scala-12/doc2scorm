import { UserService } from './user.service';
import { User } from './user';
import {
    beforeEach,
    beforeEachProviders,
    describe,
    expect,
    it,
    inject,
    injectAsync
} from 'angular2/testing';
import { provide } from 'angular2/core';
import { BaseRequestOptions, Response, ResponseOptions, Http } from 'angular2/http';
import { MockBackend, MockConnection } from 'angular2/http/testing';
import 'rxjs/Rx';

export class MockUserData {
    static CURRENT: User = { id: 4, name: "current", email: "current@email.com", role: "GUEST", registrationTime: 2000, successDocs: 5, allDocs: 10 }
    static USERS: User[] = [
        { id: 1, name: "guest", email: "guest@email.com", role: "GUEST", registrationTime: 2000, successDocs: 2, allDocs: 3 },
        { id: 2, name: "user", email: "user@email.com", role: "USER", registrationTime: 2000, successDocs: 3, allDocs: 3 },
        { id: 3, name: "admin", email: "admin@email.com", role: "ADMIN", registrationTime: 2000, successDocs: 4, allDocs: 5 }];
}

export class MockUserService extends UserService {

    getCurrentUser() {
        return Promise.resolve(MockUserData.CURRENT);
    }

    getUsers() {
        return Promise.resolve(MockUserData.USERS);
    }

    getUserById(id: number) {
        var user = null;

        MockUserData.USERS.forEach(function(u, i, USERS) {
            if (u.id === id) {
                user = u;
            }
        });

        return Promise.resolve(user);
    }

    save(user: User) {
        var updated = false;

        MockUserData.USERS.forEach(function(u, i, USERS) {
            if (u.id === user.id) {
                Object.assign(u, user);
            }
        });
        return Promise.resolve(updated);
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

    it('get current user test', injectAsync([UserService], (userService: UserService) => {
        return userService.getCurrentUser().then((u: User) => { expect(u.name).toBe(MockUserData.CURRENT.name) });
    }));

    it('get users test', injectAsync([UserService], (userService: UserService) => {
        return userService.getUsers().then((users: User[]) => { expect(users.length).toBe(MockUserData.USERS.length) });
    }));

    it('get user by id test', injectAsync([UserService], (userService: UserService) => {
        return userService.getUserById(MockUserData.USERS[1].id).then((user: User) => { expect(user.name).toBe(MockUserData.USERS[1].name) });
    }));

    it('save user test', injectAsync([UserService], (userService: UserService) => {
        var u = Object.assign({}, MockUserData.USERS[2]);
        u.name += "_new";

        return userService.save(u).then(() => { expect(MockUserData.USERS[2].name).toBe(u.name) });
    }));

});