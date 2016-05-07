package com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ipoint.coursegenerator.core.courseModel.blocks.textual.AbstractTextualBlock;
import com.ipoint.coursegenerator.core.parsers.MathInfo;
import com.ipoint.coursegenerator.core.parsers.courseParser.AbstractParagraphParser;

/**
 * Method for parsing to {@link AbstractTextualBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class AbstractTextualParagraphParser extends AbstractParagraphParser {

	/**
	 * Parsing from list of {@link IBodyElement} to
	 * {@link AbstractTextualBlock}
	 * 
	 * @param par
	 *            Paragraph
	 * @param mathInfo
	 *            Info about MathML formulas
	 * @return {@link AbstractTextualBlock}
	 */
	public static AbstractTextualBlock<?> parse(XWPFParagraph par,
			MathInfo mathInfo) {
		AbstractTextualBlock<?> block = null;

		if (!par.getRuns().isEmpty() || (par.getCTP() != null)) {
			if (ListParser.isListElement(par)) {
				// It is list
				block = ListParser.parse(par, mathInfo);
			} else {
				// is simple text with image or formula
				block = ParagraphParser.parse(par, mathInfo);
			}
		}

		return block;
	}

}
