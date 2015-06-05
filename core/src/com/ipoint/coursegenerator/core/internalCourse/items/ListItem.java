package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.HyperlinkBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ListBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;

/**
 * Item for {@link ListBlock}. This item includes other blocks:
 * {@link HyperlinkBlock} and {@link TextBlock}
 * 
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ListItem extends AbstractItem {

	public ListItem(Object blocks) {
		super(blocks);
	}

	/**
	 * Returns included blocks as {@link List}
	 * 
	 * @return List of blocks
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AbstractBlock> getValue() {
		return (List<AbstractBlock>) super.getValue();
	}

	@Override
	public List<Class<?>> getAvailableValueClasses() {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(TextBlock.class);
		classes.add(HyperlinkBlock.class);
		return classes;
	}
}
