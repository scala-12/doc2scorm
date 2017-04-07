<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>${page_title}</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	
	<script type="text/javascript" src="${system_dir}/APIWrapper.js"></script>
	<script type="text/javascript" src="${system_dir}/SCOFunctions.js"></script>
	<script type="text/javascript" src="${system_dir}/jquery-${jquery_ver}.min.js"></script>
	<script type="text/javascript" src="${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.js"></script>
	<link rel="stylesheet" href="${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.css">
	<link rel="stylesheet" href="${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.structure.css">
	<link rel="stylesheet" href="${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.theme.css">
	
	<link rel="stylesheet" href="${system_dir}/${theory_css}">
	<link rel="stylesheet" href="${system_dir}/${course_css}">
	
	<script type="text/javascript" async
			src="https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-MML-AM_CHTML">
		MathJax.Hub.Config({
			showMathMenu: false,
			showMathMenuMSIE: false
		});
	</script>
	
	<script type="text/javascript">
		$( function() {
			$('input[type=submit],input[type=button],button').button();
			$('fieldset input[type="radio"],fieldset input[type="checkbox"]').checkboxradio();
			$( 'fieldset' ).controlgroup().css('display', 'block');
		});
	</script>
</head>
<body onLoad="loadPage()" onunload="return unloadPage('completed')">
${body_content}
</body>
</html>