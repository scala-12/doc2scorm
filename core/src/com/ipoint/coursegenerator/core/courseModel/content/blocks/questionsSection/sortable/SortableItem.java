package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable;

import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;

public abstract class SortableItem extends AbstractQuestionItem<List<AbstractSectionBlock<?>>> {
	
	public static final String[] ANSWER_OTHER_CLASSES = new String[] { "ui-state-default" };

	public SortableItem(List<AbstractSectionBlock<?>> content) throws ItemCreationException {
		super(content);
	}

	/** @return answer li-element with id of answer class */
	@Override
	public Element toHtml(Document creatorTags) {
		Element input = creatorTags.createElement("li");
		input.setAttribute("class", this.getItemClasses());
		input.setAttribute("id", this.getItemIdPrefix() + String.valueOf(this.getIndex()));

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

	abstract protected String getItemClasses();

	abstract protected String getItemIdPrefix();

	@Override
	public String getText() {

		return super.getText() + String.join(" ",
				this.getValue().stream().map(AbstractSectionBlock::getText).collect(Collectors.toList()));
	}

}
