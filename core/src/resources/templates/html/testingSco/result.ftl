<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Result</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link type="text/css" rel="stylesheet" href="test.css">
	<link type="text/css" rel="stylesheet" href="../${system_dir}/test.css">
	
	<script type="text/javascript" src="../${system_dir}/APIWrapper.js"></script>
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
		var markText="Ваша оценка за тест : ";
		var percents=0;
		var percents4markA=${percents4markA};
		var percents4markB=${percents4markB};
		var percents4markC=${percents4markC};
	
		var questsTable="";	 
		var old_el=null;
		var scoAnswersSourceJson=parent.doLMSGetValue('cmi.sco_answers_source_json');
	
		function showElement(el_id){
			var el=document.getElementById(el_id);
			if(el){
				if(old_el!=null) old_el.style.display="none";
				el.style.display="block";
				old_el=el;
			}
		}
	
		function hideElement(){
			if(old_el!=null){
				old_el.style.display="none";
				old_el=null;
			}
		}
	
		function getMark(percents){
			if(percents>=percents4markA){
				return 5;
			}else if(percents>=percents4markB){
				return 4;
			}else if(percents>=percents4markC){
				return 3;
			}else{
				return 2;
			}
		}
	
		function commentTest(testparam){
			var extraContent='';
			var header;
			if((parent.testParams[0] == 2)||(parent.testParams[0] == 3)||(parent.testParams[0] == 5)){
				header='Прохождение теста окончено';
			}else if(parent.testParams[0] == 4){
				header='Упражнение окончено!';
			}else{
				header='Прохождение теста окончено';
				extraContent='<p>Результаты теста вы сможете узнать у своего преподавателя (куратора).</p>';
			}
	
			var divMsg='<div id="end_test_msg">'
					+'<h2 align="center";>'+header+'</h2>'
					+extraContent
					+'<p>В левой части текущего окна перейдите по одной из гиперссылок для дальнейшего изучения разделов курса.</p>'
				+'</div>';
	
			return divMsg;
		}
	
		/* -- Success Status -- */
		function defuneSuccess_Status(){
			var sum=0;
			for(var j=0;j<parent.answers.length;++j)
				sum=sum+parseInt(parent.answers[j][1]);
			var pers=sum/parent.questions.length*100;
			if(pers>=percents4markC){
				parent.doLMSSetValue("cmi.success_status","passed");
			}else{
				parent.doLMSSetValue("cmi.success_status","failed");		
			}
		}
	
		/* -- количество верных ответов -- */
		function getCountOfRightAnswers(){
			var count=0;
			for(var i=0;i<parent.answers.length;++i){
				if(parent.answers[i][1]=="1"){
					++count;
				}else{
					count+=parseInt(parent.answers[i][1]);
				}
			}
	
			return count;
		}
	
		/* -- количество вопросов теста -- */
		function getCountOfQuestions(){
			return parent.answers.length;
		}
	
		function createAnswersReport(){
			var linkText='Посмотреть';
			var r='Правильный';
			var w='Неправильный';
	
			var testParam0 = parent.testParams[0]==4;
			var scoAnswersSource = null;
			if ((scoAnswersSourceJson !== null) && (scoAnswersSourceJson !== '')) {
				scoAnswersSource = JSON.parse(scoAnswersSourceJson);
			}
	
			for(var j = 0; j < parent.answers.length; j++){
				var questId="quest_"+(j+1);
				var questInfo=
					'<div class="quest_info" style="display:none" id="'+questId+'">'
						+'<span class="label_caption">Текст вопроса и варианты ответа:</span>'
						+'<div class="quest_info_item"><iframe style="height:100%" src="'+parent.answers[j][0]+'.html'+'"></iframe></div>'
						+((scoAnswersSource !== null)?
							(parent.answers[j][0] in scoAnswersSource)?
								'<div class="quest_comment">'
									+'<span class="label_caption">Комментарий к выбору ответа:</span>'
									+'<br/>'
									+'<div class="quest_info_item">'
										+'<div>'
											+'<span>'+scoAnswersSource[parent.answers[j][0]]+'</span>'
										+'</div>'
									+'</div>'
								+'</div>':''
							:'')
						+'<br/>'
						+'[<a href="#" onclick="hideElement()">Скрыть информацию</a>]'
					+'</div>';
				questsTable=questsTable
						+'<tr>'
							+'<td>'+(j+1)+'</td>' //was parent.answers[j][0] instead of j+1
							+'<td>'+ ((testParam0) ? 
									parent.answers[j][8]+" - количество ошибок"
									: ((parent.answers[j][1]==1)?r:w)) +'</td>'
			            	+'<td><a href="#" onclick="showElement(\''+questId+'\');">'+linkText+'</a></td>'
			            +'</tr>'
						+'<tr><td colspan="3">'+questInfo+'</td></tr>';
			}
	
			var answersSum=getCountOfRightAnswers();
			percents=answersSum/parent.answers.length*100;
			markText=markText+getMark(percents)+" ("+Math.round(percents)+"% правильных ответов)";
		}
	
		/* -- Задержка на время mSec -- */
		function pause(mSec){
			clock=new Date();
			justMinute=clock.getTime();
			while(true){
				just=new Date();
				if(just.getTime()-justMinute>mSec) break;
			}
		}
	
		function setInteractions(){
			if(parent.testParams[0]!=4){
				for(var j=0;j<parent.answers.length;++j){
					parent.doLMSSetValue("cmi.interactions."+j+".id",parent.answers[j][0]);
					parent.doLMSSetValue("cmi.interactions."+j+".type",parent.answers[j][2]);
					parent.doLMSSetValue("cmi.interactions."+j+".result",parent.answers[j][1]=="1"?"correct":"wrong");
					parent.doLMSSetValue("cmi.interactions."+j+".weighting",1);
					parent.doLMSSetValue("cmi.interactions."+j+".latency",parent.answers[j][7]);
					parent.doLMSSetValue("cmi.interactions."+j+".timestamp",parent.answers[j][6]);
					parent.doLMSSetValue("cmi.interactions."+j+".learner_response",parent.answers[j][4]);
					parent.doLMSSetValue("cmi.interactions."+j+".correct_responses.0.pattern",parent.answers[j][5]);
				}
			}
			parent.doLMSSetValue("cmi.score.raw",getCountOfRightAnswers());
			parent.doLMSSetValue("cmi.score.max",getCountOfQuestions());
			parent.doLMSSetValue("cmi.score.min","0");
			if(parent.testParams[0]==6){
				parent.doLMSSetValue("cmi.suspend_data","done");
			}else{
				parent.doLMSSetValue("cmi.suspend_data","");
			}
			defuneSuccess_Status();
			parent.doLMSSetValue("cmi.completion_status","completed");
			var s=parent.doLMSGetValue("cmi.completion_status");
			parent.isTerminate=true;
	     	var s=parent.doLMSGetValue("cmi.completion_status");
			parent.unloadPage(s);
		}
	</script>
</head>
<body>
	<script>
		setInteractions();
		document.write(commentTest(parent.testParams[0]));

		if((parent.testParams[0]==2)||(parent.testParams[0]==3)||(parent.testParams[0]==5)||(parent.testParams[0]==4)){
			createAnswersReport();
			document.write(markText);
			if(parent.testParams[0]==4){
				var summ=0;
				for(var i=0;i<parent.answers.length;++i)
					summ+=parent.answers[i][8];
				document.write("<p id=\"error_count\">"+ summ + " - количество допущенных ошибок во всем тесте</p>");
			}else{
				document.write("<hr/>");
			}
			
			document.write("<table id=\"test_results\">");
			document.write("<caption>Результаты прохождения теста</caption>");
				document.write("<tr><td>№ вопроса</td><td>Ответ</td><td>Текст вопроса</td></tr>");
				document.write(questsTable);
			document.write("</table>");
		}else{
			var answersSum=0;
			var lesStatus="";
		  	document.write("<h2>Тест окончен</h2>");
		  	lesStatus=parent.doLMSGetValue("cmi.completion_status");
			if(lesStatus!="completed") percents=answersSum/parent.questions.length * 100;
		}
	</script>
</body>
</html>
