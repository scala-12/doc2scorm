package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ipoint.coursegenerator.core.courseModel.blocks.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.items.ListItem;

/**
 * Parsing to {@link ListBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ListParser extends AbstractParser {

	/**
	 * Parsing to {@link ListBlock} from list of {@link XWPFParagraph}
	 * 
	 * @param paragraphs
	 *            Paragraphs as items of list
	 * @return {@link ListBlock}
	 */
	public static ListBlock parse(List<XWPFParagraph> paragraphs) {
		ListBlock block = null;

		if (paragraphs != null) {
			if (!paragraphs.isEmpty()) {
				ArrayList<ListItem> items = new ArrayList<ListItem>();
				for (XWPFParagraph par : paragraphs) {
					items.add(new ListItem(ParagraphParser.parse(par)));
				}
				block = new ListBlock(items);

				block.setMarkerType(ListBlock
						.getMarkerTypeFromString(((List<XWPFParagraph>) paragraphs)
								.get(0).getNumFmt()));
			}
		}

		return block;
	}

}
