import { User } from './user';

describe('User', () => {
  it('has name', () => {
    let user: User = {id: 1, name: "user1", email: "user1@email.com", role: "ADMIN", registrationTime: 2000, docs: 2};
    expect(user.name).toEqual('user1');
  });
  it('has id', () => {
    let user: User = {id: 1, name: "user1", email: "user1@email.com", role: "ADMIN", registrationTime: 2000, docs: 2};
    expect(user.id).toEqual(1);
  });
});