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
				int number = paragraphs.get(0).getNumID().intValue();
				int level = paragraphs.get(0).getNumIlvl().intValue();
				for (int i = 0; i < paragraphs.size(); ++i) {
					XWPFParagraph par = paragraphs.get(i);
					if ((par.getNumID().intValue() == number)
							&& (par.getNumIlvl().intValue() > level)) {
						ArrayList<XWPFParagraph> pars = new ArrayList<XWPFParagraph>();
						int size = getAtomListSize(i, paragraphs);
						pars.addAll(paragraphs.subList(i, i + size));
						items.add(new ListItem(ListParser.parse(pars)));
						i += size - 1;
					} else {
						items.add(new ListItem(ParagraphParser.parse(par)));
					}
				}
				block = new ListBlock(items);

				block.setMarkerType(ListBlock
						.getMarkerTypeFromString(((List<XWPFParagraph>) paragraphs)
								.get(0).getNumFmt()));
			}
		}

		return block;
	}

	private static int getAtomListSize(int start, List<XWPFParagraph> paragraphs) {
		int number = paragraphs.get(start).getNumID().intValue();
		int level = paragraphs.get(start).getNumIlvl().intValue();
		boolean isAtom = true;
		int end = start;
		for (; (end < paragraphs.size()) && isAtom; ++end) {
			XWPFParagraph par = paragraphs.get(end);
			isAtom = (number == par.getNumID().intValue())
					&& (level == par.getNumIlvl().intValue());
			if (!isAtom) {
				--end;
			}
		}

		return end - start;
	}

}
