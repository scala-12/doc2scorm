package com.ipoint.coursegenerator.core.internalCourse.items;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;

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
		this.setValue(run.toString());

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
	 * Set value of item
	 * 
	 * @param value
	 *            Value of item
	 * @return true if all right
	 */
	public boolean setValue(String text) {
		if (text == null) {
			return false;
		} else {
			this.value = text;
			return true;
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
	
	@Override
	public Element toHtml(Document creatorTag, boolean isHyperlink) {
		Element span = creatorTag.createElement("span");
		
		if (this.isBold()) {
		    Element bold = creatorTag.createElement("b");
		    span.appendChild(bold);
		    span = bold;
		}
		
		if (this.isItalic()) {
		    Element italic = creatorTag.createElement("i");
		    span.appendChild(italic);
		    span = italic;
		}
		
		if (this.isSubscript()) {
		    Element superscript = creatorTag.createElement("sup");
		    span.appendChild(superscript);
		    span = superscript;
		} else if (this.isSubscript()) {
		    Element subscript = creatorTag.createElement("sub");
		    span.appendChild(subscript);
		    span = subscript;
		}
		
		if (!isHyperlink) {
			if (this.isUnderline()) {
			    Element underline = creatorTag.createElement("u");
			    span.appendChild(underline);
			    span = underline;
			}
			
			if (this.getColor() != null) {
				Element font = creatorTag.createElement("font");
				font.setAttribute("color", this.getColor());
				span.appendChild(font);
				span = font;
			}
		}
		
		span.setTextContent(this.getValue());
		
		return span;
	}

}
