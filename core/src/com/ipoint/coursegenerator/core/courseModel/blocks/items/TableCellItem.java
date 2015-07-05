package com.ipoint.coursegenerator.core.courseModel.blocks.items;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.ParagraphHyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.ParagraphTextBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.TableBlock;

/**
 * This item may includes {@link ParagraphTextBlock},
 * {@link ParagraphHyperlinkBlock} or {@link TableBlock} blocks or null
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

	public TableCellItem(List<AbstractParagraphBlock> blocks) {
		this.setValue(blocks);
		this.setRowSpan(0);
		this.setColSpan(0);
	}

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
		if (rowSpan != null) {
			if (rowSpan >= 0) {
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
			if (colSpan >= 0) {
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

	/**
	 * @return html element td
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element tCell = creatorTags.createElement("td");

		if ((this.getColSpan() != null) && (this.getRowSpan() != null)) {
			if (this.getColSpan() != 0) {
				tCell.setAttribute("colspan", this.getColSpan().toString());
			}
			if (this.getRowSpan() != 0) {
				tCell.setAttribute("rowspan", this.getRowSpan().toString());
			}
			if (this.getValue() != null) {
				for (AbstractParagraphBlock par : this.getValue()) {
					Element tPar = par.toHtml(creatorTags);
					tCell.appendChild(tPar);
				}
			}
		}

		return tCell;
	}

}
