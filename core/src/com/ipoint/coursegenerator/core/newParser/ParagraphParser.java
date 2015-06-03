package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;

public class ParagraphParser extends AbstractParser {

	@Override
	public AbstractBlock parseDoc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AbstractBlock> parseDocx(Object paragraph) {
		ArrayList<AbstractBlock> paragraphs = new ArrayList<AbstractBlock>();
		for (List<XWPFRun> runList : AbstractParser
				.preParseParagraphOnPieces((XWPFParagraph) paragraph)) {
			AbstractBlock block = null;
			if (runList.get(0).getClass().equals(XWPFHyperlinkRun.class)) {
				block = new HyperlinkParser().parseDocx(runList);
			} else {
				block = new TextParser().parseDocx(runList);
			}
			paragraphs.add(block);
		}

		return paragraphs;
	}

}
