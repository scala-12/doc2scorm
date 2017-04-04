<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Untitled Document</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link rel="stylesheet" href="../${system_dir}/${course_css}">
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
