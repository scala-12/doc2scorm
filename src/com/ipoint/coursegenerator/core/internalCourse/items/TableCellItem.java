package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.internalCourse.blocks.ParagraphBlock;

public class TableCellItem extends AbstractItem {

	private ArrayList<ParagraphBlock> value;
	private Integer rowSpan;
	private Integer colSpan;

	public TableCellItem() {
		this(null, null, null);
	}

	public TableCellItem(ArrayList<ParagraphBlock> value, Integer rowSpan,
			Integer colSpan) {
		this.setValue(value);
		this.setRowSpan(rowSpan);
		this.setColSpan(colSpan);
	}

	public void setRowSpan(Integer rowSpan) {
		this.rowSpan = (rowSpan == null) ? null : (rowSpan > 0) ? rowSpan : 0;
	}

	public void setColSpan(Integer colSpan) {
		this.colSpan = (colSpan == null) ? null : (colSpan > 0) ? colSpan : 0;
	}

	public Integer getRowSpan() {
		return this.rowSpan;
	}

	public Integer getColSpan() {
		return this.colSpan;
	}

	@Override
	public List<ParagraphBlock> getValue() {
		return this.value;
	}

	@Override
	public void setValue(Object value) {
		this.value = (value == null) ? new ArrayList<ParagraphBlock>()
				: (ArrayList<ParagraphBlock>) value;
	}

}
