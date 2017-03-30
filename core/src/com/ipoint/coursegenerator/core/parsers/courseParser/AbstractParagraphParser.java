package com.ipoint.coursegenerator.core.parsers.courseParser;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.parsers.AbstractParser;
import com.ipoint.coursegenerator.core.parsers.MathInfo;
import com.ipoint.coursegenerator.core.parsers.courseParser.tabularParagraphParser.TableParser;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.AbstractTextualParagraphParser;

/**
 * Class for parsing to {@link AbstractParagraphBlock}
 * 
 * @author Kalashnikov Vladslav
 *
 */
public abstract class AbstractParagraphParser extends AbstractParser {

	/**
	 * 
	 * 
	 * Parsing to one {@link AbstractParagraphBlock} from list of
	 * {@link IBodyElement}
	 * 
	 * @param bodyElement
	 *            Paragraph
	 * @param mathInfo
	 *            Info about MathML formulas
	 * @return {@link AbstractParagraphBlock} of paragraph
	 */
	public static AbstractParagraphBlock<?> parse(IBodyElement bodyElement, MathInfo mathInfo) {
		AbstractParagraphBlock<?> block = null;

		if (bodyElement.getElementType().equals(BodyElementType.PARAGRAPH)) {
			block = AbstractTextualParagraphParser.parse((XWPFParagraph) bodyElement, mathInfo);
		} else if (bodyElement.getElementType().equals(BodyElementType.TABLE)) {
			block = TableParser.parse((XWPFTable) bodyElement, mathInfo);
		}

		return block;
	}

}
