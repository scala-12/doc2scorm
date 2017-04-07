<#ftl encoding="Windows-1251"/>
		function getAllAnswers(){
<#assign isChoice = type == choice>
<#assign isMultiple = type == multiple>
<#assign isSomeChoice = isChoice || isMultiple>
<#assign isFillIn = type == fill_in>
<#assign isMatch = type == match>
<#assign isSequence = type == sequence>
<#if isSomeChoice>
			return {${answers_text}};
<#elseif isFillIn>
			return {};
<#elseif isMatch || isSequence>
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
</#if>
		}
		
		function getType(){
<#if isSomeChoice>
			return "choice";
<#elseif isFillIn>
			return "fill-in";
<#elseif isMatch>
			return "matching";
<#elseif isSequence>
			return "sequencing";
</#if>
		}

		function selectedAnswer(){
<#if isSomeChoice>
			$('form #${answer_block_id} [type='
	<#if isChoice>
					+ 'radio'
	<#elseif isFillIn>
					+ 'checkbox'
	</#if>
					+ ']:checked').each(
						function() {
							value = ((value==null) ? "" : value + "~") + this.value;
						}
					);
<#elseif isFillIn>
			$('form #${answer_block_id} #${fill_in_field_id}').each(
				function() {
					value = ((value==null) ? "" : value + "~") + this.value;
				}
			);
<#elseif isMatch || isSequence>
			var answerPrefix = '${answer_id_prefix}';
			var shift = answerPrefix.length;
			$('#${sortable_block_id} .${sortable_elem_class}').each(
				function() {
					var id = $(this).attr('id');
					value = ((value==null) ? "" : value + "~") + id.substr(shift);
				}
			);
</#if>
			return value;
		}