package com.ipoint.coursegenerator.core.parser.courseModel.paragraphs;

import java.util.List;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.parser.AbstractParser;
import com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.tabular.TableParser;
import com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.textual.AbstractTextualParagraphParser;

/**
 * Class for parsing to {@link AbstractParagraphBlock}
 * 
 * @author Kalashnikov Vladslav
 *
 */
public abstract class AbstractParagraphParser extends AbstractParser {

	/**
	 * Parsing to one {@link AbstractParagraphBlock} from list of
	 * {@link IBodyElement}
	 * 
	 * @param bodyElements
	 *            List of paragraphs. If first paragraph is not list then
	 *            parsing only it.
	 * @return {@link AbstractParagraphBlock} of paragraph
	 */
	public static AbstractParagraphBlock parse(List<IBodyElement> bodyElements) {
		AbstractParagraphBlock block = null;

		if (!bodyElements.isEmpty()) {
			if (bodyElements.get(0).getElementType()
					.equals(BodyElementType.PARAGRAPH)) {
				block = AbstractTextualParagraphParser.parse(bodyElements);
			} else if (bodyElements.get(0).getElementType()
					.equals(BodyElementType.TABLE)) {
				block = TableParser.parse((XWPFTable) bodyElements.get(0));
			}
		}

		return block;
	}

}
