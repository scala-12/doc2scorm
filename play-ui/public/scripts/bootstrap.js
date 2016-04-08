System.config({
	packages : {
		'assets/app' : {
			format : 'register',
			defaultExtension : 'js'
		},
		'assets/lib' : {
			defaultExtension : "js"
		}
	},
	map : {
		moment : 'assets/lib/moment/moment.js'
	}
});
System.import('assets/app/main').then(null, console.error.bind(console));