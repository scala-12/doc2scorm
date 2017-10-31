package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.match.MatchItem.Label2Answer;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.utils.Tools.Pair;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class MatchItem extends AbstractQuestionItem<List<Label2Answer>> {

	public static class Label2Answer extends Pair<AbstractSectionBlock<?>, AbstractSectionBlock<?>> {

		public Label2Answer(AbstractSectionBlock<?> label, AbstractSectionBlock<?> answer) {
			super(label, answer);
		}

		public AbstractSectionBlock<?> getLabel() {
			return this.left;
		}

		public AbstractSectionBlock<?> getAnswer() {
			return this.right;
		}
	}

	public static final String MATCH_ANSWER_CLASS = "match_answer";
	public static final String MATCH_LABEL_4_ANSWER_CLASS = "match_label4answer";
	public static final String[] MATCH_ANSWER_OTHER_CLASSES = new String[] { "ui-state-default" };

	public MatchItem(Set<Label2Answer> pair) {
		super(new ArrayList<>(pair));
	}

	/**
	 * @return span with spans: first is label with id of label class, second is
	 *         answer with id of answer class
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element span = creatorTags.createElement("span");
		Element label = creatorTags.createElement("span");
		label.setAttribute("class", MATCH_LABEL_4_ANSWER_CLASS);
		label.setAttribute("id", MATCH_LABEL_4_ANSWER_CLASS);
		Element answer = creatorTags.createElement("span");

		StringBuilder classes = new StringBuilder().append(MATCH_ANSWER_CLASS);
		for (String classname : MATCH_ANSWER_OTHER_CLASSES) {
			classes.append(' ').append(classname);
		}

		answer.setAttribute("class", classes.toString());
		answer.setAttribute("id", MATCH_ANSWER_CLASS);

		span.appendChild(label);
		span.appendChild(answer);

		for (Label2Answer label2Answer : this.getValue()) {
			NodeList items1 = label2Answer.getLabel().toSimpleHtml(creatorTags);
			NodeList items = label2Answer.getAnswer().toSimpleHtml(creatorTags);
			while (items1.getLength() != 0) {
				label.appendChild(items1.item(0));
				answer.appendChild(items.item(0));
			}
		}

		return span;
	}

	@Override
	public String getText() {
		// TODO
		return null;
	}

}
