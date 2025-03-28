package com.ipoint.coursegenerator.core.parsers.courseParser;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.AbstractContentSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.parsers.AbstractParser;
import com.ipoint.coursegenerator.core.parsers.MathInfo;
import com.ipoint.coursegenerator.core.parsers.courseParser.tabularParagraphParser.TableParser;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.AbstractTextualParagraphParser;

/**
 * Class for parsing to {@link AbstractSectionBlock}
 * 
 * @author Kalashnikov Vladslav
 *
 */
public abstract class AbstractParagraphParser extends AbstractParser {

	/**
	 * 
	 * 
	 * Parsing to one {@link AbstractContentSectionBlock} from list of
	 * {@link IBodyElement}
	 * 
	 * @param bodyElement
	 *            Paragraph
	 * @param mathInfo
	 *            Info about MathML formulas
	 * @return {@link AbstractContentSectionBlock} of paragraph
	 */
	public static AbstractContentSectionBlock<?> parse(IBodyElement bodyElement, MathInfo mathInfo)
			throws BlockCreationException, ItemCreationException {
		AbstractContentSectionBlock<?> block = null;

		if (bodyElement.getElementType().equals(BodyElementType.PARAGRAPH)) {
			block = AbstractTextualParagraphParser.parse((XWPFParagraph) bodyElement, mathInfo);
		} else if (bodyElement.getElementType().equals(BodyElementType.TABLE)) {
			block = TableParser.parse((XWPFTable) bodyElement, mathInfo);
		}

		return block;
	}

}
