import { DashboardComponent } from './dashboard.component';
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
import { ConverterService } from './services/converter.service';
import { BaseRequestOptions, Response, ResponseOptions, Http } from 'angular2/http';
import { MockBackend, MockConnection } from 'angular2/http/testing';
import 'rxjs/Rx';

//setBaseTestProviders(TEST_BROWSER_PLATFORM_PROVIDERS, TEST_BROWSER_APPLICATION_PROVIDERS);

class MockConverterService extends ConverterService {

    public uploadDoc(file: File): Promise<any> {
        return Promise.resolve("");
    }

    public downloadScorm(courseName: String, headerLevel: number): Promise<string> {
        return Promise.resolve("");
    }

    public previewScorm(headerLevel: number): Promise<string> {
        return Promise.resolve("");
    }

}

describe('DashboardComponent', () => {

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
            .overrideProviders(DashboardComponent,
            [provide(UserService, { useClass: MockUserService }), provide(ConverterService, { useClass: MockConverterService }), CORE_DIRECTIVES])
            .createAsync(DashboardComponent)
            .then((componentFixture: ComponentFixture) => {
                this.listComponentFixture = componentFixture;
            });
    }));

    it('user access test', injectAsync([], () => {
        MockUserData.CURRENT.role = "USER";
        return this.listComponentFixture.debugElement.componentInstance.ngOnInit().then(() => {
            this.listComponentFixture.detectChanges();
            const element = this.listComponentFixture.nativeElement;
            expect(element.querySelectorAll('.converter').length).toBe(1);
        });
    }));

    it('guest access test', injectAsync([], () => {
        MockUserData.CURRENT.role = "GUEST";
        return this.listComponentFixture.debugElement.componentInstance.ngOnInit().then(() => {
            this.listComponentFixture.detectChanges();
            const element = this.listComponentFixture.nativeElement;
            expect(element.querySelectorAll('.converter').length).toBe(0);
        });
    }));

});