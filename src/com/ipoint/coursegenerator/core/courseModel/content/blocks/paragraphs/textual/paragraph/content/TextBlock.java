package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.AbstractTextualBlock;

/**
 * This block is analogue paragraph in life. Text block which may include
 * {@link AbstractContentItem}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TextBlock extends AbstractTextualBlock<AbstractContentItem<?>> {

	public TextBlock(List<AbstractContentItem<?>> runs) {
		super(runs);
	}

	public TextBlock(AbstractContentItem<?> run) {
		this(toList(run));
	}

	private static ArrayList<AbstractContentItem<?>> toList(AbstractContentItem<?> run) {
		ArrayList<AbstractContentItem<?>> list = new ArrayList<>();
		list.add(run);

		return list;
	}

	/**
	 * @return html-element span container of runs
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		return this.toHtml(creatorTags, true);
	}

	@Override
	public Element toHtmlWithoutStyles(Document creatorTags) {
		return this.toHtml(creatorTags, false);
	}

	private Element toHtml(Document creatorTags, boolean styled) {
		Element paragraph = creatorTags.createElement("span");
		for (AbstractContentItem<?> run : this.getItems()) {
			paragraph.appendChild((styled) ? run.toHtmlWithoutStyles(creatorTags) : run.toHtml(creatorTags));
		}

		return paragraph;
	}

	@Override
	public String getText() {
		StringBuilder text = new StringBuilder();
		for (AbstractContentItem<?> item : this.getItems()) {
			text.append(item.getText());
		}

		return text.toString();
	}

}
