package com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.AbstractTextualSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;

/**
 * This block is analogue paragraph in life. Text block which may include
 * {@link AbstractContentRunItem}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TextualRunsBlock extends AbstractTextualSectionBlock<AbstractContentRunItem<?>> {

	public TextualRunsBlock(List<AbstractContentRunItem<?>> runs) throws BlockCreationException {
		super(runs);
	}

	public TextualRunsBlock(AbstractContentRunItem<?> run) throws BlockCreationException {
		this(toList(run));
	}

	private static ArrayList<AbstractContentRunItem<?>> toList(AbstractContentRunItem<?> run) {
		ArrayList<AbstractContentRunItem<?>> list = new ArrayList<>();
		list.add(run);

		return list;
	}

	/**
	 * @return html-element span container of runs
	 */
	@Override
	public Element toHtmlModel(Document creatorTags) {
		Element paragraph = creatorTags.createElement("span");
		for (AbstractContentRunItem<?> run : this.getItems()) {
			paragraph.appendChild(run.toHtmlModel(creatorTags));
		}

		return paragraph;
	}

	@Override
	public String getText() {
		StringBuilder text = new StringBuilder();
		for (AbstractContentRunItem<?> item : this.getItems()) {
			text.append(item.getText());
		}

		return text.toString();
	}

}
