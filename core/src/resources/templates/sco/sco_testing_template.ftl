<#if html_type == html_type_5>
<!DOCTYPE html>
<#elseif html_type == html_type_4_01>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
</#if>
<html>
<head>
	<title>${page_title}</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	
	<script type="text/javascript" src="../${system_dir}/SCOFunctions.js"> </script>
	<script type="text/javascript" src="../${system_dir}/APIWrapper.js"> </script>
	<script type="text/javascript" src="../${system_dir}/jquery-${jquery_ver}.min.js"> </script>
	<script type="text/javascript" src="../${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.js"> </script>
	<link rel="stylesheet" href="../${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.css">
	<link rel="stylesheet" href="../${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.structure.css">
	<link rel="stylesheet" href="../${system_dir}/jquery-ui-${jquery_ui_ver}.custom/jquery-ui.theme.css">
	
	<script type="text/javascript" src="../${system_dir}/MathJax-2.7.1/MathJax.js?config=TeX-MML-AM_CHTML&locale=ru">MathJax.Hub.Config({showMathMenu: false,showMathMenuMSIE: false});</script>
	
	<link type="text/css" rel="stylesheet" href="../${system_dir}/course.css">
	<link type="text/css" rel="stylesheet" href="../${system_dir}/test.css">
	<link type="text/css" rel="stylesheet" href="../user_course.css">
	<link type="text/css" rel="stylesheet" href="../user_test.css">
	<link type="text/css" rel="stylesheet" href="user_test.css">
	
<#assign isSingle = type == single>
<#assign isTrueFalse = type == true_false>
<#assign isMultiple = type == multiple>
<#assign isSomeChoice = isSingle || isMultiple || isTrueFalse>
<#assign isFillIn = type == fill_in>
<#assign isMatching = type == matching>
<#assign isSequencing = type == sequencing>
	<script type="text/javascript">
		$( function() {
			$('input[type=submit],input[type=button],button').button();
			$('fieldset input[type="radio"],fieldset input[type="checkbox"]').checkboxradio();
			$( 'fieldset' ).controlgroup().css('display', 'block');
			<#if isSomeChoice>
				$( '#${answer_fieldset_id}' ).controlgroup({
					"direction": "vertical"
				});
			</#if>
			
			<#if isMatching || isSequencing>
				<#if isSequencing>
					$( '#${sortable_block_id}' ).sortable({
					    tolerance: 'pointer',
						axis: "y",
						opacity: 0.7,
						revert: true,
						placeholder: "ui-state-highlight",
						sort: function() {
							$('#${sortable_block_id} .ui-state-highlight').height($('#${sortable_block_id} .active_answer').height());
							$('#${sortable_block_id} li').width($('#${sortable_block_id} .active_answer').width());
						},
						start: function(event, ui) {
							$(ui.item).addClass('active_answer');
						},
						stop: function() {
							$('#${sortable_block_id} li').removeClass('active_answer');
						}
					});
				<#else>
					var compPad = $('li.${companion_class}').css('padding-bottom');
					compPad = parseInt(compPad.substring(0, compPad.indexOf('px')));
					var elemPad = $('#${sortable_block_id} li.${sortable_elem_class}').css('padding-bottom');
					elemPad = parseInt(elemPad.substring(0, elemPad.indexOf('px')));
					
					function refreshHeights() {
						$('#${sortable_block_id} li.${sortable_elem_class}').each(
							function() {
								var elem = $(this);
								var comp = $('li.${companion_class}').eq(elem.index());
								
								elem.css('padding-bottom', elemPad + 'px');
								comp.css('padding-bottom', compPad + 'px');
								
								if (elem.outerHeight(true) > comp.outerHeight(true)) {
									comp.css('padding-bottom', elem.outerHeight(true) - comp.outerHeight(true) + compPad + 'px');
								} else if (elem.outerHeight(true) != comp.outerHeight(true)) {
									elem.css('padding-bottom', comp.outerHeight(true) - elem.outerHeight(true) + elemPad + 'px');
								}
							}
						);
					}
					refreshHeights();
					
					$( '#${sortable_block_id} li' ).draggable({
						opacity: 0.7,
						revert: 'invalid',
					    zIndex: 1000,
					    helper: 'clone'
					});
					
					$( '#${sortable_block_id} li' ).droppable({
						classes: {
					    	"ui-droppable-hover": "active_answer"
					    },
					    tolerance: 'pointer',
					    drop: function(event, ui) {
					    	var dragged = $(ui.draggable);
					    	var dropped = $(this);
					    	
					    	var afterDrag = dragged.next();
					    	var beforeDrag = dragged.prev();
					    	var afterDrop = dropped.next();
					    	var beforeDrop = dropped.prev();
					    	
					    	if (afterDrag.is(dropped)) { 
					    		dropped.after(dragged);
					    	} else if (beforeDrag.is(dropped)) {
						    	dropped.before(dragged);
					    	} else {
					    		if (afterDrag.length) {
					    			afterDrag.before(dropped);
					    		} else {
					    			beforeDrag.after(dropped);
					    		}
					    		if (afterDrop.length) {
					    			afterDrop.before(dragged);
					    		} else {
					    			beforeDrop.after(dragged);
					    		}
					    	}
					    	
					    	refreshHeights();
					    }
					});
				</#if>
				
				$('#${sortable_block_id} li').css('cursor', 'move');
				
				$( '#${sortable_block_id}' ).disableSelection();
			</#if>

			<#if isSingle || isMultiple || isMatching || isSequencing>
				<#assign tagName><#if isSomeChoice>input<#else>li</#if></#assign>
				var answerBlock = $( '#<#if isSomeChoice>choice_answers_fieldset<#elseif isMatching>match_answers_block<#elseif isSequencing>sequence_answers_block</#if>' );
				var answerBlockCopy = answerBlock.clone();
				// dont change this string - need for iLogos
				var shuffeledAnswersCopy = $('<' + 'div' + '><' + '/div' + '>');
				var answersCount = answerBlock.find('${tagName}').length;
				for (var i = answersCount; i > 0; --i) {
					var randomInd = Math.floor(Math.random() * i);
					shuffeledAnswersCopy.append(answerBlockCopy.find('${tagName}').eq(randomInd));
				}
				
				shuffeledAnswersCopy.find('${tagName}').each(function() {
					var answerId = this.id;
					answerBlock.prepend(answerBlock.find('#' + answerId).eq(0));
					<#if isSomeChoice>
						answerBlock.prepend(answerBlock.find('[for=' + answerId + ']').eq(0));
					</#if>
				});
			</#if>
		});
	</script>
	<script type="text/javascript">
		function getAllAnswers(){ 
			<#if isSomeChoice>
				return {${answers_text}};
			<#elseif isMatching || isSequencing>
				var result = {};
				
				var answerPrefix = '${answer_id_prefix}';
				var shift = answerPrefix.length;
				$('form #${answer_block_id} #${sortable_block_id} .${sortable_elem_class}').each(
					function() {
						var elem = $(this);
						result[elem.attr('id').substr(shift)] = elem.text();
					}
				);
				
				return result;
			<#else>
				return {};
			</#if>
		}
	
		function getType(){
			return "${scorm_type}";
		}
		
		function isProbabilistic() {
			return false;
		}
	
		function selectedAnswer(){
			var value = null;
			
			<#if (isSequencing || isMatching)>
				var answerPrefix = '${answer_id_prefix}';
				var shift = answerPrefix.length;
				$('#${sortable_block_id} .${sortable_elem_class}').each(
					function() {
						var id = $(this).attr('id');
						value = ((value==null) ? "" : value + "~") + id.substr(shift);
					}
				);
			<#elseif isFillIn>
				$('form #${answer_block_id} #${fill_in_field_id}').each(
					function() {
						value = ((value==null) ? "" : value + "~") + this.value;
					}
				);
			<#else>
				$('form #${answer_block_id} [type=' +
						<#if isSingle || isTrueFalse>'radio'
						<#elseif isMultiple>'checkbox'
						</#if>
						+ ']:checked').each(
					function() {
						value = ((value==null) ? "" : value + "~") + this.value;
					}
				);
			</#if>
	
			return value;
		}
	
	</script>
</head>
<body>
<input type='hidden' id='question_description' value=''>
<input type='hidden' id='question_type' value='${type}'>
${body_content}
<div id="correct" style="display:none"><p>Правильно!</p></div>
<div id="incorrect" style="display:none"><p>Неправильно!</p></div>
</body>
</html>
