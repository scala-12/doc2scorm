System.config({
	packages : {
		"assets/app" : {
			format : "register",
			defaultExtension : "js"
		},
		"assets/lib" : {
			defaultExtension : "js"
		}
	}
});


// System.paths = {
// "angular2/testing" : "assets/lib/angular2/bundles/testing.dev.js",
// "angular2/core" : "assets/lib/angular2/core.js",
// "angular2/http" : "assets/lib/angular2/bundles/http.dev.js",
// };

// #3. Import the spec files explicitly
Promise.all([
  System.import("assets/app/services/user.spec"),
  System.import("assets/app/services/user.service.spec"),
  System.import("assets/app/profile.component.spec"),
  System.import("assets/app/admin.component.spec"),
  System.import("assets/app/user-edit.component.spec"),
  System.import("assets/app/dashboard.component.spec")
])

  // #4. wait for all imports to load ...
  // then re-execute `window.onload` which
  // triggers the Jasmine test-runner start
  // or explain what went wrong.
  .then(window.onload)
  .catch(console.error.bind(console));