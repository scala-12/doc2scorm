package com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.contentParser;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.HyperlinkRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;

/**
 * Parsing to {@link HyperlinkRunsBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class HyperlinkParser extends TextParser {

	/**
	 * Parsing to {@link HyperlinkRunsBlock} from list of {@link XWPFRun}
	 * 
	 * @return {@link HyperlinkRunsBlock}
	 */
	public static HyperlinkRunsBlock parse(List<XWPFRun> hyperRuns)
			throws BlockCreationException, ItemCreationException {
		if (hyperRuns != null) {
			if (!hyperRuns.isEmpty()) {
				XWPFHyperlinkRun link = (XWPFHyperlinkRun) hyperRuns.get(0);
				String url = (link.getHyperlinkId() == null) ? new String()
						: link.getDocument().getHyperlinkByID(link.getHyperlinkId()).getURL();

				if (link.getAnchor() != null) {
					url = url + "#" + link.getAnchor();
				}

				return new HyperlinkRunsBlock(TextParser.parse(hyperRuns).getItems(), url);
			}
		}

		return null;
	}

}
