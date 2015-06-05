package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.internalCourse.blocks.TableBlock;

/**
 * Item for {@link TableBlock}. This item includes table cells (see
 * {@link TableCellItem}).
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TableRowItem extends AbstractItem {

	public TableRowItem(ArrayList<TableCellItem> cells) {
		super(cells);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TableCellItem> getValue() {
		return (List<TableCellItem>) super.getValue();
	}

	@Override
	public List<Class<?>> getAvailableValueClasses() {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(TableCellItem.class);
		return classes;
	}

}
