import { AdminComponent } from './admin.component';
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

import { Location, Router, RouteRegistry, ROUTER_PROVIDERS, ROUTER_PRIMARY_COMPONENT } from 'angular2/router';
import { RootRouter } from 'angular2/src/router/router';
import { SpyLocation } from 'angular2/src/mock/location_mock';
import { AppComponent } from './app.component';

//setBaseTestProviders(TEST_BROWSER_PLATFORM_PROVIDERS, TEST_BROWSER_APPLICATION_PROVIDERS);

describe('AdminComponent', () => {

    beforeEachProviders(() => [
        provide(UserService, { useClass: MockUserService }),
        BaseRequestOptions,
        MockBackend,
        provide(Http, {
            useFactory: (backend: MockBackend, defaultOptions: BaseRequestOptions) => {
                return new Http(backend, defaultOptions);
            },
            deps: [MockBackend, BaseRequestOptions]
        }),
        RouteRegistry,
        provide(Location, { useClass: SpyLocation }),
        provide(ROUTER_PRIMARY_COMPONENT, { useValue: AppComponent }),
        provide(Router, { useClass: RootRouter })
    ]);

    beforeEach(injectAsync([TestComponentBuilder], (tcb: TestComponentBuilder) => {

        return tcb
            .overrideProviders(AdminComponent, [provide(UserService, { useClass: MockUserService }), CORE_DIRECTIVES])
            .createAsync(AdminComponent)
            .then((componentFixture: ComponentFixture) => {
                this.listComponentFixture = componentFixture;
            });
    }));

    it('user access test', injectAsync([], () => {
        MockUserData.CURRENT.role = "USER";
        return this.listComponentFixture.debugElement.componentInstance.ngOnInit().then(() => {
            this.listComponentFixture.detectChanges();
            const element = this.listComponentFixture.nativeElement;
            expect(element.querySelectorAll('tr').length).toBe(0);
        });
    }));

    it('admin access test', injectAsync([], () => {
        MockUserData.CURRENT.role = "ADMIN";
        return this.listComponentFixture.debugElement.componentInstance.ngOnInit().then(() => {
            this.listComponentFixture.detectChanges();
            const element = this.listComponentFixture.nativeElement;
            expect(element.querySelectorAll('tr').length).toBe(3);
        });
    }));

    it('count of documents', injectAsync([], () => {
        MockUserData.CURRENT.role = "ADMIN";
        return this.listComponentFixture.debugElement.componentInstance.ngOnInit().then(() => {
            this.listComponentFixture.detectChanges();
            const element = this.listComponentFixture.nativeElement;
            for (var i = 0; i < MockUserData.USERS.length; i++) {
                expect(element.querySelectorAll('.success-document-count')[i].textContent).toBe("" + MockUserData.USERS[i].successDocs);
                expect(element.querySelectorAll('.all-document-count')[i].textContent).toBe("" + MockUserData.USERS[i].allDocs);
            }
        });
    }));

});