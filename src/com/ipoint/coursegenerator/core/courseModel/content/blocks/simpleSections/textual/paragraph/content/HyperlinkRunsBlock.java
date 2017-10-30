package com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Hyperlink block. This block is {@link TextualRunsBlock} with hyperlink
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class HyperlinkRunsBlock extends TextualRunsBlock {

	private String url;

	public HyperlinkRunsBlock(List<AbstractContentRunItem<?>> runs, String url) {
		super(runs);
		if (!this.setUrl(url)) {
			// TODO: Exception empty url of link
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
		hyperlink.appendChild(super.toHtml(creatorTags));
		hyperlink.setAttribute("href", this.getUrl());
		hyperlink.setAttribute("target", "_blank");

		return hyperlink;
	}

}
