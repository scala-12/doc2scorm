package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.match.MatchItem.Label2Answer;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.HyperlinkRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.TextualRunsBlock;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * This block is an analogue of text paragraph. These includes several
 * {@link TextualRunsBlock}, {@link HyperlinkRunsBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class MatchBlock extends AbstractQuestionBlock<MatchItem> {

	public static final String MATCH_BLOCK_ID = "match_block";
	public static final String MATCH_ANSWERS_BLOCK_ID = "match_answers_block";
	public static final String MATCH_LABEL_BLOCK_ID = "match_labels_block";
	public static final String MATCH_ANSWER_ID_PREFIX = MatchItem.MATCH_ANSWER_CLASS + '_';

	public MatchBlock(List<MatchItem> items) throws BlockCreationException {
		this(items, null);
	}

	public MatchBlock(final List<MatchItem> items, String task) throws BlockCreationException {
		super(itemsWithShuffledAnswers(items), task, false);

		final List<List<AbstractSectionBlock<?>>> shuffledAnswers = this.getItems().stream()
				.map(pair -> pair.getValue().getAnswerSections()).collect(Collectors.toList());

		this.correctAnswers = items.stream()
				.map(pair -> String.valueOf(shuffledAnswers.indexOf(pair.getValue().getAnswerSections())))
				.toArray(String[]::new);
	}

	private static List<MatchItem> itemsWithShuffledAnswers(List<MatchItem> items) {
		List<List<AbstractSectionBlock<?>>> labels = items.stream().map(pair -> pair.getValue().getLabelSections())
				.collect(Collectors.toList());

		List<List<AbstractSectionBlock<?>>> shuffledAnswers = items.stream()
				.map(pair -> pair.getValue().getAnswerSections()).collect(Collectors.toList());
		Collections.shuffle(shuffledAnswers);

		final Iterator<List<AbstractSectionBlock<?>>> shuffledAnswerIter = shuffledAnswers.iterator();
		final ArrayList<MatchItem> shuffledItems = new ArrayList<>(items.size());
		labels.stream().forEach(label -> {
			try {
				shuffledItems.add(new MatchItem(new Label2Answer(label, shuffledAnswerIter.next())));
			} catch (ItemCreationException e) {
				e.printStackTrace();
			}
		});

		return shuffledItems;
	}

	/**
	 * add table to answer block that included list of labels and list of
	 * answers
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element div = super.toHtml(creatorTags);
		Element answersBlock = (Element) Tools.getElementById(div, AbstractQuestionBlock.ANSWER_BLOCK_ID);

		Element table = creatorTags.createElement("table");
		table.setAttribute("id", MATCH_BLOCK_ID);
		table.appendChild(creatorTags.createElement("tbody"));
		table.getFirstChild().appendChild(creatorTags.createElement("tr"));
		table.getFirstChild().getFirstChild().appendChild(creatorTags.createElement("td"));
		table.getFirstChild().getFirstChild().appendChild(creatorTags.createElement("td"));

		Element labels = creatorTags.createElement("ol");
		labels.setAttribute("id", MATCH_LABEL_BLOCK_ID);

		Element answers = creatorTags.createElement("ul");
		answers.setAttribute("id", MATCH_ANSWERS_BLOCK_ID);

		table.getFirstChild().getFirstChild().getFirstChild().appendChild(labels);
		table.getFirstChild().getFirstChild().getLastChild().appendChild(answers);

		int i = -1;
		while (answersBlock.hasChildNodes()) {
			i += 1;

			Element span = (Element) answersBlock.getFirstChild();
			Element labelSpan = (Element) span.getFirstChild();
			Element answerSpan = (Element) span.getLastChild();
			if (!labelSpan.getAttribute("id").equals(MatchItem.MATCH_LABEL_4_ANSWER_CLASS)) {
				Element tmp = labelSpan;
				labelSpan = answerSpan;
				answerSpan = tmp;
			}

			Element label = creatorTags.createElement("li");
			label.setAttribute("class", labelSpan.getAttribute("class"));

			Element answer = creatorTags.createElement("li");
			answer.setAttribute("class", answerSpan.getAttribute("class"));
			answer.setAttribute("id", MATCH_ANSWER_ID_PREFIX + String.valueOf(i));

			while (labelSpan.hasChildNodes()) {
				label.appendChild(labelSpan.getFirstChild());
			}

			while (answerSpan.hasChildNodes()) {
				answer.appendChild(answerSpan.getFirstChild());
			}

			labels.appendChild(label);
			answers.appendChild(answer);

			answersBlock.removeChild(span);
		}

		answersBlock.appendChild(table);

		return div;
	}

	@Override
	public int getType() {
		return MATCHING;
	}
}
