package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.ListItem;

/**
 * List block which may includes several {@link TextBlock} or
 * {@link HyperlinkBlock} as one item
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ListBlock extends AbstractTextualParagraphBlock {

	// TODO: change type of marker on Integer
	public static final int SIMPLE_MARKER = 0;
	public static final int UPPER_LETTER_MARKER = 1;
	public static final int LOWER_LETTER_MARKER = 2;
	public static final int UPPER_ROMAN_MARKER = 3;
	public static final int LOWER_ROMAN_MARKER = 4;
	public static final int DECIMAL_MARKER = 5;

	/**
	 * Type list marker
	 */
	private Integer type;

	/**
	 * Create list block first level and with simple marker
	 * 
	 * @param items
	 */
	public ListBlock(List<ListItem> items) {
		super(items);
		this.setMarkerType(SIMPLE_MARKER);
	}

	/**
	 * Returns equivalent of marker type in number format from string
	 * 
	 * @param markerName
	 *            Equivalent of marker type in string format
	 * @return Equivalent of marker type in number format. If equivalent not
	 *         founded then return type "simple marker"
	 */
	public static int getMarkerTypeFromString(String markerName) {
		int markerType = SIMPLE_MARKER;

		if (markerName != null) {
			if (markerName.equalsIgnoreCase("upperLetter")) {
				markerType = UPPER_LETTER_MARKER;
			} else if (markerName.equalsIgnoreCase("lowerLetter")) {
				markerType = LOWER_LETTER_MARKER;
			} else if (markerName.equalsIgnoreCase("upperRoman")) {
				markerType = UPPER_ROMAN_MARKER;
			} else if (markerName.equalsIgnoreCase("lowerRoman")) {
				markerType = LOWER_ROMAN_MARKER;
			} else if (markerName.equalsIgnoreCase("decimal")) {
				markerType = DECIMAL_MARKER;
			}
		}

		return markerType;
	}

	@Override
	public List<ListItem> getItems() {
		return (List<ListItem>) super.getItems();
	}

	/**
	 * Changing list marker type
	 * 
	 * @param type
	 *            Type of marker
	 * @return if successful then true
	 */
	public boolean setMarkerType(Integer type) {
		if (type != null) {
			if ((type == DECIMAL_MARKER) || (type == LOWER_LETTER_MARKER)
					|| (type == LOWER_ROMAN_MARKER)
					|| (type == UPPER_LETTER_MARKER)
					|| (type == UPPER_ROMAN_MARKER) || (type == SIMPLE_MARKER)) {
				this.type = type;
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns type of list marker
	 * 
	 * @return type of list marker
	 */
	public Integer getMarkerType() {
		return this.type;
	}

	/**
	 * @return html element ol (if there is numbered) or ul (if another)
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		String typeMarker = null;
		if (this.type != null) {
			if (this.type == UPPER_LETTER_MARKER) {
				typeMarker = "A";
			} else if (this.type == LOWER_LETTER_MARKER) {
				typeMarker = "a";
			} else if (this.type == UPPER_ROMAN_MARKER) {
				typeMarker = "I";
			} else if (this.type == LOWER_ROMAN_MARKER) {
				typeMarker = "i";
			} else if (this.type == DECIMAL_MARKER) {
				typeMarker = "1";
			}
		}

		Element list = null;
		if (typeMarker == null) {
			list = creatorTags.createElement("ul");
		} else {
			list = creatorTags.createElement("ol");
			list.setAttribute("type", typeMarker);
		}

		for (int i = 0; i < this.getItems().size(); ++i) {
			Element listItem = this.getItems().get(i).toHtml(creatorTags);
			list.appendChild(listItem);
			if ((i + 1) < this.getItems().size()) {
				if (this.getItems().get(i + 1).getValue() instanceof ListBlock) {
					// first child because this method returns <li>, but we need
					// insert into listItem a other list. If don't do it then
					// another list inserted into new list item - It is looks
					// not correct.
					listItem.appendChild(this.getItems().get(++i)
							.toHtml(creatorTags).getFirstChild());
				}
			}
		}

		return list;
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

}
