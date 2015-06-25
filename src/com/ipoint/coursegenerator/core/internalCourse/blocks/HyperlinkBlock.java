package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.List;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.items.AbstractTextItem;

/**
 * Hyperlink block. This block is an extends of {@link TextBlock}
 * 
 * @see TextBlock
 * @see AbstractBlock
 * @author Kalashnikov Vladislav
 *
 */
public class HyperlinkBlock extends TextBlock {

	/**
	 * Full address of hyperlink.
	 */
	private String url;

	/**
	 * Create Hyperlink block with preset address and items
	 * 
	 * @param url
	 *            Address of hyperlink
	 * @param items
	 *            List of text and pictures
	 */
	public HyperlinkBlock(List<AbstractTextItem> items, String url) {
		super(items);
		if (!this.setUrl(url)) {
			// TODO: Exception
		}
	}

	/**
	 * Returns adress of hyperlink
	 * 
	 * @return Adress of hyperlink
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Change address of hyperlink
	 * 
	 * @param url
	 *            Address of hyperlink
	 */
	public boolean setUrl(String url) {
		if (url == null) {
			return false;
		} else {
			if (url.isEmpty()) {
				return false;
			} else {
				this.url = url;
				return true;
			}
		}
	}

	@Override
	public Node toHtml() {
		// TODO Auto-generated method stub
		return null;
	}

}
