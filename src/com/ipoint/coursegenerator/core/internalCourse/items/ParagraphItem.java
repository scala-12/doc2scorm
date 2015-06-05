package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.HyperlinkBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ListBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ParagraphBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TableBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;

/**
 * Item for {@link ParagraphBlock}. This item is other block: {@link TextBlock},
 * {@link TableBlock}, {@link ListBlock} or {@link HyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphItem extends AbstractItem {

	public ParagraphItem(AbstractBlock block) {
		super(block);
	}

	/**
	 * Returns block as item
	 * @return Block as item
	 */
	@Override
	public AbstractBlock getValue() {
		return (AbstractBlock) super.getValue();
	}

	@Override
	public List<Class<?>> getAvailableValueClasses() {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(TextBlock.class);
		classes.add(HyperlinkBlock.class);
		classes.add(ListBlock.class);
		classes.add(TableBlock.class);
		return classes;
	}
}
