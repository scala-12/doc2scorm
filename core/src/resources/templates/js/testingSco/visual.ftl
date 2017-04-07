<#assign isChoice = type == choice>
<#assign isMultiple = type == multiple>
<#assign isSomeChoice = isChoice || isMultiple>
<#assign isFillIn = type == fill_in>
<#assign isMatch = type == match>
<#assign isSequence = type == sequence>
		$( function() {
			$( 'input[type=submit],input[type=button],button' ).button();
			$( 'fieldset input[type="radio"],fieldset input[type="checkbox"]' ).checkboxradio();
			$( 'fieldset' ).controlgroup().css('display', 'block');
<#if isSomeChoice>
			$( '#${answer_fieldset_id}' ).controlgroup({
				"direction": "vertical"
			});
<#elseif isMatch || isSequence>
	<#if isMatch>
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
	<#elseif isSequence>
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
	</#if>
			$( '#${sortable_block_id} li' ).css( 'cursor', 'move' );
			
			$( '#${sortable_block_id}' ).disableSelection();
</#if>
		});