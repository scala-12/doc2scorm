package com.ipoint.coursegenerator.core.courseModel.blocks.tabular.cell;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.HyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.TextBlock;

/**
 * This item may includes {@link TextBlock}, {@link HyperlinkBlock} or
 * {@link TableBlock} blocks or null
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CellItem extends AbstractItem<List<AbstractParagraphBlock<?>>> {

	/**
	 * Count of cells which combined in row
	 */
	private Integer rowSpan;

	/**
	 * Count of cells which combined in column
	 */
	private Integer colSpan;

	public CellItem(List<AbstractParagraphBlock<?>> blocks) {
		super(blocks);
		this.setRowSpan(1);
		this.setColSpan(1);
	}

	public CellItem() {
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
		if (rowSpan != null) {
			if (rowSpan > 0) {
				this.rowSpan = rowSpan;
				return true;
			}
		}

		return false;
	}

	/**
	 * Change count of cells which combined in column
	 * 
	 * @param colSpan
	 *            Count of cells which combined in column.
	 * @return if successful then true
	 */
	public boolean setColSpan(Integer colSpan) {
		if (colSpan != null) {
			if (colSpan > 0) {
				this.colSpan = colSpan;
				return true;
			}
		}

		return false;
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

	/**
	 * Setup value of cell
	 * 
	 * @param paragraphs
	 *            Value of cell. May be null
	 */
	@Override
	public boolean setValue(List<AbstractParagraphBlock<?>> paragraphs) {
		this.value = paragraphs;
		if (paragraphs != null) {
			if (paragraphs.isEmpty()) {
				this.value = null;
			}
		}

		return true;
	}

		/**
	 * @return html element td
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element tCell = creatorTags.createElement("td");

		if ((this.getColSpan() != null) && (this.getRowSpan() != null)) {
			if (this.getColSpan() != 1) {
				tCell.setAttribute("colspan", this.getColSpan().toString());
			}
			if (this.getRowSpan() != 1) {
				tCell.setAttribute("rowspan", this.getRowSpan().toString());
			}
			if (this.getValue() != null) {
				for (AbstractParagraphBlock<?> par : this.getValue()) {
					Element tPar = par.toHtml(creatorTags);
					tCell.appendChild(tPar);
				}
			}
		}

		return tCell;
	}

}
