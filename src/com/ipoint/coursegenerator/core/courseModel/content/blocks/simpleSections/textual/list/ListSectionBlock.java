package com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.list;

import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.AbstractTextualSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.HyperlinkRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.TextualRunsBlock;

/**
 * List block which may includes several {@link TextualRunsBlock} or
 * {@link HyperlinkRunsBlock} as one item
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ListSectionBlock extends AbstractTextualSectionBlock<ListSectionItem> {

	public static enum MarkerType {
		SIMPLE_MARKER, UPPER_LETTER_MARKER, LOWER_LETTER_MARKER, UPPER_ROMAN_MARKER, LOWER_ROMAN_MARKER, DECIMAL_MARKER
	}

	/**
	 * Type list marker
	 */
	private MarkerType type;

	/**
	 * Create list block first level and with simple marker
	 * 
	 * @param items
	 *            Items of block
	 * @throws BlockCreationException
	 */
	public ListSectionBlock(List<ListSectionItem> items) throws BlockCreationException {
		super(items);
		this.setMarkerType(MarkerType.SIMPLE_MARKER);
	}

	/** Changing list marker type */
	public void setMarkerType(MarkerType type) {
		this.type = type;
	}

	/** @return type of list marker */
	public MarkerType getMarkerType() {
		return this.type;
	}

	public int getSize() {
		int size = 0;

		for (ListSectionItem item : this.getItems()) {
			if (item.getValue() instanceof ParagraphBlock) {
				++size;
			} else if (item.getValue() instanceof ListSectionBlock) {
				size += ((ListSectionBlock) item.getValue()).getSize();
			}
		}

		return size;
	}

	/**
	 * @return html element ol (if there is numbered) or ul (if another)
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element list = null;
		if (this.getMarkerType() == MarkerType.SIMPLE_MARKER) {
			list = creatorTags.createElement("ul");
		} else {
			String typeMarker = null;
			if (this.getMarkerType() == MarkerType.UPPER_LETTER_MARKER) {
				typeMarker = "A";
			} else if (this.getMarkerType() == MarkerType.LOWER_LETTER_MARKER) {
				typeMarker = "a";
			} else if (this.getMarkerType() == MarkerType.UPPER_ROMAN_MARKER) {
				typeMarker = "I";
			} else if (this.getMarkerType() == MarkerType.LOWER_ROMAN_MARKER) {
				typeMarker = "i";
			} else if (this.getMarkerType() == MarkerType.DECIMAL_MARKER) {
				typeMarker = "1";
			}

			list = creatorTags.createElement("ol");
			list.setAttribute("type", typeMarker);
		}

		for (int i = 0; i < this.getItems().size(); ++i) {
			Element listItem = this.getItems().get(i).toHtml(creatorTags);
			list.appendChild(listItem);
			if ((i + 1) < this.getItems().size()) {
				if (this.getItems().get(i + 1).getValue() instanceof ListSectionBlock) {
					// first child because this method returns <li>, but we need
					// insert into listItem a other list. If don't do it then
					// another list inserted into new list item - It is looks
					// not correct.
					ListSectionItem item = this.getItems().get(++i);
					listItem.appendChild(item.toHtml(creatorTags).getFirstChild());
				}
			}
		}

		return list;
	}

	@Override
	public String getText() {
		return String.join("\n", this.getItems().stream().map(li -> li.getText()).collect(Collectors.toList()));
	}

}
