package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.Convertable;

/**
 * Abstract block which includes items. These can't have empty items.
 * 
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractBlock implements Convertable {

	private List<? extends AbstractItem> items;

	protected AbstractBlock(List<? extends AbstractItem> items) {
		if (items != null) {
			if (items.isEmpty()) {
				// TODO:exception
			} else {
				this.items = items;
			}
		} else {
			// TODO: exception
		}
	}

	protected List<? extends AbstractItem> getItems() {
		return this.items;
	}

	protected AbstractItem getFirstItem() {
		return this.getItems().get(0);
	}

}
