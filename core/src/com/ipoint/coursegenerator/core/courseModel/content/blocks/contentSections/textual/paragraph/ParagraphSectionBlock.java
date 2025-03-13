package com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph;

import java.util.Collections;
import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.AbstractTextualSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.runs.TextRunItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphSectionBlock extends AbstractTextualSectionBlock<ParagraphSectionItem> {

	public static enum TextAlign {
		LEFT, CENTER, RIGHT, JUSTIFY, UNDEFINED
	}

	private static final String LEFT_ALIGN_CLASS = "left_align";

	private static final String CENTER_ALIGN_CLASS = "center_align";

	private static final String RIGHT_ALIGN_CLASS = "right_align";

	private static final String JUST_ALIGN_CLASS = "justify_align";

	private TextAlign alignment;

	public ParagraphSectionBlock(List<ParagraphSectionItem> items, TextAlign align) throws BlockCreationException {
		super(items);
		this.alignment = align;
	}

	public ParagraphSectionBlock(ParagraphSectionItem item, TextAlign align) throws BlockCreationException {
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
	public Element toHtmlModel(Document creatorTags) {
		Element par = creatorTags.createElement("p");

		for (ParagraphSectionItem item : this.getItems()) {
			par.appendChild(item.toHtmlModel(creatorTags));
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
		for (ParagraphSectionItem parItem : this.getItems()) {
			parItem.getValue().getItems().stream().filter(item -> item instanceof TextRunItem)
					.forEach(item -> text.append(((TextRunItem) item).getValue()));
		}

		return text.toString();
	}
}
