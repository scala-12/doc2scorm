package com.ipoint.coursegenerator.core.newParser;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.HyperlinkBlock;

/**
 * Parsing paragraph which includes only text and images as hyperlink
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class HyperlinkParser extends TextParser {

	@Override
	public AbstractBlock parseDoc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HyperlinkBlock parseDocx(Object element) {
		XWPFHyperlinkRun link = (XWPFHyperlinkRun) ((List<XWPFRun>) element)
				.get(0);

		String url = (link.getHyperlinkId() == null) ? new String() : link
				.getDocument().getHyperlinkByID(link.getHyperlinkId()).getURL();

		if (link.getAnchor() != null) {
			url = url.concat("#").concat(link.getAnchor());
		}

		return new HyperlinkBlock(url, new TextParser().parseDocx(element)
				.getItems());
	}

}
