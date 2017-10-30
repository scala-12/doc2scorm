package com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.sequence;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class SequenceItem extends AbstractQuestionItem<List<AbstractSectionBlock<?>>> {

	public static final String SEQUENCE_ANSWER_CLASS = "sequence_answer";
	public static final String[] SEQUENCE_ANSWER_OTHER_CLASSES = new String[] { "ui-state-default" };

	public SequenceItem(List<AbstractSectionBlock<?>> content) {
		super(content);
	}

	/**
	 * @return list item
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element item = creatorTags.createElement("li");
		StringBuilder classes = new StringBuilder().append(SEQUENCE_ANSWER_CLASS);
		for (String classname : SEQUENCE_ANSWER_OTHER_CLASSES) {
			classes.append(' ').append(classname);
		}

		item.setAttribute("class", classes.toString());

		for (AbstractSectionBlock<?> block : this.getValue()) {
			NodeList items = block.toSimpleHtml(creatorTags);
			while (items.getLength() != 0) {
				item.appendChild(items.item(0));
			}
		}

		return item;
	}

}
