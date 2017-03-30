package com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph.content.items;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph.content.AbstractContentItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph.content.TextBlock;

/**
 * Item for {@link TextBlock}. This item includes text and him properties.
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TextContentItem extends AbstractContentItem<String> {

	private static final String SPAN_TAG_NAME = "span";
	private static final String BOLD_TAG_NAME = "b";
	private static final String ITALIC_TAG_NAME = "i";
	private static final String SUPERSCRIPT_TAG_NAME = "sup";
	private static final String SUBSCRIPT_TAG_NAME = "sub";
	private static final String UNDERLINE_TAG_NAME = "u";
	private static final String FONT_TAG_NAME = "font";
	private static final String COLOR_TAG_NAME = "color";

	// properties of text
	private boolean bold;
	private boolean italic;
	private boolean underline;
	private boolean superscript;
	private boolean subscript;
	private String color;

	/**
	 * Create text item from run of text
	 * 
	 * @param run
	 *            Run of text
	 */
	public TextContentItem(XWPFRun run) {
		super(run.toString());

		boolean isHyperlink = (run instanceof XWPFHyperlinkRun);

		this.setBold(run.isBold());
		this.setItalic(run.isItalic());
		this.setSuperscript(run.getSubscript() == VerticalAlign.SUPERSCRIPT);
		this.setSubscript(run.getSubscript() == VerticalAlign.SUBSCRIPT);
		this.setUnderline((run.getUnderline() != UnderlinePatterns.NONE) && !isHyperlink);

		// TODO: what is it?
		// if ((styleNumber < 1) || (styleNumber > 9)) {
		if (!isHyperlink) {
			this.setColor(run.getColor());
		}
		// } else if ((styleNumber > 0) && (styleNumber < 10)) {
		// tempElement = el;
		// }
	}

	public String getValue() {
		return this.value;
	}

	/**
	 * @param value
	 *            Text of item. If there is null then return false
	 * @return if successful then true
	 */
	public boolean setValue(String text) {
		if (text != null) {
			if (!text.isEmpty()) {
				this.value = text;

				return true;
			}
		}

		return false;
	}

	public void setBold(boolean value) {
		this.bold = value;
	}

	public void setItalic(boolean value) {
		this.italic = value;
	}

	public void setUnderline(boolean value) {
		this.underline = value;
	}

	public void setSuperscript(boolean value) {
		this.superscript = value;
		if (value) {
			this.subscript = !value;
		}
	}

	public void setSubscript(boolean value) {
		this.subscript = value;
		if (value) {
			this.superscript = !value;
		}
	}

	public void setColor(String value) {
		this.color = value;
	}

	public boolean isBold() {
		return this.bold;
	}

	public boolean isItalic() {
		return this.italic;
	}

	public boolean isUnderline() {
		return this.underline;
	}

	public boolean isSuperscript() {
		return this.superscript;
	}

	public boolean isSubscript() {
		return this.subscript;
	}

	public String getColor() {
		return this.color;
	}

	/**
	 * @return html-element span
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element headTag = creatorTags.createElement(SPAN_TAG_NAME);
		Element text = null;

		if (this.isBold()) {
			Element tag = creatorTags.createElement(BOLD_TAG_NAME);
			if (SPAN_TAG_NAME.equals(headTag.getNodeName())) {
				headTag = tag;
				text = tag;
			} else {
				headTag.appendChild(tag);
				text = tag;
			}
		}

		if (this.isItalic()) {
			Element tag = creatorTags.createElement(ITALIC_TAG_NAME);
			if (SPAN_TAG_NAME.equals(headTag.getNodeName())) {
				headTag = tag;
				text = tag;
			} else {
				headTag.appendChild(tag);
				text = tag;
			}
		}

		if (this.isSuperscript()) {
			Element tag = creatorTags.createElement(SUPERSCRIPT_TAG_NAME);
			if (SPAN_TAG_NAME.equals(headTag.getNodeName())) {
				headTag = tag;
				text = tag;
			} else {
				headTag.appendChild(tag);
				text = tag;
			}
		} else if (this.isSubscript()) {
			Element tag = creatorTags.createElement(SUBSCRIPT_TAG_NAME);
			if (SPAN_TAG_NAME.equals(headTag.getNodeName())) {
				headTag = tag;
				text = tag;
			} else {
				headTag.appendChild(tag);
				text = tag;
			}
		}

		if (this.isUnderline()) {
			Element tag = creatorTags.createElement(UNDERLINE_TAG_NAME);
			if (SPAN_TAG_NAME.equals(headTag.getNodeName())) {
				headTag = tag;
				text = tag;
			} else {
				headTag.appendChild(tag);
				text = tag;
			}
		}

		if (this.getColor() != null) {
			Element tag = creatorTags.createElement(FONT_TAG_NAME);
			tag.setAttribute(COLOR_TAG_NAME, this.getColor());
			if (SPAN_TAG_NAME.equals(headTag.getNodeName())) {
				headTag = tag;
				text = tag;
			} else {
				headTag.appendChild(tag);
				text = tag;
			}
		}

		if (text == null) {
			headTag.setTextContent(this.getValue());
		} else {
			text.setTextContent(this.getValue());
		}

		return headTag;
	}

}
