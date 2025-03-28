package com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.ParagraphSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.ParagraphSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.TextualRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.parsers.AbstractParser;
import com.ipoint.coursegenerator.core.parsers.MathInfo;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.contentParser.HyperlinkParser;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.contentParser.TextParser;

/**
 * Parsing to {@link ParagraphSectionBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphParser extends AbstractParser {

	/**
	 * Parsing {@link XWPFParagraph} on pieces - simple text and hyperlink
	 * 
	 * @param paragraph
	 *            Paragraph for parsing
	 * @return List of text and hyperlink ({@link XWPFRun})
	 */
	private static List<List<XWPFRun>> parseParagraphOnPieces(XWPFParagraph paragraph) {
		ArrayList<List<XWPFRun>> runBlocks = new ArrayList<List<XWPFRun>>();

		ArrayList<Integer> formula = new ArrayList<Integer>();
		if (!paragraph.getCTP().getOMathList().isEmpty()) {
			Node node = paragraph.getCTP().getDomNode().getChildNodes().item(0);
			for (int i = 0; node != null; node = node.getNextSibling()) {
				if (node.getLocalName().equalsIgnoreCase("r")) {
					i++;
				} else if (node.getLocalName().equalsIgnoreCase("oMath")) {
					formula.add(i);
				}
			}
		}

		Integer k = (formula.isEmpty()) ? null : 0;

		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			Integer last = null; // number of last element in run
			if (run instanceof XWPFHyperlinkRun) {
				int childCount = run.getCTR().getDomNode().getParentNode().getChildNodes().getLength();
				last = i;
				for (int ind = 0; ind < childCount; ++ind) {
					// search latest run of hyperlink
					if ("r".equalsIgnoreCase(
							run.getCTR().getDomNode().getParentNode().getChildNodes().item(ind).getLocalName())) {
						++last;
					}
				}
			} else {
				for (last = i; (last < runs.size()) && !(runs.get(last) instanceof XWPFHyperlinkRun); last++) {
					// action: search latest non-hyperlink run
				}
			}

			if (k != null) {
				Integer start = i;

				while (start != null) {
					runBlocks.add(runs.subList(start, formula.get(k)));
					start = formula.get(k);
					runBlocks.add(null);
					for (; (k < formula.size() - 1) && (formula.get(k) != formula.get(k + 1)); k++) {
						runBlocks.add(null);
					}

					if ((formula.get(k) >= last) || (k <= (formula.size() - 1))) {
						start = null;
					}
				}

				runBlocks.add(runs.subList(formula.get(k), last));
			} else {
				runBlocks.add(runs.subList(i, last));
			}

			i = last - 1;
		}

		return runBlocks;
	}

	/**
	 * Parse to {@link ParagraphSectionBlock} which includes only text and images from
	 * {@link XWPFParagraph}
	 * 
	 * @param paragraph
	 *            Paragraph with text and images
	 * @param mathInfo
	 *            Info about MathML formulas
	 * @return {@link ParagraphSectionBlock}
	 */
	public static ParagraphSectionBlock parse(XWPFParagraph paragraph, MathInfo mathInfo)
			throws BlockCreationException, ItemCreationException {
		ArrayList<ParagraphSectionItem> itemsOfParagraph = new ArrayList<ParagraphSectionItem>();
		boolean hasFormuls = mathInfo != null;
		if (hasFormuls) {
			hasFormuls = !mathInfo.isReed();
		}

		if (hasFormuls && paragraph.getRuns().isEmpty()) {
			for (CTOMathPara mathPara : paragraph.getCTP().getOMathParaList()) {
				for (int i = 0; i < mathPara.getOMathList().size(); ++i) {
					TextualRunsBlock block = TextParser.parse(mathInfo, true);
					itemsOfParagraph.add(new ParagraphSectionItem(block));
				}
			}
		} else {
			for (List<XWPFRun> runList : parseParagraphOnPieces(paragraph)) {
				TextualRunsBlock block = null;
				if ((runList == null) && hasFormuls) {
					if (paragraph.getCTP().getOMathList() != null) {
						block = TextParser.parse(mathInfo, false);
					}
				} else if (!runList.isEmpty()) {
					if (runList.get(0) instanceof XWPFHyperlinkRun) {
						block = HyperlinkParser.parse(runList);
					} else {
						block = TextParser.parse(runList);
					}
				}

				if (block != null) {
					itemsOfParagraph.add(new ParagraphSectionItem(block));
				}
			}
		}

		return (itemsOfParagraph.isEmpty()) ? null
				: new ParagraphSectionBlock(itemsOfParagraph, ParagraphSectionBlock.convertAlignValue(paragraph.getAlignment()));
	}

}
