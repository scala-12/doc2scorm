import {Injectable} from 'angular2/core';
import {User} from './user';
import {Http, HTTP_PROVIDERS} from 'angular2/http';

@Injectable()
export class UserService {

    constructor(private _http: Http) { }

    getCurrentUser() {
        return this._http
            .get('/rest/current/user')
            .map((response) => {
                return response.json()
            })
            .toPromise();
    }

    getUsers() {
        return this._http
            .get('/rest/users')
            .map((response) => {
                return response.json()
            })
            .toPromise();
    }

    getUserById(id: number) {
        return this._http
            .get('/rest/users/' + id)
            .map((response) => {
                return response.json()
            })
            .toPromise();
    }

    save(user: User) {
        return new Promise((resolve, reject) => {
            let xhr: XMLHttpRequest = new XMLHttpRequest();

            xhr.onreadystatechange = () => {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        resolve("success");
                    } else {
                        reject("fail");
                    }
                }
            };

            xhr.open('POST', '/rest/users/' + user.id, true);
            xhr.setRequestHeader("Content-Type", "application/json");
            let data = JSON.stringify(user);
            console.log(data);
            xhr.send(data);
        });
    }
}        