<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<script type="text/javascript" src="../${system_dir}/SCOFunctions.js"></script>
	<script type="text/javascript" src="../${system_dir}/jquery-${jquery_ver}.min.js"></script>
	<script type="text/javascript" src="../${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.js"></script>
	<link rel="stylesheet" href="../${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.css">
	<link rel="stylesheet" href="../${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.structure.css">
	<link rel="stylesheet" href="../${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.theme.css">
	<script type="text/javascript">
		$( function() {
			$('input[type=submit],input[type=button],button').button();
			$('fieldset input[type="radio"],fieldset input[type="checkbox"]').checkboxradio();
			$( 'fieldset' ).controlgroup().css('display', 'block');
		});
	</script>
	
<script type="text/javascript">
	var a=new Array();
	var b=new Array();
	var startQ=null;
	var stopQ=null;
	var duration=null;
	var i=0;
	var stTestTime=0;

	function time(Miliseconds){
		var NewDate=new Date(Miliseconds);
		YYYY=NewDate.getFullYear();
		MM=NewDate.getMonth()+1;
		DD=NewDate.getDate();
		hh=NewDate.getHours();
		mm=NewDate.getMinutes();
		ss=NewDate.getSeconds();
		if((new String(MM)).length<2){
			MM="0"+MM;
		}
		if((new String(DD)).length<2){
			DD="0"+DD;
		}
		if((new String(hh)).length<2){
			hh="0"+hh;
		}
		if((new String(mm)).length<2){
			mm="0"+mm;
		}
		if((new String(ss)).length<2){
	      ss="0"+ss;
		}
		val=YYYY+"-"+MM
				+"-"+DD
				+"T"+hh
				+":"+mm
				+":"+ss;
		
		return val;
	}

	var questionDate=null;
	var startDate=null;
	var timerID=0;
	// ограничение времени
	var isAnd=false;
	var stoptime=0;

	function updateTimer(){
	    if(!questionDate)
		    return;
	    var currentDate=new Date().getTime();
	    var elapsedQuestionSeconds=Math.round((currentDate-questionDate)/1000);
	    var elapsedTotalSeconds=Math.round((currentDate-startDate)/1000);
	    document.operationForm.totalTime.value=convertTotalSeconds(elapsedTotalSeconds);
	    document.operationForm.questionTime.value=convertTotalSeconds(elapsedQuestionSeconds);
	    timerID=setTimeout("updateTimer()",1000);
	    // ограничение времени
		if(stoptime !=0)
			if(currentDate-startDate+parseInt(stTestTime)>stoptime*60*1000)
				isAnd=true;
		if(isAnd){
			if(parent.testParams[0]==6){
				doLMSSetValue("cmi.suspend_data","done");
			}else{
				doLMSSetValue("cmi.suspend_data","");
			}
			parent.frames["question"].location.href="stopalert.html";
			parent.frames["botton"].location.href="empty.html";
			
			return;
		}
	}

	function next(){
		if(!startDate){
			startDate=new Date().getTime();
			if(parent.testParams[2])
				stTestTime=parent.testParams[2];
		}
		if(!questionDate){
			timerID=setTimeout("updateTimer()", 1000);
			document.getElementById("timer").style.display = "inline";
		}
		//Анализ продолжительности и начала ответа
		if(!startQ){
			startQ=new Date().getTime();
		} else {
	   		stopQ=new Date().getTime();
			duration=(stopQ-startQ)/1000;
			startQ=new Date().getTime();
		}
	    if((startQ!=null)&&(stopQ!=null)){
			parent.ArrTimestamp[i]=time(stopQ);
			parent.ArrLatency[i]=convertTotalSeconds(duration);
			parent.ddd=questionDate-startDate+parseInt(stTestTime);
			++i;
		}
		var oldAnsNumber=parent.answers.length;
		parent.submitAnswer();
		if((!questionDate)||(oldAnsNumber!=parent.answers.length)){
			questionDate=new Date().getTime();
		}
		document.getElementById("question").value=(parent.answers.length+1)+'  из  '+parent.questions.length;
		document.getElementById("qid").value=parent.currentQuestion;
		countMark();
	}

	function stopTimer(){
		if(timerID){
			clearTimeout(timerID);
			timerID=0;
		}
		questionDate=null;
	}
	
	function countMark(){
		var answersSum=0;
		var minPercent=0;
		var questionsSum=0;
		for(var j=0;j<parent.answers.length;++j)
			answersSum=answersSum+parseInt(parent.answers[j][1]);
		questionsSum=parent.questions.length;
		minPercent=Math.round(100/questionsSum);
		var percents=0;
		if(parent.answers.length>0)
			if(minPercent*answersSum-Math.round(minPercent*answersSum)>=0){
				percents=Math.round(minPercent*answersSum);
			}else{
				percents=Math.round(minPercent*answersSum)-1;
			}
		document.getElementById("percents").value=percents+'%';
	}
</script>
</head>
<body onUnload="stopTimer()">
	<form name="operationForm" method="post" action="" align="right">
		<table border="0" width="100%">
			<tr>
				<td align="left"><input id="btn" style="display: none;"
					name="Submit" type="button" onClick="next()" value="Продолжить">
				</td>
				<td align="right"
					style="font: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; text-align: left;">
					<span id="timer" style="display: none;"> &nbsp;&nbsp;
						Затрачено всего: <input
						style="border-width: 0px; background-color: #FFFFFF" type="text"
						id="totalTime" name="totalTime" size="8" value="" disabled />
						&nbsp;&nbsp; на вопрос: <input
						style="border-width: 0px; background-color: #FFFFFF" type="text"
						id="questionTime" name="questionTime" size="8" disabled /> |
						&nbsp;&nbsp; Вопрос: &nbsp;&nbsp;<input
						style="border-width: 0px; background-color: #FFFFFF" type="text"
						id="question" name="question" size="10" value="" disabled /> |
						&nbsp;&nbsp; Правильных ответов: <input
						style="border-width: 0px; background-color: #FFFFFF" type="text"
						id="percents" name="percents" size="4" value="" disabled />
						&nbsp;&nbsp; ID: <input
						style="border-width: 0px; background-color: #FFFFFF" type="text"
						id="qid" name="qid" size="4" value="" disabled />
				</span>&nbsp;
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
