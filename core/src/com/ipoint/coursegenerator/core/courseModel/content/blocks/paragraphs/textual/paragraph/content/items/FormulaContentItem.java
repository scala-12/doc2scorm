package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.items;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.AbstractContentItem;

/**
 * Item which includes Math formula
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FormulaContentItem extends AbstractContentItem<Node> {

	private boolean paragraphFlag;

	public FormulaContentItem(Node math, boolean paragraphFlag) {
		super(math);
		this.setParagraphFlag(paragraphFlag);
	}

	public boolean isParagraph() {
		return this.paragraphFlag;
	}

	public void setParagraphFlag(boolean flag) {
		this.paragraphFlag = flag;
	}

	/**
	 * Returns html-object math
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element mathML = (Element) creatorTags.importNode(this.getValue(), true);

		mathML.removeAttribute("display");
		if (this.isParagraph()) {
			mathML.setAttribute("display", "block");
		} else {
			mathML.setAttribute("display", "inline");
		}

		return mathML;
	}

	@Override
	public Element toHtmlWithoutStyles(Document creatorTags) {
		return toHtml(creatorTags);
	}

	@Override
	public String getText() {
		try {
			return this.toHtml(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument())
					.getTextContent();
		} catch (DOMException | ParserConfigurationException e) {
			return " ";
		}
	}

}
