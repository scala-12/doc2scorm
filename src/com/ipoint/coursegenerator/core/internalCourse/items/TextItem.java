package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;

/**
 * Item for {@link TextBlock}. This item includes text and him properties.
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TextItem extends AbstractItem {

	//properties of text
	private boolean bold;
	private boolean italic;
	private boolean underline;
	private boolean superscript;
	private boolean subscript;
	private String color;

	public TextItem(XWPFRun run) {
		super(run.toString());

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
		return (String) super.getValue();
	}

	@Override
	public List<Class<?>> getAvailableValueClasses() {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(String.class);
		return classes;
	}

}
