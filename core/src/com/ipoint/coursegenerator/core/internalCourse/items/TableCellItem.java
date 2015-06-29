package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.HyperlinkBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TableBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;

/**
 * Item for {@link TableItem}. This item includes other blocks:
 * {@link TextBlock}, {@link HyperlinkBlock}, {@link TableBlock} or null
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TableCellItem extends AbstractItem {

	private List<AbstractParagraphBlock> value;

	/**
	 * Count of cells which combined in row
	 */
	private Integer rowSpan;

	/**
	 * Count of cells which combined in column
	 */
	private Integer colSpan;

	/**
	 * Create cell as block item
	 * 
	 * @param value
	 *            Value of cell. There can be null
	 */
	public TableCellItem(List<AbstractParagraphBlock> blocks) {
		this.setValue(blocks);
		this.setRowSpan(0);
		this.setColSpan(0);
	}

	/**
	 * Create empty cell
	 */
	public TableCellItem() {
		this(null);
	}

	/**
	 * Change count of cells which combined in row
	 * 
	 * @param rowSpan
	 *            Count of cells which combined in row.
	 * @return if successful then true
	 */
	public boolean setRowSpan(Integer rowSpan) {
		if (rowSpan == null) {
			return false;
		} else {
			if (rowSpan >= 0) {
				this.rowSpan = rowSpan;
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Change count of cells which combined in column
	 * 
	 * @param colSpan
	 *            Count of cells which combined in column.
	 * @return if successful then true
	 */
	public boolean setColSpan(Integer colSpan) {
		if (colSpan == null) {
			return false;
		} else {
			if (colSpan >= 0) {
				this.colSpan = colSpan;
				return true;
			} else {
				return false;
			}
		}
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

	public List<AbstractParagraphBlock> getValue() {
		return this.value;
	}

	/**
	 * Setup value of cell
	 * 
	 * @param paragraphs
	 *            Value of cell. May be null
	 */
	public void setValue(List<AbstractParagraphBlock> paragraphs) {
		this.value = paragraphs;
		if (paragraphs != null) {
			if (paragraphs.isEmpty()) {
				this.value = null;
			}
		}
	}

	/**
	 * Setup paragraph as value of cell
	 * 
	 * @param paragraph
	 *            Paragraph
	 * @return true if it's all right
	 */
	public void setValue(AbstractParagraphBlock paragraph) {
		if (value != null) {
			ArrayList<AbstractParagraphBlock> valueAsList = new ArrayList<AbstractParagraphBlock>();
			valueAsList.add(paragraph);
			this.setValue(valueAsList);
		} else {
			this.value = null;
		}
	}
	
	public void setFantom() {
		this.value = null;
		this.colSpan = null;
		this.rowSpan = null;
	}

}
