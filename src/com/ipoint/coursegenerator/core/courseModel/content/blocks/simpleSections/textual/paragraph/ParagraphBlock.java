package com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph;

import java.util.Collections;
import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.AbstractTextualSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.runs.TextRunItem;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphBlock extends AbstractTextualSectionBlock<ParagraphItem> {

	public static enum TextAlign {
		LEFT, CENTER, RIGHT, JUSTIFY, UNDEFINED
	}

	private static final String LEFT_ALIGN_CLASS = "left_align";

	private static final String CENTER_ALIGN_CLASS = "center_align";

	private static final String RIGHT_ALIGN_CLASS = "right_align";

	private static final String JUST_ALIGN_CLASS = "justify_align";

	private TextAlign alignment;

	public ParagraphBlock(List<ParagraphItem> items, TextAlign align) throws BlockCreationException {
		super(items);
		this.alignment = align;
	}

	public ParagraphBlock(ParagraphItem item, TextAlign align) throws BlockCreationException {
		this(Collections.singletonList(item), align);
	}

	public TextAlign getAlignment() {
		return this.alignment;
	}

	public static TextAlign convertAlignValue(ParagraphAlignment align) {
		if (ParagraphAlignment.LEFT == align) {
			return TextAlign.LEFT;
		} else if (ParagraphAlignment.RIGHT == align) {
			return TextAlign.RIGHT;
		} else if (ParagraphAlignment.CENTER == align) {
			return TextAlign.CENTER;
		} else if (ParagraphAlignment.BOTH == align) {
			return TextAlign.JUSTIFY;
		}

		return TextAlign.UNDEFINED;
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

		if (this.getAlignment() != TextAlign.UNDEFINED) {
			par.setAttribute("class",
					(this.getAlignment() == TextAlign.LEFT) ? LEFT_ALIGN_CLASS
							: ((this.getAlignment() == TextAlign.CENTER) ? CENTER_ALIGN_CLASS
									: ((this.getAlignment() == TextAlign.RIGHT) ? RIGHT_ALIGN_CLASS
											: ((this.getAlignment() == TextAlign.JUSTIFY) ? JUST_ALIGN_CLASS : null))));
		}

		return par;
	}

	@Override
	public String getText() {
		final StringBuilder text = new StringBuilder();
		for (ParagraphItem parItem : this.getItems()) {
			parItem.getValue().getItems().stream().filter(item -> item instanceof TextRunItem)
					.forEach(item -> text.append(((TextRunItem) item).getValue()));
		}

		return text.toString();
	}
}
