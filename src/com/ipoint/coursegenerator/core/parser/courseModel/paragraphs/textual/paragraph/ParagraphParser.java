package com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.textual.paragraph;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.ParagraphItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.TextBlock;
import com.ipoint.coursegenerator.core.parser.AbstractParser;
import com.ipoint.coursegenerator.core.parser.MathInfo;
import com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.textual.paragraph.content.HyperlinkParser;
import com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.textual.paragraph.content.TextParser;

/**
 * Parsing to {@link ParagraphBlock}
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
				last = i + run.getCTR().getDomNode().getParentNode().getChildNodes().getLength();
			} else {
				for (last = i; (last < runs.size()) && !(runs.get(last) instanceof XWPFHyperlinkRun); last++) {
					// waiting
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
	 * Parse to {@link ParagraphBlock} which includes only text and images from
	 * {@link XWPFParagraph}
	 * 
	 * @param paragraph
	 *            Paragraph with text and images
	 * @param mathInfo
	 *            Info about MathML formulas
	 * @return {@link ParagraphBlock}
	 */
	public static ParagraphBlock parse(XWPFParagraph paragraph, MathInfo mathInfo) {
		ArrayList<ParagraphItem> itemsOfParagraph = new ArrayList<ParagraphItem>();
		boolean hasFormuls = mathInfo != null;
		if (hasFormuls) {
			hasFormuls = !mathInfo.isReed();
		}

		if (paragraph.getRuns().isEmpty() && hasFormuls) {
			for (CTOMathPara mathPara : paragraph.getCTP().getOMathParaList()) {
				for (int i = 0; i < mathPara.getOMathList().size(); ++i) {
					TextBlock block = TextParser.parse(mathInfo, true);
					itemsOfParagraph.add(new ParagraphItem(block));
				}
			}
		} else {
			for (List<XWPFRun> runList : parseParagraphOnPieces(paragraph)) {
				TextBlock block = null;
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
					itemsOfParagraph.add(new ParagraphItem(block));
				}
			}
		}

		return (itemsOfParagraph.isEmpty()) ? null
				: new ParagraphBlock(itemsOfParagraph,
						ParagraphBlock.convertAlignValue(paragraph.getAlignment()));
	}

}
