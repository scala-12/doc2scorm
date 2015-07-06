package com.ipoint.coursegenerator.core.courseModel.blocks.items;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.TextBlock;

/**
 * Item for {@link TextBlock}. This item includes text and him properties.
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TextOnlyItem extends AbstractTextItem {

	private String value;

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
	public TextOnlyItem(XWPFRun run) {
		if (!this.setValue(run.toString())) {
			// TODO: exception
		}

		boolean isHyperlink = (run instanceof XWPFHyperlinkRun);

		this.setBold(run.isBold());
		this.setItalic(run.isItalic());
		this.setSuperscript(run.getSubscript() == VerticalAlign.SUPERSCRIPT);
		this.setSubscript(run.getSubscript() == VerticalAlign.SUBSCRIPT);
		this.setUnderline((run.getUnderline() != UnderlinePatterns.NONE)
				&& !isHyperlink);

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
	public Element toHtml(Document creatorTags, boolean isHyperlink) {
		Element span = creatorTags.createElement("span");

		if (this.isBold()) {
			Element bold = creatorTags.createElement("b");
			span.appendChild(bold);
			span = bold;
		}

		if (this.isItalic()) {
			Element italic = creatorTags.createElement("i");
			span.appendChild(italic);
			span = italic;
		}

		if (this.isSubscript()) {
			Element superscript = creatorTags.createElement("sup");
			span.appendChild(superscript);
			span = superscript;
		} else if (this.isSubscript()) {
			Element subscript = creatorTags.createElement("sub");
			span.appendChild(subscript);
			span = subscript;
		}

		if (!isHyperlink) {
			if (this.isUnderline()) {
				Element underline = creatorTags.createElement("u");
				span.appendChild(underline);
				span = underline;
			}

			if (this.getColor() != null) {
				Element font = creatorTags.createElement("font");
				font.setAttribute("color", this.getColor());
				span.appendChild(font);
				span = font;
			}
		}

		span.setTextContent(this.getValue());

		return span;
	}

	/**
	 * @return html-element span. With underline and color parameters
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		return this.toHtml(creatorTags, false);
	}

}
