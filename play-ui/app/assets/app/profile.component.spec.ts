import { ProfileComponent } from './profile.component';
import { MockUserService, MockUserData } from  './services/user.service.spec';
import {
    beforeEach,
    beforeEachProviders,
    describe,
    expect,
    it,
    inject,
    injectAsync,
    TestComponentBuilder,
    ComponentFixture,
    setBaseTestProviders
} from 'angular2/testing';
import {
    TEST_BROWSER_PLATFORM_PROVIDERS,
    TEST_BROWSER_APPLICATION_PROVIDERS
} from 'angular2/platform/testing/browser';
import { provide } from 'angular2/core';
import { CORE_DIRECTIVES, FORM_DIRECTIVES } from 'angular2/common';
import { UserService } from './services/user.service';
import 'rxjs/Rx';
import { BaseRequestOptions, Response, ResponseOptions, Http } from 'angular2/http';
import { MockBackend, MockConnection } from 'angular2/http/testing';

setBaseTestProviders(TEST_BROWSER_PLATFORM_PROVIDERS, TEST_BROWSER_APPLICATION_PROVIDERS);

describe('ProfileComponent', () => {

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

    beforeEach(injectAsync([TestComponentBuilder], (tcb: TestComponentBuilder) => {

        return tcb
            .overrideProviders(ProfileComponent, [provide(UserService, { useClass: MockUserService }), CORE_DIRECTIVES])
            .createAsync(ProfileComponent)
            .then((componentFixture: ComponentFixture) => {
                this.listComponentFixture = componentFixture;
            });
    }));

    it('properties count test', injectAsync([], () => {
        return this.listComponentFixture.debugElement.componentInstance.ngOnInit().then(() => {
            this.listComponentFixture.detectChanges();
            const element = this.listComponentFixture.nativeElement;
            expect(element.querySelectorAll('span').length).toBe(4);
        });
    }));

    it('show user name test', injectAsync([], () => {
        return this.listComponentFixture.debugElement.componentInstance.ngOnInit().then(() => {
            this.listComponentFixture.detectChanges();
            const element = this.listComponentFixture.nativeElement;
            expect(element.querySelectorAll('#user-name')[0].textContent).toBe(MockUserData.CURRENT.name);
        });
    }));

    it('show user email test', injectAsync([], () => {
        return this.listComponentFixture.debugElement.componentInstance.ngOnInit().then(() => {
            this.listComponentFixture.detectChanges();
            const element = this.listComponentFixture.nativeElement;
            expect(element.querySelectorAll('#user-email')[0].textContent).toBe(MockUserData.CURRENT.email);
        });
    }));

    it('show user registration time test', injectAsync([], () => {
        return this.listComponentFixture.debugElement.componentInstance.ngOnInit().then(() => {
            this.listComponentFixture.detectChanges();
            const element = this.listComponentFixture.nativeElement;
            var regTime = this.listComponentFixture.debugElement.componentInstance.formatDate(MockUserData.CURRENT.registrationTime)
            expect(element.querySelectorAll('#user-reg-time')[0].textContent).toBe(regTime);
        });
    }));

    it('show user success docs test', injectAsync([], () => {
        return this.listComponentFixture.debugElement.componentInstance.ngOnInit().then(() => {
            this.listComponentFixture.detectChanges();
            const element = this.listComponentFixture.nativeElement;
            expect(element.querySelectorAll('#user-success-docs')[0].textContent).toBe('' + MockUserData.CURRENT.successDocs);
        });
    }));

});