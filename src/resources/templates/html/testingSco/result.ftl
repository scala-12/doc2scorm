<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Result</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link type="text/css" rel="stylesheet" href="../${system_dir}/course.css">
	<link type="text/css" rel="stylesheet" href="../${system_dir}/test.css">
	<link type="text/css" rel="stylesheet" href="../user_course.css">
	<link type="text/css" rel="stylesheet" href="../user_test.css">
	<link type="text/css" rel="stylesheet" href="user_test.css">
	
	<script type="text/javascript" src="../${system_dir}/APIWrapper.js"></script>
	<script type="text/javascript" src="../${system_dir}/SCOFunctions.js"></script>
	<script type="text/javascript" src="../${system_dir}/jquery-${jquery_ver}.min.js"></script>
	<script type="text/javascript">
		var markText;
		var percents = 0;
		var percents4markA = ${percents4markA};
		var percents4markB = ${percents4markB};
		var percents4markC = ${percents4markC};

		var questsTable = '';
		var oldElem = null;
		var scoAnswersSourceJson = parent.doLMSGetValue('cmi.sco_answers_source_json');

		function showElement(elemId) {
			var elem = $('#' + elemId);
			if (elem.length) {
				if (oldElem != null) {
					oldElem.hide();
				}

				elem.show();
				oldElem = elem;
			}
		}

		function hideElement() {
			if (oldElem != null) {
				oldElem.hide();
				oldElem = null;
			}
		}
	
		function getMark(percents) {
			if (percents >= percents4markA) {
				return 5;
			} else if (percents >= percents4markB) {
				return 4;
			} else if (percents >= percents4markC) {
				return 3;
			} else {
				return 2;
			}
		}
	
		function commentTest(testparam) {
			var extraContent = '';
			var header;
			if((parent.testParams[0] == 2)
					|| (parent.testParams[0] == 3)
					|| (parent.testParams[0] == 5)) {
				header = 'Прохождение теста окончено';
			} else if (parent.testParams[0] == 4) {
				header = 'Упражнение окончено!';
			} else {
				header = 'Прохождение теста окончено';
				extraContent ='<p>Результаты теста вы сможете узнать у своего преподавателя (куратора).</p>';
			}
	
			return '<div id="end_test_msg">'
						+ '<h2 align="center";>' + header + '</h2>'
						+ extraContent
						+ '<p>В левой части текущего окна перейдите по одной из гиперссылок для дальнейшего изучения разделов курса.</p>'
					+ '</div>';
		}
	
		// Success Status
		function defuneSuccessStatus() {
			var pers = getCountOfRightAnswers() / getCountOfQuestions() * 100;
			if (pers >= percents4markC) {
				parent.doLMSSetValue('cmi.success_status', 'passed');
			} else {
				parent.doLMSSetValue('cmi.success_status', 'failed');		
			}
		}
	
		// количество верных ответов
		function getCountOfRightAnswers() {
			var count = 0;
			for (var i = 0; i < parent.answers.length; ++i) {
				var answer = parent.answers[i];
				if (answer[1] == 1) {
					count += 1;
				} else if ((answer[1] == 0) || (!answer[9])) {
					count += 0;
				} else {
					count = Number((count + parseFloat(answer[1])).toFixed(7));
				}
			}
	
			return count;
		}

		function getCountOfQuestions() {
			return parent.questions.length;
		}
	
		function createAnswersReport() {
			var testParam0 = parent.testParams[0] == 4;
			var scoAnswersSource = null;
			if ((scoAnswersSourceJson !== null)
					&& (scoAnswersSourceJson !== '')) {
				scoAnswersSource = JSON.parse(scoAnswersSourceJson);
			}
		
			for (var j = 0; j < parent.answers.length; j++) {
				var questId = 'quest_' + (j + 1);
				var questInfo =
						'<div class="quest_info" style="display:none" id="' + questId + '">'
							+ '<span class="label_caption">Текст вопроса и варианты ответа:</span>'
							+ '<div class="quest_info_item"><iframe style="height:100%" src="' + parent.answers[j][0] + '.html' + '"></iframe></div>'
							+ ((scoAnswersSource !== null) ?
									(parent.answers[j][0] in scoAnswersSource) ?
											'<div class="quest_comment">'
												+ '<span class="label_caption">Комментарий к выбору ответа:</span>'
												+ '<br/>'
												+ '<div class="quest_info_item">'
													+ '<div>'
														+ '<span>' + scoAnswersSource[parent.answers[j][0]] + '</span>'
													+ '</div>'
												+ '</div>'
											+ '</div>'
											: ''
									: '')
							+ '<br/>'
							+ '[<a href="#" onclick="hideElement()">Скрыть информацию</a>]'
						+ '</div>';
				questsTable +=
						'<tr>'
							+ '<td>' + (j + 1) + '</td>' //was parent.answers[j][0] instead of j+1
							+ '<td>' + ((testParam0) ? 
									parent.answers[j][8] + " - количество ошибок"
									: getAnswerStatus(parent.answers[j]))
							+ '</td>'
							+ '<td><a href="#" onclick="showElement(\'' + questId + '\');">Посмотреть</a></td>'
						+ '</tr>'
						+ '<tr><td colspan="3">' + questInfo + '</td></tr>';
			}
		
			percents = getCountOfRightAnswers() / parent.answers.length * 100;
			markText = "Ваша оценка за тест : " + getMark(percents) + ' (' + Math.round(percents) + '% правильных ответов)';
			
			return false;
		}
		
		function getAnswerStatus(answer) {
			var res;
			if (answer[1] == 1) {
				res = 'Правильный';
			} else if ((answer[1] == 0) || (!answer[9])) {
				res = 'Неправильный' + ((answer[1] != 0) ? ' (' + parseFloat(answer[1] * 100).toFixed(2) + '%)' : '');
			} else {
				res = 'Частично правильный (' + parseFloat(answer[1] * 100).toFixed(2) + '%)';
			}

			return res;
		}
	
		/* -- Задержка на время mSec -- */
		function pause(mSec) {
			var justMinute = new Date().getTime();
			while (true) {
				if((new Date().getTime() - justMinute) > mSec) {
					break;
				}
			}
		}
	
		function setInteractions() {
			if (parent.testParams[0] != 4) {
				for (var j = 0; j < parent.answers.length; ++j) {
					parent.doLMSSetValue("cmi.interactions." + j + ".id", parent.answers[j][0]);
					parent.doLMSSetValue("cmi.interactions." + j + ".type", parent.answers[j][2]);
					parent.doLMSSetValue("cmi.interactions." + j + ".result", (parent.answers[j][1] == 1) ? 
							"correct"
							: (((parent.answers[j][1] == 0) || !parent.answers[j][9]) ?
									"incorrect" 
									: parent.answers[j][1]));
					parent.doLMSSetValue("cmi.interactions." + j + ".weighting", parent.answers[j][3]);
					parent.doLMSSetValue("cmi.interactions." + j + ".latency", parent.answers[j][7]);
					parent.doLMSSetValue("cmi.interactions." + j + ".timestamp", parent.answers[j][6]);
					parent.doLMSSetValue("cmi.interactions." + j + ".learner_response", parent.answers[j][4]);
					parent.doLMSSetValue("cmi.interactions." + j + ".correct_responses.0.pattern", parent.answers[j][5]);
				}
			}
			parent.doLMSSetValue("cmi.score.raw", getCountOfRightAnswers());
			parent.doLMSSetValue("cmi.score.max", getCountOfQuestions());
			parent.doLMSSetValue("cmi.score.min", "0");
			parent.doLMSSetValue("cmi.suspend_data", (parent.testParams[0] == 6) ? 'done' : '');
			
			defuneSuccessStatus();
			parent.doLMSSetValue("cmi.completion_status", "completed");
			
			parent.isTerminate = true;

			parent.unloadPage(parent.doLMSGetValue("cmi.completion_status"));
		}
	</script>
</head>
<body>
	<script>
		setInteractions();
		document.write(commentTest(parent.testParams[0]));

		if((parent.testParams[0] == 2)
				|| (parent.testParams[0] == 3)
				|| (parent.testParams[0] == 5)
				|| (parent.testParams[0] == 4)) {
			createAnswersReport();
			document.write(markText);
			if (parent.testParams[0] == 4) {
				var summ = 0;
				for (var i = 0; i < parent.answers.length; ++i) {
					summ += parent.answers[i][8];
				}
				document.write("<p id=\"error_count\">"+ summ + " - количество допущенных ошибок во всем тесте</p>");
			} else {
				document.write("<hr/>");
			}
			
			document.write("<table id=\"test_results\">");
			document.write("<caption>Результаты прохождения теста</caption>");
			document.write(	"<tr><td>№ вопроса</td><td>Ответ</td><td>Текст вопроса</td></tr>");
			document.write(	questsTable);
			document.write("</table>");
		} else {
			var answersSum = 0;
			var lesStatus = "";
		  	document.write("<h2>Тест окончен</h2>");
		  	lesStatus = parent.doLMSGetValue("cmi.completion_status");
			if (lesStatus != "completed") {
				percents = answersSum / getCountOfQuestions() * 100;
			}
		}
	</script>
</body>
</html>