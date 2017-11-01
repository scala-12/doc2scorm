package com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.runs.FormulaRunItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.runs.ImageRunItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.runs.TextRunItem;

/**
 * Item that may be {@link TextRunItem}, {@link FormulaRunItem} or
 * {@link ImageRunItem}
 * 
 * @author Kalashnikov Vladislav
 * 
 * @param <T>
 *            Type of item that included in block
 *
 */
public abstract class AbstractContentRunItem<T> extends AbstractSectionItem<T> {

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

	protected AbstractContentRunItem(XWPFRun run, T value) throws ItemCreationException {
		super(value);

		if (run == null) {
			this.setBold(false);
			this.setItalic(false);
			this.setSuperscript(false);
			this.setSubscript(false);
			this.setUnderline(false);
		} else {
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
		return (Element) this.toHtml(creatorTags, true);
	}

	@Override
	public NodeList toSimpleHtml(Document creatorTags) {
		Element span = creatorTags.createElement("span");
		span.appendChild(toHtml(creatorTags, false));

		return span.getChildNodes();
	}

	private Node toHtml(Document creatorTags, boolean styled) {
		Node topNode = creatorTags.createElement(SPAN_TAG_NAME);
		Node bottomNode = null;

		if (styled) {
			if (this.isBold()) {
				Element tag = creatorTags.createElement(BOLD_TAG_NAME);
				if (SPAN_TAG_NAME.equals(topNode.getNodeName())) {
					topNode = tag;
					bottomNode = tag;
				} else {
					topNode.appendChild(tag);
					bottomNode = tag;
				}
			}

			if (this.isItalic()) {
				Element tag = creatorTags.createElement(ITALIC_TAG_NAME);
				if (SPAN_TAG_NAME.equals(topNode.getNodeName())) {
					topNode = tag;
					bottomNode = tag;
				} else {
					topNode.appendChild(tag);
					bottomNode = tag;
				}
			}

			if (this.isUnderline()) {
				Element tag = creatorTags.createElement(UNDERLINE_TAG_NAME);
				if (SPAN_TAG_NAME.equals(topNode.getNodeName())) {
					topNode = tag;
					bottomNode = tag;
				} else {
					topNode.appendChild(tag);
					bottomNode = tag;
				}
			}

			if (this.getColor() != null) {
				Element tag = creatorTags.createElement(FONT_TAG_NAME);
				tag.setAttribute(COLOR_TAG_NAME, this.getColor());
				if (SPAN_TAG_NAME.equals(topNode.getNodeName())) {
					topNode = tag;
					bottomNode = tag;
				} else {
					topNode.appendChild(tag);
					bottomNode = tag;
				}
			}
		}

		if (this.isSuperscript()) {
			Element tag = creatorTags.createElement(SUPERSCRIPT_TAG_NAME);
			if (SPAN_TAG_NAME.equals(topNode.getNodeName())) {
				topNode = tag;
				bottomNode = tag;
			} else {
				topNode.appendChild(tag);
				bottomNode = tag;
			}
		} else if (this.isSubscript()) {
			Element tag = creatorTags.createElement(SUBSCRIPT_TAG_NAME);
			if (SPAN_TAG_NAME.equals(topNode.getNodeName())) {
				topNode = tag;
				bottomNode = tag;
			} else {
				topNode.appendChild(tag);
				bottomNode = tag;
			}
		}

		if (bottomNode == null) {
			if (styled) {
				topNode.appendChild(getValueAsHtml(creatorTags));
			} else {
				topNode = getValueAsHtml(creatorTags);
			}
		} else {
			bottomNode.appendChild(getValueAsHtml(creatorTags));
		}

		return topNode;
	}

}
