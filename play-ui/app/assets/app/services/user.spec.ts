import { User } from './user';

describe('User', () => {

    it('user name test', () => {
        let user: User = { id: 1, name: "user1", email: "user1@email.com", role: "ADMIN", registrationTime: 2000, docs: 2 };
        expect(user.name).toEqual('user1');
    });

    it('user id test', () => {
        let user: User = { id: 1, name: "user1", email: "user1@email.com", role: "ADMIN", registrationTime: 2000, docs: 2 };
        expect(user.id).toEqual(1);
    });

});