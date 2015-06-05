package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.List;

import com.ipoint.coursegenerator.core.internalCourse.blocks.HyperlinkBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ParagraphBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TableBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;

/**
 * Item for {@link TableRowItem}. This item includes other blocks:
 * {@link TextBlock}, {@link HyperlinkBlock}, {@link TableBlock} or null
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TableCellItem extends AbstractItem {

	/**
	 * Count of cells which combined in row
	 */
	private Integer rowSpan;

	/**
	 * Count of cells which combined in column
	 */
	private Integer colSpan;

	/**
	 * Create cell which preset value
	 * 
	 * @param value
	 *            Value for cell. May be null
	 */
	public TableCellItem(List<ParagraphBlock> value) {
		super(value);
	}

	/**
	 * Create cell which preset value and properties
	 * 
	 * @param value
	 *            Value for cell. May be null
	 * @param rowSpan
	 *            Count of cells which combined in row. May be null
	 * @param colSpan
	 *            Count of cells which combined in column. May be null
	 */
	public TableCellItem(List<ParagraphBlock> value, Integer rowSpan,
			Integer colSpan) {
		this(value);
		this.setRowSpan(rowSpan);
		this.setColSpan(colSpan);
	}

	/**
	 * Create empty cell
	 */
	public TableCellItem() {
		this(null, null, null);
	}

	/**
	 * Change count of cells which combined in row
	 * 
	 * @param rowSpan
	 *            Count of cells which combined in row. May be null
	 */
	public void setRowSpan(Integer rowSpan) {
		this.rowSpan = (rowSpan == null) ? null : (rowSpan > 0) ? rowSpan : 0;
	}

	/**
	 * Change count of cells which combined in column
	 * 
	 * @param colSpan
	 *            Count of cells which combined in column. May be null
	 */
	public void setColSpan(Integer colSpan) {
		this.colSpan = (colSpan == null) ? null : (colSpan > 0) ? colSpan : 0;
	}

	/**
	 * Returns count of cells which combined in row
	 * 
	 * @return count of cells which combined in row
	 */
	public Integer getRowSpan() {
		return this.rowSpan;
	}

	/**
	 * Returns count of cells which combined in column
	 * 
	 * @return count of cells which combined in column
	 */
	public Integer getColSpan() {
		return this.colSpan;
	}

	@Override
	protected boolean isRightItemValue(Object value) {
		return (value == null) ? true : super.isRightItemValue(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ParagraphBlock> getValue() {
		return (List<ParagraphBlock>) super.getValue();
	}

}
