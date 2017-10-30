package com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Lists;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.AbstractTextualSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.runs.TextRunItem;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphBlock extends AbstractTextualSectionBlock<ParagraphItem> {

	public static final int LEFT_ALIGN = 0;

	public static final int CENTER_ALIGN = 1;

	public static final int RIGHT_ALIGN = 2;

	public static final int JUST_ALIGN = 3;

	private static final String LEFT_ALIGN_CLASS = "left_align";

	private static final String CENTER_ALIGN_CLASS = "center_align";

	private static final String RIGHT_ALIGN_CLASS = "right_align";

	private static final String JUST_ALIGN_CLASS = "justify_align";

	private Integer alignment;

	public ParagraphBlock(List<ParagraphItem> items, Integer align) {
		super(items);
		this.alignment = align;
	}

	public ParagraphBlock(ParagraphItem item, Integer align) {
		this(Lists.newArrayList(item), align);
	}

	public Integer getAlignment() {
		return this.alignment;
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

	/**
	 * @return html-element p
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element par = creatorTags.createElement("p");

		for (ParagraphItem item : this.getItems()) {
			par.appendChild(item.toHtml(creatorTags));
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

	@Override
	public String getText() {
		StringBuilder text = new StringBuilder();
		ArrayList<TextRunItem> items = new ArrayList<>();
		for (ParagraphItem parItem : this.getItems()) {
			parItem.getValue().getItems().stream().filter(item -> item instanceof TextRunItem)
					.forEach(item -> items.add((TextRunItem) item));
		}

		for (TextRunItem item : items) {
			text.append(item.getValue());
		}

		return text.toString();
	}
}
