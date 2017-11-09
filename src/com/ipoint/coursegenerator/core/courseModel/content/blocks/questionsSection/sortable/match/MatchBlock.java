package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.match;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.MatchQuestionBlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.QuestionWithSortableItems;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.HyperlinkRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.TextualRunsBlock;
import com.ipoint.coursegenerator.core.utils.Tools;
import com.ipoint.coursegenerator.core.utils.Tools.Pair;

/**
 * This block is an analogue of text paragraph. These includes several
 * {@link TextualRunsBlock}, {@link HyperlinkRunsBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class MatchBlock extends QuestionWithSortableItems<MatchItem> {

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

	public static final String MATCH_BLOCK_ID = "match_block";
	public static final String MATCH_ANSWERS_BLOCK_ID = "match_answers_block";
	public static final String MATCH_LABEL_BLOCK_ID = "match_labels_block";
	public static final String MATCH_LABEL_4_ANSWER_CLASS = "match_label4answer";

	private List<List<AbstractSectionBlock<?>>> labels;

	public MatchBlock(List<Label2Answer> pairs, String task) throws BlockCreationException {
		this(pairs.stream().map(pair -> pair.getLabelSections()).collect(Collectors.toList()),
				pairs.stream().map(pair -> {
					try {
						return new MatchItem(pair.getAnswerSections());
					} catch (ItemCreationException e) {
						e.printStackTrace();
					}

					return null;
				}).collect(Collectors.toList()), task);
	}

	private MatchBlock(List<List<AbstractSectionBlock<?>>> labels, List<MatchItem> items, String task)
			throws BlockCreationException {
		super(items, task);

		this.labels = labels;

		if (items.size() != labels.size()) {
			throw new MatchQuestionBlockCreationException(this, items);
		}
	}

	public List<List<AbstractSectionBlock<?>>> getLabels() {
		return new ArrayList<>(this.labels);
	}

	/**
	 * add table to answer block that included list of labels and list of answers
	 */
	@Override
	public Element toHtml(final Document creatorTags) {
		Element div = super.toHtml(creatorTags);
		Element answersBlock = (Element) Tools.getElementById(div, AbstractQuestionBlock.ANSWER_BLOCK_ID);

		Element table = creatorTags.createElement("table");
		table.setAttribute("id", MATCH_BLOCK_ID);
		table.appendChild(creatorTags.createElement("tbody"));
		table.getFirstChild().appendChild(creatorTags.createElement("tr"));
		table.getFirstChild().getFirstChild().appendChild(creatorTags.createElement("td"));
		table.getFirstChild().getFirstChild().appendChild(creatorTags.createElement("td"));

		final Element labels = creatorTags.createElement("ol");
		labels.setAttribute("id", MATCH_LABEL_BLOCK_ID);
		this.getLabels().stream().forEach(label -> {
			final Element labelHtml = creatorTags.createElement("li");
			labelHtml.setAttribute("class", MATCH_LABEL_4_ANSWER_CLASS);
			label.stream().forEach(labelBlock -> {
				NodeList labelNodes = labelBlock.toSimpleHtml(creatorTags);
				while (labelNodes.getLength() != 0) {
					labelHtml.appendChild(labelNodes.item(0));
					labelHtml.appendChild(creatorTags.createTextNode(" "));
				}
			});
			labels.appendChild(labelHtml);
		});

		Element answers = creatorTags.createElement("ul");
		answers.setAttribute("id", MATCH_ANSWERS_BLOCK_ID);
		while (answersBlock.hasChildNodes()) {
			answers.appendChild(answersBlock.getFirstChild());
		}

		table.getFirstChild().getFirstChild().getFirstChild().appendChild(labels);
		table.getFirstChild().getFirstChild().getLastChild().appendChild(answers);

		answersBlock.appendChild(table);

		return div;
	}

	@Override
	public QuestionType getType() {
		return QuestionType.MATCHING;
	}

}
