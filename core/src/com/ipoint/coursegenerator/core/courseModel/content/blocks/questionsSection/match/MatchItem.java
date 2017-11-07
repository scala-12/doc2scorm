package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.match;

import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.match.MatchItem.Label2Answer;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.utils.Tools.Pair;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class MatchItem extends AbstractQuestionItem<Label2Answer> {

	public static class Label2Answer extends Pair<List<AbstractSectionBlock<?>>, List<AbstractSectionBlock<?>>> {

		public Label2Answer(List<AbstractSectionBlock<?>> label, List<AbstractSectionBlock<?>> answer) {
			super(label, answer);
		}

		public List<AbstractSectionBlock<?>> getLabelSections() {
			return this.left;
		}

		public List<AbstractSectionBlock<?>> getAnswerSections() {
			return this.right;
		}
	}

	public static final String MATCH_ANSWER_CLASS = "match_answer";
	public static final String MATCH_LABEL_4_ANSWER_CLASS = "match_label4answer";
	public static final String MATCH_ANSWER_CLASSES = String.join(" ",
			new String[] { MATCH_ANSWER_CLASS, "ui-state-default" });
	public static final String MATCH_ANSWER_ID_PREFIX = MATCH_ANSWER_CLASS + '_';

	public MatchItem(Label2Answer pair) throws ItemCreationException {
		super(pair);
	}

	/**
	 * @return span with spans: first is label with id of label class, second is
	 *         answer with id of answer class
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element mainSpan = creatorTags.createElement("span");

		Element input = creatorTags.createElement("li");
		input.setAttribute("class", MATCH_ANSWER_CLASSES);
		input.setAttribute("id", MATCH_ANSWER_ID_PREFIX + String.valueOf(this.getIndex()));
		mainSpan.appendChild(input);

		Element label = creatorTags.createElement("li");
		label.setAttribute("class", MATCH_LABEL_4_ANSWER_CLASS);
		mainSpan.appendChild(label);

		int i = 0;
		for (AbstractSectionBlock<?> labelSection : this.getValue().getLabelSections()) {
			if (i == 0) {
				i = 1;
			} else {
				label.appendChild(creatorTags.createTextNode(" "));
			}
			label.appendChild(labelSection.toSimpleHtml(creatorTags).item(0));
		}

		i = 0;
		for (AbstractSectionBlock<?> answerSection : this.getValue().getAnswerSections()) {
			if (i == 0) {
				i = 1;
			} else {
				input.appendChild(creatorTags.createTextNode(" "));
			}
			input.appendChild(answerSection.toSimpleHtml(creatorTags).item(0));
		}

		return mainSpan;
	}

	@Override
	public String getText() {

		return String.join(" ",
				this.getValue().getLabelSections().stream().map(section -> section.getText())
						.collect(Collectors.toList()))
				+ " - " + String.join(" ", this.getValue().getAnswerSections().stream()
						.map(section -> section.getText()).collect(Collectors.toList()));
	}

}
