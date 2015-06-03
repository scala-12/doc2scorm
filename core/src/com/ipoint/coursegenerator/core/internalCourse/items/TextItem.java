package com.ipoint.coursegenerator.core.internalCourse.items;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class TextItem extends AbstractItem {

	private String text;
	private boolean bold;
	private boolean italic;
	private boolean underline;
	private boolean superscript;
	private boolean subscript;
	private String color;

	public TextItem() {
		this.setBold(false);
		this.setItalic(false);
		this.setUnderline(false);
		this.setSuperscript(false);
		this.setSubscript(false);
		this.setColor(null);
		this.setValue(null);
	}

	public TextItem(XWPFRun run) {
		this();

		boolean isHyperlink = run.getClass().equals(XWPFHyperlinkRun.class);

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

		this.setValue(run.toString());
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
	public String getValue() {
		return this.text;
	}

	@Override
	public void setValue(Object text) {
		this.text = (String) text;
	}

}
