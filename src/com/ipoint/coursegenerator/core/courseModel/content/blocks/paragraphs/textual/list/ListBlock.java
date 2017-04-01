package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.list;

import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.AbstractTextualBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.HyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.TextBlock;

/**
 * List block which may includes several {@link TextBlock} or
 * {@link HyperlinkBlock} as one item
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ListBlock extends AbstractTextualBlock<ListItem> {

	public static final int SIMPLE_MARKER = 0;
	public static final int UPPER_LETTER_MARKER = 1;
	public static final int LOWER_LETTER_MARKER = 2;
	public static final int UPPER_ROMAN_MARKER = 3;
	public static final int LOWER_ROMAN_MARKER = 4;
	public static final int DECIMAL_MARKER = 5;

	/**
	 * Type list marker
	 */
	private int type;

	/**
	 * Create list block first level and with simple marker
	 * 
	 * @param items
	 *            Items of block
	 */
	public ListBlock(List<ListItem> items) {
		super(items);
		this.setMarkerType(SIMPLE_MARKER);
	}

	/**
	 * Changing list marker type
	 * 
	 * @param type
	 *            Type of marker
	 * @return if successful then true
	 */
	public boolean setMarkerType(int type) {
		if ((type == DECIMAL_MARKER) || (type == LOWER_LETTER_MARKER) || (type == LOWER_ROMAN_MARKER)
				|| (type == UPPER_LETTER_MARKER) || (type == UPPER_ROMAN_MARKER) || (type == SIMPLE_MARKER)) {
			this.type = type;

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns type of list marker
	 * 
	 * @return type of list marker
	 */
	public int getMarkerType() {
		return this.type;
	}

	public int getSize() {
		int size = 0;

		for (ListItem item : this.getItems()) {
			if (item.getValue() instanceof ParagraphBlock) {
				++size;
			} else if (item.getValue() instanceof ListBlock) {
				size += ((ListBlock) item.getValue()).getSize();
			}
		}

		return size;
	}

	/**
	 * @return html element ol (if there is numbered) or ul (if another)
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		return this.toHtml(creatorTags, true);
	}

	@Override
	public Element toHtmlWithoutStyles(Document creatorTags) {
		return this.toHtml(creatorTags, false);
	}

	private Element toHtml(Document creatorTags, boolean styled) {
		Element list = null;
		if (this.getMarkerType() == SIMPLE_MARKER) {
			list = creatorTags.createElement("ul");
		} else {
			String typeMarker = null;
			if (this.getMarkerType() == UPPER_LETTER_MARKER) {
				typeMarker = "A";
			} else if (this.getMarkerType() == LOWER_LETTER_MARKER) {
				typeMarker = "a";
			} else if (this.getMarkerType() == UPPER_ROMAN_MARKER) {
				typeMarker = "I";
			} else if (this.getMarkerType() == LOWER_ROMAN_MARKER) {
				typeMarker = "i";
			} else if (this.getMarkerType() == DECIMAL_MARKER) {
				typeMarker = "1";
			}

			list = creatorTags.createElement("ol");
			list.setAttribute("type", typeMarker);
		}

		for (int i = 0; i < this.getItems().size(); ++i) {
			Element listItem = (styled) ? this.getItems().get(i).toHtml(creatorTags)
					: this.getItems().get(i).toHtmlWithoutStyles(creatorTags);
			list.appendChild(listItem);
			if ((i + 1) < this.getItems().size()) {
				if (this.getItems().get(i + 1).getValue() instanceof ListBlock) {
					// first child because this method returns <li>, but we need
					// insert into listItem a other list. If don't do it then
					// another list inserted into new list item - It is looks
					// not correct.
					ListItem item = this.getItems().get(++i);
					listItem.appendChild((styled) ? item.toHtml(creatorTags).getFirstChild()
							: item.toHtmlWithoutStyles(creatorTags).getFirstChild());
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
