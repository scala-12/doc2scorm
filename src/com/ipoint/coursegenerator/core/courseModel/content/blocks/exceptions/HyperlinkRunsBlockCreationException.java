package com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.HyperlinkRunsBlock;

public class HyperlinkRunsBlockCreationException extends BlockCreationException {

	private static final long serialVersionUID = -8776402507712074798L;

	private final String url;

	public HyperlinkRunsBlockCreationException(AbstractBlock<?> block, List<? extends AbstractItem<?>> items,
			String url) {
		super(block, items, (HyperlinkRunsBlock.isValidUrl(url)) ? null : "Exception empty url of link");
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

}
