package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.AbstractTextItem;

/**
 * Hyperlink block. This block is {@link ParagraphTextBlock} with hyperlink
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphHyperlinkBlock extends ParagraphTextBlock {

	private String url;

	public ParagraphHyperlinkBlock(List<AbstractTextItem> items, String url) {
		super(items);
		if (!this.setUrl(url)) {
			// TODO: Exception
		}
	}

	public String getUrl() {
		return this.url;
	}

	/**
	 * Change address of hyperlink
	 * 
	 * @param url
	 *            New address
	 * @return if new address is not null then true
	 */
	public boolean setUrl(String url) {
		if (url != null) {
			if (!url.isEmpty()) {
				this.url = url;
				return true;
			}
		}

		return false;
	}

	/**
	 * @return html-element a
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element hyperlink = creatorTags.createElement("a");
		hyperlink.appendChild(super.toHtml(creatorTags, true));
		hyperlink.setAttribute("href", this.getUrl());

		return hyperlink;
	}

}
