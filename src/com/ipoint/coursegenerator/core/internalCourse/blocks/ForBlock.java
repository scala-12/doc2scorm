package com.ipoint.coursegenerator.core.internalCourse.blocks;

import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;

public interface ForBlock {

	/**
	 * Checking: this block is right item or not for This block
	 * 
	 * @param item
	 *            checking item
	 * @return true if item is right for This block
	 */
	boolean isRightItem(AbstractItem item);
}
