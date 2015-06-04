package com.ipoint.coursegenerator.core.newParser;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ListBlock;
import com.ipoint.coursegenerator.core.internalCourse.items.ListItem;

public class ListParser extends AbstractParser {

	@Override
	public AbstractBlock parseDoc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListBlock parseDocx(Object paragraphs) {
		ListBlock block = new ListBlock();

		for (XWPFParagraph par : (List<XWPFParagraph>) paragraphs) {
			ListItem item = new ListItem();
			item.setValue(new ParagraphParser().parseDocx(par));
			block.addItem(item);
		}

		return block;
	}

}
