package com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ipoint.coursegenerator.core.courseModel.blocks.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.list.ListItem;
import com.ipoint.coursegenerator.core.parsers.AbstractParser;
import com.ipoint.coursegenerator.core.parsers.MathInfo;

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
	 * @param pars
	 *            Paragraphs as items of list
	 * @param mathInfo
	 *            Info about MathML formulas
	 * @return {@link ListBlock}
	 */
	public static ListBlock parse(XWPFParagraph par, MathInfo mathInfo) {
		ListBlock block = null;

		List<XWPFParagraph> list = getAtomListParagraphs(par);
		if ((list != null) && !list.isEmpty()) {
			block = new ListBlock(list.stream().map(item -> new ListItem(ParagraphParser.parse(item, mathInfo)))
					.collect(Collectors.toList()));
			block.setMarkerType(getMarkerTypeFromString(par.getNumFmt()));
		}

		return block;
	}

	/**
	 * Checking that is paragraph list item
	 * 
	 * @param par
	 *            Paragraph
	 * @return true if this paragraph is item of list
	 */
	public static boolean isListElement(XWPFParagraph par) {
		if ((par.getRuns().isEmpty()) || (par.getNumID() == null) || (par.getNumFmt() == null)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Search list elements when first element is head
	 * 
	 * @param head
	 *            Item in list with number
	 * @return Count of list items of list
	 */
	public static List<XWPFParagraph> getAtomListParagraphs(XWPFParagraph head) {
		if (isListElement(head)) {
			ArrayList<XWPFParagraph> listElems = new ArrayList<XWPFParagraph>();
			List<IBodyElement> elements = head.getBody().getBodyElements();

			boolean isAtomList = true;
			int number = 0;
			int level = 0;
			String marker = head.getNumFmt();
			number = head.getNumID().intValue();
			level = head.getNumIlvl().intValue();

			for (int i = elements.indexOf(head); (i < elements.size()) && isAtomList; i++) {
				if (elements.get(i) instanceof XWPFParagraph) {
					XWPFParagraph par = (XWPFParagraph) elements.get(i);
					isAtomList = isListElement(par) && (number == par.getNumID().intValue())
							&& (level == par.getNumIlvl().intValue() && (marker.equals(par.getNumFmt())));
					if (isAtomList) {
						listElems.add(par);
					}
				}
			}

			return listElems;
		}

		return null;
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

}
