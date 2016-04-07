System.config({
	packages : {
		'assets/app' : {
			format : 'register',
			defaultExtension : 'js'
		},
		'assets/lib/ng2-bootstrap' : {
			defaultExtension : "js"
		}
		,
		'assets/lib' : {
			defaultExtension : "js"
		}
	},
	
	paths : {
		"ng2-bootstrap/ng2-bootstrap" : 'assets/lib/ng2-bootstrap/ng2-bootstrap'
	},
	map : {
		moment : 'assets/lib/moment/moment.js'
	}
});
System.import('assets/app/main').then(null, console.error.bind(console));