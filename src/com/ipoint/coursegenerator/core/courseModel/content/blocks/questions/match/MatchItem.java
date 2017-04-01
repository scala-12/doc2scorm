package com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.match;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
		label.setAttribute("id", MATCH_LABEL_4_ANSWER_CLASS);
		Element answer = creatorTags.createElement("span");
		answer.setAttribute("id", MATCH_ANSWER_CLASS);

		span.appendChild(label);
		span.appendChild(answer);

		for (AbstractParagraphBlock<?> block : this.getValue().get(0)) {
			label.appendChild(block.toHtml(creatorTags));
		}
		for (AbstractParagraphBlock<?> block : this.getValue().get(1)) {
			answer.appendChild(block.toHtml(creatorTags));
		}

		return span;
	}

}
