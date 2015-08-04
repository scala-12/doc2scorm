package com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.textual;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.list.ListItem;
import com.ipoint.coursegenerator.core.parser.AbstractParser;
import com.ipoint.coursegenerator.core.parser.MathInfo;
import com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.textual.paragraph.ParagraphParser;

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
	 * @param mathInfo
	 *            Info about MathML formulas
	 * @return {@link ListBlock}
	 */
	public static ListBlock parse(List<XWPFParagraph> paragraphs,
			MathInfo mathInfo) {
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
						items.add(new ListItem(ListParser.parse(pars, mathInfo)));
						i += size - 1;
					} else {
						items.add(new ListItem(ParagraphParser.parse(par,
								mathInfo)));
					}
				}
				block = new ListBlock(items);

				block.setMarkerType(getMarkerTypeFromString(((List<XWPFParagraph>) paragraphs)
						.get(0).getNumFmt()));
			}
		}

		return block;
	}

	/**
	 * Returns equivalent of marker type in number format from string
	 * 
	 * @param markerName
	 *            Equivalent of marker type in string format
	 * @return Equivalent of marker type in number format. If equivalent not
	 *         founded then return type "simple marker"
	 */
	private static int getMarkerTypeFromString(String markerName) {
		int markerType = ListBlock.SIMPLE_MARKER;

		if (markerName != null) {
			if (markerName.equalsIgnoreCase("upperLetter")) {
				markerType = ListBlock.UPPER_LETTER_MARKER;
			} else if (markerName.equalsIgnoreCase("lowerLetter")) {
				markerType = ListBlock.LOWER_LETTER_MARKER;
			} else if (markerName.equalsIgnoreCase("upperRoman")) {
				markerType = ListBlock.UPPER_ROMAN_MARKER;
			} else if (markerName.equalsIgnoreCase("lowerRoman")) {
				markerType = ListBlock.LOWER_ROMAN_MARKER;
			} else if (markerName.equalsIgnoreCase("decimal")) {
				markerType = ListBlock.DECIMAL_MARKER;
			}
		}

		return markerType;
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
