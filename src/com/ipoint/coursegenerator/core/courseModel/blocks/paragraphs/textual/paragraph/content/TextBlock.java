package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractBlock;

/**
 * This block is analogue paragraph in life. Text block which may include
 * {@link AbstractContentItem}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TextBlock extends AbstractBlock<AbstractContentItem<?>> {

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
	 * @return html-element span as paragraph
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element paragraph = creatorTags.createElement("span");
		for (AbstractContentItem<?> run : this.getItems()) {
			NodeList childs = run.toHtml(creatorTags).getChildNodes();
			while (childs.getLength() != 0) {
				// node move from nodes to par
				paragraph.appendChild(childs.item(0));
			}
		}

		return paragraph;
	}

}
