package com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.runs;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.AbstractContentRunItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.TextualRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;

/**
 * Item for {@link TextualRunsBlock}. This item includes text and him
 * properties.
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TextRunItem extends AbstractContentRunItem<String> {

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

	/**
	 * Create text item from run of text
	 * 
	 * @param run
	 *            Run of text
	 */
	public TextRunItem(XWPFRun run) throws ItemCreationException {
		super(run, run.toString());

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
	 * @param value
	 *            Text of item. If there is null then return false
	 * @return if successful then true
	 */
	public boolean isValidValue(String text) {
		if (super.isValidValue(text) && !text.isEmpty()) {
			return true;
		}

		return false;
	}

	@Override
	public String getText() {
		return this.getValue();
	}

	@Override
	public NodeList toSimpleHtmlModel(Document creatorTags) {
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

		((bottomNode == null) ? topNode : bottomNode).appendChild(creatorTags.createTextNode(this.getValue()));

		return topNode;
	}

	@Override
	public Element toHtmlModel(Document creatorTags) {
		Node result = toHtml(creatorTags, true);
		if (result.getNodeType() == Node.TEXT_NODE) {
			Element span = creatorTags.createElement("span");
			span.appendChild(result);
			result = span;
		}

		return (Element) result;
	}

}
