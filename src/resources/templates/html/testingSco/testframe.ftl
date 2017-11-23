<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
<html>
<head>
<title>Тестирование</title>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta NAME="Pragma" CONTENT="no-cache">
<meta NAME="Cache-Control" CONTENT="no-cache">

<script type="text/javascript" src="../${system_dir}/APIWrapper.js"> </script>
<script type="text/javascript" src="../${system_dir}/SCOFunctions.js"> </script>
<script type="text/javascript" src="../${system_dir}/parser.js"> </script>
<script type="text/javascript" src="../${system_dir}/jquery-${jquery_ver}.min.js"> </script>
<script type="text/javascript">
	String.prototype.trim=function(){
		return this.replace(/^[\s\n\t]*|[\s\n\t]*$/g,"");
	}
	
	var date = new Date().getTime();

	//Начало ответа
	var ArrTimestamp=new Array();	
	//продолжительность ответа
	var ArrLatency=new Array();	
	//array of all questions loaded from the system
	//each element in the array has the following structure 
	//question_id|correct_answer~correct_answer
	var questions=new Array();	
	//array of answered questions to disable duplication
	//question_id|answer (answer:  "0" - incorrect , "1" - correct) 
	var answers=new Array();	
	//variable to store current question being presented
	var currentQuestion=-1;	
	var testParams=new Array();

	//для шестого типа
	itog=false;

	//для упражнения
	var col_wrong=0;

	//длительнотсь теста
	var ddd=0; 

	function findQuestion(questionNumber,questionsArray){
		for(var i=0;i<questionsArray.length;++i){ 
			var q=questionsArray[i][0];
			if(q==questionNumber)
				return i;
		}

		return -1;
	}

	//массив хранит коды ошибок
	var errorList=new Array();
	errorList[errorList.length]=1;

	// Function to parse data from the launch_data field of the cmi object model
	function start() {
		var col_loading=30;
		if(getErrorString||doLMSGetValue){
			loadPage();
			
			var timerId = setInterval(
				function() {
					var frame = $(window.frames["botton"].document);
					if (frame.length) {
						var startButton = frame.find('#btn');
						if (startButton.length) {
							startButton.show();
							clearInterval(timerId);
						}
					}
				},
				500
			);
			
			errorList=new Array();
		}else if(col_loading != 0){
			--col_loading; 
			var timerLoad=setTimeout('start', 1000);
			return;
		}else{
			window.frames["question"].document.body.innerHTML="Проблема загрузки ...<br>apiwraper.js не найден";
			return;
		}

		if(timerLoad)
			clearTimeout(timerLoad);
		
		var status=doLMSGetValue("cmi.core.lesson_status");
		var completeStatus=doLMSGetValue("cmi.core.completion_status");
		var suspendData=(""+doLMSGetValue("cmi.suspend_data")).split("\n");

		if(doLMSGetValue("cmi.suspend_data")=="done"){
			itog=true;
			doLMSSetValue("cmi.suspend_data","done");
			doLMSCommit();
			doLMSFinish();
		}

  		if((status=="incomplete")&&(suspendData.length > 1)){
			if(suspendData){
				testParams=suspendData[0].split("|");
				for(var i=1;i<=testParams[1];++i)
					questions[questions.length]=suspendData[i].split("|");
				for(var i=questions.length+1;i<suspendData.length;++i)
					answers[answers.length]=suspendData[i].split("|");
			}
		}else{
			var launch_data=""+doLMSGetValue("cmi.launch_data");
			launch_data=launch_data.trim();
			// каждая строка это вопрос, кроме первой - это тип теста+параметры специфические для этого типа теста
			var a=launch_data.split("\n");
			var tmpArray=new Array();
			var selectedQuestions = new Array(); // номера выбранных вопросов
			if(a){
				for(var i=1;i<a.length;++i){
					var t=""+a[i];
					tmpArray[tmpArray.length] = t.split("|");
				}
				var numberOfQuestions=0; // by default - questions selected in turn
				a[0]=a[0].trim();
				testParams=a[0].split("|"); // получаем тип теста + параметры специфические для этого типа теста
				if(testParams.length==1){
					// only test type
					questions=tmpArray;
				}else{
					// other info
					testParams[1]=testParams[1].trim();
					if(testParams[1].length>0){
						// the 2d parameter is a sequence to parse
						var questionsNumbers=generateSequenceArray(testParams[1]);
						if (questionsNumbers==null)
							return;
						for(var i=0;i<questionsNumbers.length;++i){
							var idx=findQuestion(questionsNumbers[i],tmpArray);
							if(idx==-1){
								alert("Error! The question #"+questionsNumbers[i]+" is absent!");
								return;
							}
							questions[questions.length] = tmpArray[idx];
						}
					}else{
						questions=tmpArray;
					}
				}
				testParams[1]=questions.length;
				var suspend_data=testParams.join("|");
				for(var i=0;i<questions.length;++i)
					suspend_data=suspend_data+"\n"+questions[i].join("|");
				for(var i=0;i<answers.length;i++)
					suspend_data=suspend_data+"\n"+answers[i].join("|");
				doLMSSetValue("cmi.suspend_data",suspend_data);
				doLMSSetValue("cmi.core.lesson_status","incomplete");
			}
		}
	}

	function nextQuestion(){
		for(var i=0;i<questions.length;++i){
			var isAnswered=false;
			for(var j=0;j<answers.length;++j)
				if(questions[i][0]==answers[j][0])
					isAnswered=true;
			if(!isAnswered)
				return questions[i][0];
		}

		return null;
	}

	function isCorrectAnswer(correctAnswer,answer){
		var result=1;
		var cor_answer_array=new Array();
		var cor_answer=new Array();
		var answer_array=new Array();

		answer=""+answer.trim();
		correctAnswer=""+correctAnswer.trim();
		cor_answer_array=correctAnswer.split("~");
		answer_array=answer.split('~');

		if(cor_answer_array.length != answer_array.length) 
			return 0;
		for(var j=0;j<cor_answer_array.length;++j){  
			cor_answer=cor_answer_array[j].split("#");
			var ans=""+answer_array[j].trim();
			var error=1;
			for(var i=0;i<cor_answer.length;++i){
				var cor_ans=""+cor_answer[i].trim();
				if(cor_ans==ans)
					error = 0;
			}
			if(error == 1){
				result=0;
				break;
			}
		}

		return result;
	}

	function getStudentAnswers(StudentAnswers){
		var result='';
		var answer=window.frames["question"].selectedAnswer();
		var questionType=window.frames["question"].getType();
		var allAnswers=window.frames["question"].getAllAnswers();
		var answer_array=new Array();

		StudentAnswers=""+StudentAnswers.trim();
		answer_array=StudentAnswers.split('~');
		if((questionType == "choice")
				|| (questionType == "true-false")) {
			for (var i = 0; i < answer_array.length; ++i) {
				if (i == 0) {
					result += allAnswers[answer_array[i] - 1];
				} else {
					result += '[,]' + allAnswers[answer_array[i] - 1];
				}
			}
		} else if (questionType == "sequencing") {
			// TODO: изучить, как правильно оформлять
			for (var i = 0; i < answer_array.length; ++i) {
				if (result == '') {
					result = allAnswers[answer_array[i]];
				} else {
					result = result + '[,]' + allAnswers[answer_array[i]];
				}
			}
		}else if(questionType=="fill-in"){
			// TODO: изучить, как правильно оформлять
			result=StudentAnswers;
		}else if (questionType == "matching") {
			for (var i = 0; i < answer_array.length; ++i) {
				if (result == '') {
					result = i + '[.]' + allAnswers[answer_array[i]];
				} else {
					result += '[,]' + i + '[.]' + allAnswers[answer_array[i]];
				}
			}
		}else{
			result='1';
		}

		return (questionType=="fill-in") ? result : result.split(' ').join('_');
	}

	function getAnswerCorrectMeasure(type, correct, answer) {
		var correctTokens = correct.trim().split("~");
		var answerTokens = answer.trim().split('~');
		var correctCount = 0;
		var variantsCount = correctTokens.length;
		if (type == 'choice') {
			for (var j = 0; j < answerTokens.length; ++j) {
				if (correct.search('(~|^)' + answerTokens[j] + '(?!\\d)') != -1) {
					++correctCount;
				}
			}
			variantsCount += answerTokens.length - correctCount;
		} else if ((type != null) && (type != '')) {
			for (var j = 0; j < answerTokens.length; ++j) {
				if (correctTokens[j] == answerTokens[j]) {
					++correctCount;
				}
			}
		}
		
		return Number((correctCount / variantsCount).toFixed(7));
	}

	function weightQuestion(questionType,correctAnswer,answer,iscor){
		var sum=0;
		var flag=0;
		var cor_answer_array=new Array();
		var cor_answer=new Array();
		var answer_array=new Array();

		answer=""+answer.trim();
		correctAnswer=""+correctAnswer.trim();
		cor_answer_array=correctAnswer.split("~");
		answer_array=answer.split('~');
		
		if(questionType=="other"){
			var weight = window.frames["question"].getWeight();
			for(var j=0;j<answer_array.length;++j){
				var k=0;
				for(var i=0;i<cor_answer_array.length;++i)
					if(answer_array[j]!=cor_answer_array[i]){
						++k;
					}else{
						sum=sum+parseInt(weight[cor_answer_array[i]-1]);
					}
				if(k==cor_answer_array.length)
					var flag = 1;
			}
			if(flag==1){
				return 0;
			}else{
				return parseFloat(sum/100);
			}
		}else{
			if(iscor==1){
				return 1;
			}else{
				return 0;
			}
		}
	}
	
	function justSubmitAnswer(questIndex, questionType, isProbabilistic, submitedAnswer) {
		var answer = "" + submitedAnswer.trim();
		
		var correctAnswer=null;
		for (var i = 0; i < questions.length; ++i) {
			if (questIndex == questions[i][0]) {
				correctAnswer = questions[i][1];
				break;
			}
		}
		correctAnswer = (correctAnswer == null) ? '-' + answer : "" + correctAnswer.trim();
		
		var correctMeasure = getAnswerCorrectMeasure(questionType, correctAnswer, answer);
		var weight = weightQuestion(questionType, correctAnswer, answer, isCorrectAnswer(correctAnswer, answer));
		
		var answerItem = new Array(
				questIndex,							//[0]	id вопроса
				correctMeasure, 					//[1]	степень правильности
				questionType, 						//[2]	тип вопроса
				1,									//[3]	вес вопроса
				getStudentAnswers(answer), 			//[4] 	ответ студента
				getStudentAnswers(correctAnswer), 	//[5]	правильный ответ
				ArrTimestamp[ArrTimestamp.length-1],//[6]	время
				ArrLatency[ArrTimestamp.length-1], 	//[7]	Latency
				col_wrong,							//[8]	неверные ответы для 4 типа
				isProbabilistic						//[9]	возможен ли частично правильный ответ
		);
		col_wrong=0;
		answers[answers.length]=answerItem;
		doLMSSetValue("cmi.suspend_data", doLMSGetValue("cmi.suspend_data")
				+ "\n" + answerItem.join("|"));
		testParams[2]=ddd;
		
		var s_d=doLMSGetValue("cmi.suspend_data").split("\n");
		var t_p=s_d[0].split("|");
		t_p[2]=ddd;
		s_d[0]=t_p.join("|");
		doLMSSetValue("cmi.suspend_data", s_d.join("\n"));

		computeTime();

		doLMSCommit();
	}

	function submitAnswer(){
		if(currentQuestion!=-1){
			var answer=window.frames["question"].selectedAnswer();
			if(answer==null){
				alert("Не выбран ответ.\nВыберите ответ и нажмите 'Продолжить'");
				return;
			}

			answer=""+answer.trim();
			var correctAnswer="";
			for(var i=0;i<questions.length;++i) {
				if(currentQuestion==questions[i][0]){
					correctAnswer=questions[i][1];
					break;
				}
			}
			correctAnswer=""+correctAnswer.trim();
			isCorrect=isCorrectAnswer(correctAnswer,answer);
			if(testParams[0]==2)
				if(isCorrect==1){
					alert("Правильно!");
				}else{
					alert("Не верно!");
				}
			if(testParams[0]==4){
				var doc=window.frames["question"].document;
				var correctDiv=doc.getElementById("correct");
				var incorrectDiv=doc.getElementById("incorrect");
				if(isCorrect==1){
					incorrectDiv.style.display = "none";
					correctDiv.style.display="block";
				}else{
					correctDiv.style.display="none";
					incorrectDiv.style.display="block";
					++col_wrong;
					return;
				}
			}
			
			justSubmitAnswer(currentQuestion, window.frames["question"].getType(), window.frames["question"].isProbabilistic(), answer);
		}
		currentQuestion=nextQuestion();
		if(itog){
			$('[name=question]').attr('src', "empty.html?time=" + date);
			$('[name=botton]').attr('src', "empty.html?time=" + date);
			window.frames["question"].document.write("Тест пройден\n" + "Для повторного прохождения итогового теста обратитесь к преподавателю");
		}else if(currentQuestion!=null){
			$('[name=question]').attr('src', currentQuestion + ".html?time=" + date);
		}else{
			$('[name=question]').attr('src', "result.html?time=" + date);
			$('[name=botton]').attr('src', "empty.html?time=" + date);
		}
	}

	var isTerminate=false;
	function finish(isTerminate){
		if(!isTerminate){
			var s=doLMSGetValue("cmi.completion_status");
			unloadPage(s);
		}else{
			return;
		}
	}
	
	$( function() {
		$('[name=question]').attr('src', "intro.html?time=" + date);
		$('[name=botton]').attr('src', "operationframe.html?time=" + date);
	});
</script>
</head>
<frameset rows="*,80" frameborder="0" border="0" framespacing="0"
	onLoad="start()" onunload="finish(isTerminate)">
	<frame src="intro.html" name="question">
	<frame src="operationframe.html" name="botton" scrolling="NO" noresize style='border-top:1px solid gray'>
	<frame name="hidden_frame" style='display:none'>
</frameset>
<noframes>
	<body>You are not able to take testing due to inability to use frames</body>
</noframes>
</html>
