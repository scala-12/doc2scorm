<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>$scoName</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

<script type="text/javascript" src="../${system_dir}/SCOFunctions.js"></script>
<script type="text/javascript" src="../${system_dir}/APIWrapper.js"></script>
<script type="text/javascript" src="../${system_dir}/jquery-3.1.1.min.js"></script>
<script type="text/javascript" src="../${system_dir}/jquery-ui-1.12.1.custom/jquery-ui.js"></script>
<link rel="stylesheet" href="../${system_dir}/jquery-ui-1.12.1.custom/jquery-ui.css">
<link rel="stylesheet" href="../${system_dir}/jquery-ui-1.12.1.custom/jquery-ui.structure.css">
<link rel="stylesheet" href="../${system_dir}/jquery-ui-1.12.1.custom/jquery-ui.theme.css">
<script type="text/javascript" async
		src="https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-MML-AM_CHTML">
	MathJax.Hub.Config({
    	showMathMenu: false,
    	showMathMenuMSIE: false
  	});
</script>
<link type="text/css" rel="stylesheet" href="../${system_dir}/test.css">
<link type="text/css" rel="stylesheet" href="test.css">

<script type="text/javascript">
	$( function() {
		$('input[type=submit],input[type=button],button').button();
		$('fieldset input[type="radio"],fieldset input[type="checkbox"]').checkboxradio();
		$( 'fieldset' ).controlgroup().css('display', 'block');
		#if((${questionType} == ${true_false_type})
				|| (${questionType} == ${choice_type}) || (${questionType} == ${multiple_type}))
			$( '#${answer_fieldset_id}' ).controlgroup({
				"direction": "vertical"
			});
		#end
		
		#if((${questionType} == ${sequencing_type}) || (${questionType} == ${matching_type}))
			#if(${questionType} == ${sequencing_type})
				$( '#${sortable_block}' ).sortable({
				    tolerance: 'pointer',
					axis: "y",
					opacity: 0.7,
					revert: true,
					placeholder: "ui-state-highlight",
					sort: function() {
						$('#${sortable_block} .ui-state-highlight').height($('#${sortable_block} .active_answer').height());
						$('#${sortable_block} li').width($('#${sortable_block} .active_answer').width());
					},
					start: function(event, ui) {
						$(ui.item).addClass('active_answer');
					},
					stop: function() {
						$('#${sortable_block} li').removeClass('active_answer');
					}
				});
			#else
				var compPad = $('li.${companion_class}').css('padding-bottom');
				compPad = parseInt(compPad.substring(0, compPad.indexOf('px')));
				var elemPad = $('#${sortable_block} li.${sortable_elem}').css('padding-bottom');
				elemPad = parseInt(elemPad.substring(0, elemPad.indexOf('px')));
				
				function refreshHeights() {
					$('#${sortable_block} li.${sortable_elem}').each(
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
				
				$( '#${sortable_block} li' ).draggable({
					opacity: 0.7,
					revert: 'invalid',
				    zIndex: 1000,
				    helper: 'clone'
				});
				
				$( '#${sortable_block} li' ).droppable({
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
			#end
			
			$('#${sortable_block} li').css('cursor', 'move');
			
			$( '#${sortable_block}' ).disableSelection();
		#end
	});
</script>
<script type="text/javascript">
	function getAllAnswers(){ 
		#if((${questionType} == ${true_false_type})
				|| (${questionType} == ${choice_type}) || (${questionType} == ${multiple_type}))
			return {$answersText};
		#elseif((${questionType} == ${matching_type})
				|| (${questionType} == ${sequencing_type}))
			var result = {};
			
			var answerPrefix = '${prefix}';
			var shift = answerPrefix.length;
			$('form #${answer_block} #${sortable_block} .${sortable_elem}').each(
				function() {
					var elem = $(this);
					result[elem.attr('id').substr(shift)] = elem.text();
				}
			);
			
			return result;
		#else
			return {}
		#end
	}

#if((${questionType} == ${true_false_type})
		|| (${questionType} == ${choice_type}) || (${questionType} == ${multiple_type})
		|| (${questionType} == ${fill_in_type})
		|| (${questionType} == ${matching_type})
		|| (${questionType} == ${sequencing_type}))
	function getType(){
		return #if((${questionType} == ${true_false_type}) || (${questionType} == ${choice_type}) || (${questionType} == ${multiple_type}))"choice"
				#elseif(${questionType} == ${fill_in_type})"fill-in"
				#elseif(${questionType} == ${matching_type})"matching"
				#elseif(${questionType} == ${sequencing_type})"sequencing"
				#{end};
	}

	function selectedAnswer(){
		var value = null;
		
		#if((${questionType} == ${sequencing_type}) || (${questionType} == ${matching_type}))
			var answerPrefix = '${prefix}';
			var shift = answerPrefix.length;
			$('#${sortable_block} .${sortable_elem}').each(
				function() {
					var id = $(this).attr('id');
					value = ((value==null) ? "" : value + "~") + id.substr(shift);
				}
			);
		#elseif(${questionType} == ${fill_in_type})
			$('form #${answer_block} #${fill_in_field_id}').each(
				function() {
					value = ((value==null) ? "" : value + "~") + this.value;
				}
			);
		#else
			$('form #${answer_block} [type=' +
					#if((${questionType} == ${true_false_type}) || (${questionType} == ${choice_type}))'radio'
					#elseif(${questionType} == ${multiple_type})'checkbox'
					#end
					+ ']:checked').each(
				function() {
					value = ((value==null) ? "" : value + "~") + this.value;
				}
			);
		#end

		return value;
	}
#end
</script>
</head>
<body>
${taskHtml}
<form name="examForm" action="" method="">
#if(${answersHtml.length()}!=0)
	${answersHtml}
#end
</form>
<div id="correct" style="display:none"><p>Правильно!</p></div>
<div id="incorrect" style="display:none"><p>Неправильно!</p></div>
</body>
</html>
