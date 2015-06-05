package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ListBlock;
import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;
import com.ipoint.coursegenerator.core.internalCourse.items.ListItem;

public class ListParser extends AbstractParser {

	@Override
	public AbstractBlock parseDoc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListBlock parseDocx(Object paragraphs) {
		ArrayList<AbstractItem> items = new ArrayList<AbstractItem>();

		for (XWPFParagraph par : (List<XWPFParagraph>) paragraphs) {
			items.add(new ListItem(new ParagraphParser().parseDocx(par)));
		}
		ListBlock block = new ListBlock(items);

		block.setMarkerType(((List<XWPFParagraph>) paragraphs).get(0)
				.getNumFmt());

		return block;
	}

}
