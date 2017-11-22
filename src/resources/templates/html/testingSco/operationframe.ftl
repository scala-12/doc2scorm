<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Тест</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script type="text/javascript" src="../${system_dir}/SCOFunctions.js"></script>
<script type="text/javascript" src="../${system_dir}/jquery-${jquery_ver}.min.js"></script>
<script type="text/javascript" src="../${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.js"></script>
<link rel="stylesheet" href="../${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.css">
<link rel="stylesheet" href="../${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.structure.css">
<link rel="stylesheet" href="../${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.theme.css">
<script type="text/javascript">
	var testingStartDate=null;
	
	var startQuest=null;
	var stopQuest=null;
	var questIndex=0;
	
	var timerID=0;
	
	var hasEndTime=false;
	var timeLimit = null;
	
	var stTestTime=0;
	
	var isFirstQuest=true;

	function milisecondsToDate(miliseconds){
		var date = new Date(miliseconds);
		var year = date.getFullYear();
		var month=date.getMonth()+1;
		var day=date.getDate();
		var hour=date.getHours();
		var minute=date.getMinutes();
		var second=date.getSeconds();
		if((new String(month)).length<2){
			month="0"+month;
		}
		if((new String(day)).length<2){
			day="0"+day;
		}
		if((new String(hour)).length<2){
			hour="0"+hour;
		}
		if((new String(minute)).length<2){
			minute="0"+minute;
		}
		if((new String(second)).length<2){
	      second="0"+second;
		}
		
		return year
				+"-"+month
				+"-"+day
				+"T"+hour
				+":"+minute
				+":"+second;
	}

	function updateTimer(){
	    if(!startQuest) {
		    return;
		}
	    var currentDate=new Date().getTime();
		var testTime = currentDate-testingStartDate;
	    $('#question_time').val(convertTotalSeconds(Math.round((currentDate-startQuest)/1000)));
	    timerID=setTimeout("updateTimer()",1000);
	    // ограничение времени
	    if (timeLimit == null) {
	    	timeLimit = parent.doLMSGetValue("cmi.max_time_allowed");
	    	if ((timeLimit + '').length == 0) {
	    		timeLimit = -1;
	    	}
	    }
		if (timeLimit < 0) {
			if (isFirstQuest) {
				isFirstQuest = false;
				$('.without_timeout').show();
				$('.with_timeout').hide();
			}
			$('#total_time').val(convertTotalSeconds(Math.round(testTime/1000)));
		} else {
			if (isFirstQuest) {
				isFirstQuest = false;
				$('.with_timeout').show();
				$('.without_timeout').hide();
			}
			$('#balance_time').val(convertTotalSeconds(Math.round((timeLimit-testTime)/1000)));
			if(testTime+parseInt(stTestTime)>timeLimit) {
				stopQuest=new Date().getTime();
				var duration=(stopQuest-startQuest)/1000;
				for (var i = questIndex; i < parent.questions.length; ++i) {
					parent.ArrTimestamp[i]=milisecondsToDate(stopQuest);
					parent.ArrLatency[i]=convertTotalSeconds(duration);
					parent.ddd=startQuest-testingStartDate+parseInt(stTestTime);
					
					parent.justSubmitAnswer(i + 1, '', false, '');
					stopQuest = 0;
					duration = 0;
					startQuest = new Date().getTime();
				}
				
			
				if(parent.testParams[0]==6){
					parent.doLMSSetValue("cmi.suspend_data","done");
				}else{
					parent.doLMSSetValue("cmi.suspend_data","");
				}
				parent.frames["question"].location.href="stopalert.html";
				parent.frames["botton"].location.href="empty.html";
				
				return;
			}
		}
	}

	function next(){
		if(!testingStartDate){
			testingStartDate=new Date().getTime();
			if(parent.testParams[2])
				stTestTime=parent.testParams[2];
		}
		if(startQuest){
			//Анализ продолжительности и начала ответа
			stopQuest=new Date().getTime();
			var duration=(stopQuest-startQuest)/1000;		
			if((startQuest!=null)&&(stopQuest!=null)){
				parent.ArrTimestamp[questIndex]=milisecondsToDate(stopQuest);
				parent.ArrLatency[questIndex]=convertTotalSeconds(duration);
				parent.ddd=startQuest-testingStartDate+parseInt(stTestTime);
				++questIndex;
			}	
		} else {
			timerID=setTimeout("updateTimer()", 1000);
			$("#timer").show();
		}
		
		var oldAnsNumber=parent.answers.length;
		parent.submitAnswer();
		if((!startQuest)||(oldAnsNumber!=parent.answers.length)){
			startQuest=new Date().getTime();
		}
		var nextQuestIndex = parent.answers.length+1;
		var questCount = parent.questions.length;
		$("#question").val((nextQuestIndex - ((nextQuestIndex <= questCount) ? 0 : 1)) + ' из ' + questCount);
		$("#qid").val(parent.currentQuestion);
		countMark();
	}

	function stopTimer(){
		if(timerID){
			clearTimeout(timerID);
			timerID=0;
		}
		startQuest=null;
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
		$("#percents").val(percents+'%');
	}
	$( function() {
		$('.admin_info').hide();
		$('input[type=submit],input[type=button],button').button();
	});
</script>
</head>
<body onUnload="stopTimer()">
	<table border="0" style="width:100%">
	<tbody>
		<tr>
			<td align="left" style="vertical-align: middle;">
				<input id="btn" style="display: none;" type="button" onClick="next()" value="Продолжить">
			</td>
			<td align="right" style="font: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; text-align: left;">
				<table id="timer" style="display: none;">
				<tbody style='vertical-align:top'>
					<tr>
						<td style='text-align:right'>Затрачено времени&nbsp;&nbsp;<strong>на вопрос:</strong></td>
						<td><input style="border-width: 0px; background-color: #FFFFFF" type="text" id="question_time" size="8" disabled></td>
						<td rowspan='2'>
							<strong>Вопрос:</strong>
							<input style="border-width: 0px; background-color: #FFFFFF" type="text" id="question" size="10" value="" disabled>
						</td>
						<td rowspan='2' class='admin_info'>
							<strong>Правильных ответов:</strong>
							<input style="border-width: 0px; background-color: #FFFFFF" type="text" id="percents" size="4" value="" disabled>
						</td>
						<td rowspan='2' class='admin_info'>
							<strong>ID:</strong>
							<input style="border-width: 0px; background-color: #FFFFFF" type="text" id="qid" size="4" value="" disabled>
						</td>
					</tr>
					<tr>
						<td style='text-align:right'>
							<strong class='without_timeout' style='display:none;'>всего:</strong>
							<strong class='with_timeout' style='display:none;'>Осталось времени:</strong>
						</td>
						<td>
							<input class='without_timeout' style="border-width:0px;background-color:#FFFFFF;display:none;" type="text" id="total_time" size="8" value="" disabled>
							<input class='with_timeout' style="border-width:0px;background-color:#FFFFFF;display:none;" type="text" id="balance_time" size="8" value="" disabled>
						</td>
					</tr>
				</tbody>
				</table>
			</td>
		</tr>
	</tbody>
	</table>
</body>
</html>
