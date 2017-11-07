package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.match;

import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class MatchItem extends AbstractQuestionItem<List<AbstractSectionBlock<?>>> {

	public static final String MATCH_ANSWER_CLASS = "match_answer";
	public static final String MATCH_ANSWER_CLASSES = String.join(" ",
			new String[] { MATCH_ANSWER_CLASS, "ui-state-default" });
	public static final String MATCH_ANSWER_ID_PREFIX = MATCH_ANSWER_CLASS + '_';

	MatchItem(List<AbstractSectionBlock<?>> answer) throws ItemCreationException {
		super(answer);
	}

	/** @return answer li-element with id of answer class */
	@Override
	public Element toHtml(Document creatorTags) {
		Element input = creatorTags.createElement("li");
		input.setAttribute("class", MATCH_ANSWER_CLASSES);
		input.setAttribute("id", MATCH_ANSWER_ID_PREFIX + String.valueOf(this.getIndex()));

		int i = 0;
		for (AbstractSectionBlock<?> answerSection : this.getValue()) {
			if (i == 0) {
				i = 1;
			} else {
				input.appendChild(creatorTags.createTextNode(" "));
			}

			NodeList answerNodes = answerSection.toSimpleHtml(creatorTags);
			while (answerNodes.getLength() != 0) {
				input.appendChild(answerNodes.item(0));
			}
		}

		return input;
	}

	@Override
	public String getText() {

		return String.join(" ",
				this.getValue().stream().map(section -> section.getText()).collect(Collectors.toList()));
	}

}
