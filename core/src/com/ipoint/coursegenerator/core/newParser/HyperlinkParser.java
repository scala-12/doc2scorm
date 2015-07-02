package com.ipoint.coursegenerator.core.newParser;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.ipoint.coursegenerator.core.courseModel.blocks.HyperlinkBlock;

/**
 * Parsing paragraph which includes only text and images as hyperlink
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class HyperlinkParser extends TextParser {

	/**
	 * Parse runs with hyperlink to Hyperlink block
	 */
	public HyperlinkBlock parseDocx(List<XWPFRun> hyperRuns) {
		if (hyperRuns != null) {
			if (hyperRuns.isEmpty()) {
				return null;
			} else {
				XWPFHyperlinkRun link = (XWPFHyperlinkRun) hyperRuns.get(0);
				String url = (link.getHyperlinkId() == null) ? new String()
						: link.getDocument()
								.getHyperlinkByID(link.getHyperlinkId())
								.getURL();

				if (link.getAnchor() != null) {
					url = url.concat("#").concat(link.getAnchor());
				}

				return new HyperlinkBlock(new TextParser().parseDocx(hyperRuns)
						.getItems(), url);
			}
		} else {
			return null;
		}
	}

}
