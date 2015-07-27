package com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.textual.paragraph.content;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.HyperlinkBlock;

/**
 * Parsing to {@link HyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class HyperlinkParser extends TextParser {

	/**
	 * Parsing to {@link HyperlinkBlock} from list of {@link XWPFRun}
	 * 
	 * @return {@link HyperlinkBlock}
	 */
	public static HyperlinkBlock parse(List<XWPFRun> hyperRuns) {
		if (hyperRuns != null) {
			if (!hyperRuns.isEmpty()) {
				XWPFHyperlinkRun link = (XWPFHyperlinkRun) hyperRuns.get(0);
				String url = (link.getHyperlinkId() == null) ? new String()
						: link.getDocument()
								.getHyperlinkByID(link.getHyperlinkId())
								.getURL();

				if (link.getAnchor() != null) {
					url = url + "#" + link.getAnchor();
				}

				return new HyperlinkBlock(TextParser.parse(hyperRuns)
						.getItems(), url);
			}
		}

		return null;
	}

}
