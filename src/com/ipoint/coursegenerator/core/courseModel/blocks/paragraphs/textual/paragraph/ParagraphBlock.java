package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.AbstractTextualParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.HyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.TextBlock;

/**
 * This block is an analogue of text paragraph. These includes {@link TextBlock}
 * , {@link HyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphBlock extends AbstractTextualParagraphBlock {

	private Integer alignment;

	public static final int LEFT_ALIGN = 0;

	public static final int CENTER_ALIGN = 1;

	public static final int RIGHT_ALIGN = 2;

	public static final int JUST_ALIGN = 3;

	private static final String LEFT_ALIGN_CLASS = "left_align";

	private static final String CENTER_ALIGN_CLASS = "center_align";

	private static final String RIGHT_ALIGN_CLASS = "right_align";

	private static final String JUST_ALIGN_CLASS = "justify_align";

	public ParagraphBlock(List<ParagraphItem> items, Integer align) {
		super(items);
		this.alignment = align;
	}

	public ParagraphBlock(ParagraphItem item, Integer align) {
		this(Lists.newArrayList(item), align);
	}

	public static Integer convertAlignValue(ParagraphAlignment align) {
		if (ParagraphAlignment.LEFT == align) {
			return LEFT_ALIGN;
		} else if (ParagraphAlignment.RIGHT == align) {
			return RIGHT_ALIGN;
		} else if (ParagraphAlignment.CENTER == align) {
			return CENTER_ALIGN;
		} else if (ParagraphAlignment.BOTH == align) {
			return JUST_ALIGN;
		}

		return null;
	}

	public Integer getAlignment() {
		return this.alignment;
	}

	@Override
	public List<ParagraphItem> getItems() {
		ArrayList<ParagraphItem> items = new ArrayList<ParagraphItem>();
		for (AbstractItem item : super.getItems()) {
			items.add((ParagraphItem) item);
		}

		return items;
	}

	/**
	 * @return html-element p
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element par = creatorTags.createElement("p");
		for (ParagraphItem item : this.getItems()) {
			NodeList nodes = item.toHtml(creatorTags).getChildNodes();
			while (nodes.getLength() != 0) {
				// node move from nodes to par
				par.appendChild(nodes.item(0));
			}
		}

		if (this.getAlignment() != null) {
			switch (this.getAlignment()) {
			case LEFT_ALIGN:
				par.setAttribute("class", LEFT_ALIGN_CLASS);
				break;
			case CENTER_ALIGN:
				par.setAttribute("class", CENTER_ALIGN_CLASS);
				break;
			case RIGHT_ALIGN:
				par.setAttribute("class", RIGHT_ALIGN_CLASS);
				break;
			case JUST_ALIGN:
				par.setAttribute("class", JUST_ALIGN_CLASS);
				break;
			}
		}

		return par;
	}

}
