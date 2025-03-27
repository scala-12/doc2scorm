import { UserEditComponent } from './user-edit.component';
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

import { Location, Router, RouteRegistry, ROUTER_PROVIDERS, ROUTER_PRIMARY_COMPONENT, RouteParams } from 'angular2/router';
import { RootRouter } from 'angular2/src/router/router';
import { SpyLocation } from 'angular2/src/mock/location_mock';
import { AppComponent } from './app.component';

//setBaseTestProviders(TEST_BROWSER_PLATFORM_PROVIDERS, TEST_BROWSER_APPLICATION_PROVIDERS);

describe('UserEditComponent', () => {

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
        provide(Router, { useClass: RootRouter }),
        provide(RouteParams, { useValue: new RouteParams({ id: '' + MockUserData.USERS[1].id }) })
    ]);

    beforeEach(injectAsync([TestComponentBuilder], (tcb: TestComponentBuilder) => {

        return tcb
            .overrideProviders(UserEditComponent, [provide(UserService, { useClass: MockUserService }), CORE_DIRECTIVES])
            .createAsync(UserEditComponent)
            .then((componentFixture: ComponentFixture) => {
                this.listComponentFixture = componentFixture;
            });
    }));

    it('user name test', injectAsync([], () => {
        MockUserData.CURRENT.role = "ADMIN";
        return this.listComponentFixture.debugElement.componentInstance.ngOnInit().then(() => {
            this.listComponentFixture.detectChanges();
            const element = this.listComponentFixture.nativeElement;
            expect(element.querySelectorAll('#input-user-name')[0].value).toBe(MockUserData.USERS[1].name);
        });
    }));

    it('user email test', injectAsync([], () => {
        MockUserData.CURRENT.role = "ADMIN";
        return this.listComponentFixture.debugElement.componentInstance.ngOnInit().then(() => {
            this.listComponentFixture.detectChanges();
            const element = this.listComponentFixture.nativeElement;
            expect(element.querySelectorAll('#input-user-email')[0].value).toBe(MockUserData.USERS[1].email);
        });
    }));

});