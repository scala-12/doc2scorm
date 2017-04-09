package com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.match;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionItem;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class MatchItem extends AbstractQuestionItem<List<List<AbstractParagraphBlock<?>>>> {

	public static final String MATCH_ANSWER_CLASS = "match_answer";
	public static final String MATCH_LABEL_4_ANSWER_CLASS = "match_label4answer";
	public static final String[] MATCH_ANSWER_OTHER_CLASSES = new String[] { "ui-state-default" };

	public MatchItem(List<List<AbstractParagraphBlock<?>>> pair) {
		super(pair);
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

		for (AbstractParagraphBlock<?> block : this.getValue().get(0)) {
			NodeList items = block.toSimpleHtml(creatorTags);
			while (items.getLength() != 0) {
				label.appendChild(items.item(0));
			}
		}
		for (AbstractParagraphBlock<?> block : this.getValue().get(1)) {
			NodeList items = block.toSimpleHtml(creatorTags);
			while (items.getLength() != 0) {
				answer.appendChild(items.item(0));
			}
		}

		return span;
	}

}
