<#if html_type == html_type_5>
<!DOCTYPE html>
<#elseif html_type == html_type_4_01>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
</#if>
<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link type="text/css" rel="stylesheet" href="../${system_dir}/course.css">
<link type="text/css" rel="stylesheet" href="../${system_dir}/test.css">
<link type="text/css" rel="stylesheet" href="../user_course.css">
<link type="text/css" rel="stylesheet" href="../user_test.css">
<link type="text/css" rel="stylesheet" href="user_test.css">
	<script type="text/javascript">
	function show(){
		parent.frames["question"].location.href="result.html?time="+(new Date().getTime());
		return;
	}

	function start(){
		setTimeout('show()',4000);
	}
</script>
</head>
<body onload = "start()"><h1>Ваше время истекло!!!</h1>Результаты теста загружаются...</body>
</html>
