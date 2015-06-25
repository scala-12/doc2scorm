package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ipoint.coursegenerator.core.internalCourse.blocks.ListBlock;
import com.ipoint.coursegenerator.core.internalCourse.items.ListItem;

public class ListParser extends AbstractParser {


	public ListBlock parseDocx(List<XWPFParagraph> paragraphs) {
		ListBlock block = null;
		
		if (paragraphs != null) {
			if (paragraphs.isEmpty()) {
				ArrayList<ListItem> items = new ArrayList<ListItem>();
				for (XWPFParagraph par : paragraphs) {
					items.add(new ListItem(new ParagraphParser().parseDocx(par)));
				}
				block = new ListBlock(items);

				block.setMarkerType(((List<XWPFParagraph>) paragraphs).get(0)
						.getNumFmt());
			}
		}		

		return block;
	}

}
