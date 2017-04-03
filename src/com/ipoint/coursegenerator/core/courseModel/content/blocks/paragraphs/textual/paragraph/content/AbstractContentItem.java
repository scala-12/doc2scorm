package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.items.FormulaContentItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.items.ImageContentItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.items.TextContentItem;

/**
 * Item that may be {@link TextContentItem}, {@link FormulaContentItem} or
 * {@link ImageContentItem}
 * 
 * @author Kalashnikov Vladislav
 * 
 * @param <T>
 *            Type of item that included in block
 *
 */
public abstract class AbstractContentItem<T> extends AbstractParagraphItem<T> {

	public static final String SPAN_TAG_NAME = "span";
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

	protected AbstractContentItem(XWPFRun run, T value) {
		super(value);

		if (run != null) {
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
		} else {
			this.setBold(false);
			this.setItalic(false);
			this.setSuperscript(false);
			this.setSubscript(false);
			this.setUnderline(false);
		}
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

	abstract protected Node getValueAsHtml(Document creatorTags);

	/**
	 * @return span
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
		Element headTag = creatorTags.createElement(SPAN_TAG_NAME);
		Element text = null;

		if (styled) {
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

		if (text == null) {
			headTag.appendChild(getValueAsHtml(creatorTags));
		} else {
			text.appendChild(getValueAsHtml(creatorTags));
		}

		return headTag;
	}

}
