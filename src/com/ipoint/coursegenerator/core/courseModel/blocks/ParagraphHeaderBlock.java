package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.ParagraphItem;

public class ParagraphHeaderBlock extends ParagraphBlock {

	private int level;

	public ParagraphHeaderBlock(List<ParagraphItem> items) {
		this(items, null);
	}

	public ParagraphHeaderBlock(List<ParagraphItem> items, Integer level) {
		super(items);
		this.setLevel(level);
	}

	public boolean setLevel(Integer level) {
		if (level != null) {
			if (level >= 0) {
				this.level = level;
				return true;
			}
		}

		return false;
	}

	public int getLevel() {
		return this.level;
	}

	@Override
	public Element toHtml(Document creatorTags) {
		Element header = creatorTags.createElement("h".concat(String
				.valueOf(this.getLevel() + 1)));

		if (this.getItems().size() == 1) {
			NodeList childs = this.getItems().get(0).toHtml(creatorTags)
					.getChildNodes();
			for (int i = 0; i < childs.getLength(); ++i) {
				header.appendChild(childs.item(i));
			}
		} else {
			for (ParagraphItem item : this.getItems()) {
				header.appendChild(item.toHtml(creatorTags));
			}
		}

		return header;
	}

}
