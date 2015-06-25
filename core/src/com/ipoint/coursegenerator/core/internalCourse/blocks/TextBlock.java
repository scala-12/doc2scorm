package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.List;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.items.AbstractTextItem;

/**
 * Text block which may include images or text. This block is an extends of
 * {@link AbstractBlock}
 * 
 * @see AbstractBlock
 * @author Kalashnikov Vladislav
 *
 */
public class TextBlock extends AbstractBlock {

	/**
	 * Create Text block
	 * @param runs Runs of text
	 */
	public TextBlock(List<AbstractTextItem> runs) {
		super(runs);
	}

	@Override
	public List<AbstractTextItem> getItems() {
		return (List<AbstractTextItem>) super.getItems();
	}

	public Node toHtml() {
		// TODO Auto-generated method stub

		return null;
	}

}
