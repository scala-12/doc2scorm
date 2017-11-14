package com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular.cell;

import java.util.List;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular.TableSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.HyperlinkRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.TextualRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;

/**
 * This item may includes {@link TextualRunsBlock}, {@link HyperlinkRunsBlock}
 * or {@link TableSectionBlock} blocks or null
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CellItem extends AbstractSectionItem<Optional<List<AbstractSectionBlock<?>>>> {

	/**
	 * Count of cells which combined in row
	 */
	private Integer rowSpan;

	/**
	 * Count of cells which combined in column
	 */
	private Integer colSpan;

	public CellItem(List<AbstractSectionBlock<?>> sections) throws ItemCreationException {
		this(Optional.of(sections));
	}

	public CellItem() throws ItemCreationException {
		this(Optional.empty());
	}

	private CellItem(Optional<List<AbstractSectionBlock<?>>> optSections) throws ItemCreationException {
		super(optSections);

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

	@Override
	public boolean isValidValue(Optional<List<AbstractSectionBlock<?>>> sections) {

		return (!sections.isPresent()) || !sections.get().isEmpty();
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

			if (this.getValue().isPresent()) {
				for (AbstractSectionBlock<?> par : this.getValue().get()) {
					tCell.appendChild(par.toHtml(creatorTags));
				}
			}
		}

		return tCell;
	}

	@Override
	public String getText() {
		StringBuilder text = new StringBuilder();

		if (this.getValue().isPresent()) {
			for (AbstractSectionBlock<?> block : this.getValue().get()) {
				text.append(block.getText());
			}
		}

		return text.toString();
	}

}
