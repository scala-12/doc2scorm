package com.ipoint.coursegenerator.core.newParser;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.ipoint.coursegenerator.core.courseModel.blocks.ParagraphHyperlinkBlock;

/**
 * Parsing to {@link ParagraphHyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class HyperlinkParser extends TextParser {

	/**
	 * Parsing to {@link ParagraphHyperlinkBlock} from list of {@link XWPFRun}
	 * 
	 * @return {@link ParagraphHyperlinkBlock}
	 */
	public static ParagraphHyperlinkBlock parse(List<XWPFRun> hyperRuns) {
		if (hyperRuns != null) {
			if (!hyperRuns.isEmpty()) {
				XWPFHyperlinkRun link = (XWPFHyperlinkRun) hyperRuns.get(0);
				String url = (link.getHyperlinkId() == null) ? new String()
						: link.getDocument()
								.getHyperlinkByID(link.getHyperlinkId())
								.getURL();

				if (link.getAnchor() != null) {
					url = url.concat("#").concat(link.getAnchor());
				}

				return new ParagraphHyperlinkBlock(TextParser.parse(hyperRuns)
						.getItems(), url);
			}
		}

		return null;
	}

}
