package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.List;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;

public class HyperlinkBlock extends TextBlock {

	private String url;

	public HyperlinkBlock(String url, List<AbstractItem> list) {
		super(list);
		this.url = url;
	}

	public HyperlinkBlock(String url) {
		this(url, null);
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public Node toHtml() {
		// TODO Auto-generated method stub
		return null;
	}

}
