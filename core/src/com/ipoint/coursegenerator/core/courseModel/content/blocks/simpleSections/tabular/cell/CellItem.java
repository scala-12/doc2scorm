package com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.tabular.cell;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.HyperlinkRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.TextualRunsBlock;

/**
 * This item may includes {@link TextualRunsBlock}, {@link HyperlinkRunsBlock}
 * or {@link TableBlock} blocks or null
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CellItem extends AbstractSectionItem<List<AbstractSectionBlock<?>>> {

	/**
	 * Count of cells which combined in row
	 */
	private Integer rowSpan;

	/**
	 * Count of cells which combined in column
	 */
	private Integer colSpan;

	public CellItem(List<AbstractSectionBlock<?>> blocks) throws ItemCreationException {
		super(blocks);
		onConstructor();
	}

	public CellItem() {
		super();
		onConstructor();
	}

	private void onConstructor() {
		this.setRowSpan(1);
		this.setColSpan(1);
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
	public boolean setValue(List<AbstractSectionBlock<?>> paragraphs) {
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
				for (AbstractSectionBlock<?> par : this.getValue()) {
					tCell.appendChild(par.toHtml(creatorTags));
				}
			}
		}

		return tCell;
	}

	@Override
	public String getText() {
		StringBuilder text = new StringBuilder();
		for (AbstractSectionBlock<?> block : this.getValue()) {
			text.append(block.getText());
		}

		return text.toString();
	}

}
