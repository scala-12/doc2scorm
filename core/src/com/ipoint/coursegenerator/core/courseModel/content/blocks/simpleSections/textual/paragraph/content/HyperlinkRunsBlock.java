package com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.HyperlinkRunsBlockCreationException;

/**
 * Hyperlink block. This block is {@link TextualRunsBlock} with hyperlink
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class HyperlinkRunsBlock extends TextualRunsBlock {

	private String url;

	public HyperlinkRunsBlock(List<AbstractContentRunItem<?>> runs, String url)
			throws BlockCreationException {
		super(runs);
		if (!this.setUrl(url)) {
			throw new HyperlinkRunsBlockCreationException(this, runs, url);
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
		if (isValidUrl(url)) {
			this.url = url;

			return true;
		} else {
			return false;
		}
	}

	public static boolean isValidUrl(String url) {
		return (url != null) && !url.isEmpty();
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
