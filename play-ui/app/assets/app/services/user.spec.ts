import { User } from './user';

describe('User', () => {

    it('user name test', () => {
        let user: User = { id: 1, name: "user1", email: "user1@email.com", role: "ADMIN", registrationTime: 2000, successDocs: 2, allDocs: 5 };
        expect(user.name).toEqual('user1');
    });

    it('user id test', () => {
        let user: User = { id: 1, name: "user1", email: "user1@email.com", role: "ADMIN", registrationTime: 2000, successDocs: 2, allDocs: 5 };
        expect(user.id).toEqual(1);
    });

});