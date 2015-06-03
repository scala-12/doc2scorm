package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ipoint.coursegenerator.core.internalCourse.Course.Course;
import com.ipoint.coursegenerator.core.internalCourse.Course.CoursePage;
import com.ipoint.coursegenerator.core.internalCourse.Course.CourseTreeItem;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ParagraphBlock;

public class CourseParser {

	private static boolean isListElement(XWPFParagraph par) {
		if ((par.getNumID() != null) && (getCurrentParagraphStyleID(par) <= 0)) {
			return true;
		} else {
			return false;
		}
	}

	public static Course parse(XWPFDocument document, String manifest,
			String courseName) {
		Course course = new Course();
		ArrayList<Integer> levelMap = new ArrayList<Integer>();
		CoursePage page = new CoursePage();
		for (int i = 0; i < document.getBodyElements().size(); i++) {
			IBodyElement bodyElement = document.getBodyElements().get(i);
			// TODO check pages before course body
			ParagraphBlock block = null;
			if (bodyElement.getElementType().equals(BodyElementType.PARAGRAPH)) {
				XWPFParagraph paragraph = (XWPFParagraph) bodyElement;

				if (paragraph.getStyleID() != null) {
					if (levelMap.size() == getNonNumericHeaderParser(paragraph
							.getStyleID())) {
						levelMap.set(levelMap.size() - 1,
								levelMap.get(levelMap.size() - 1) + 1); // to
																		// next
																		// element
																		// on
																		// the
																		// level
					} else {
						if (levelMap.size() > getNonNumericHeaderParser(paragraph
								.getStyleID())) {
							levelMap = (ArrayList<Integer>) levelMap.subList(0,
									getNonNumericHeaderParser(paragraph
											.getStyleID())); // remove extra
																// levels
						} else {
							while (levelMap.size() < getNonNumericHeaderParser(paragraph
									.getStyleID())) {
								levelMap.add(0); // create new levels
							}
						}

						CourseTreeItem treeNode = null;
						for (Integer lvl : levelMap) {
							if (course.getItem(lvl) == null) {
								course.addItem(new CourseTreeItem());
							}
							if (treeNode == null) {
								treeNode = course.getItem(lvl);
							} else {
								treeNode = treeNode.getCourseTree().get(lvl);
							}
						}

						if (page.getAllBlocks().isEmpty()) {
							treeNode.setPage(page);
						} else {
							treeNode.setPage(page);
							page = new CoursePage();
						}
					}
				}

				if (isListElement(paragraph)) {
					ArrayList<XWPFParagraph> listItems = new ArrayList<XWPFParagraph>();
					for (; i < document.getBodyElements().size(); i++) {
						XWPFParagraph par = (XWPFParagraph) document
								.getBodyElements().get(i);
						if (isListElement(par)) {
							listItems.add(par);
						}
					}
					block = new ParagraphBlock(
							new ListParser().parseDocx(listItems));
				} else {
					block = new ParagraphBlock(
							new ParagraphParser().parseDocx(paragraph));
				}

				/*
				 * List<XWPFRun> runs = paragraph.getRuns(); for (int i = 0; i <
				 * runs.size(); i++) { XWPFRun run = runs.get(i); Integer end =
				 * null; if (run.getClass().equals(XWPFHyperlinkRun.class)) {
				 * end =
				 * run.getCTR().getDomNode().getParentNode().getChildNodes(
				 * ).getLength() - 1; } if (end == null) { for (end = i; (end <
				 * runs.size()) &&
				 * !runs.get(end).getClass().equals(XWPFHyperlinkRun.class);
				 * end++) { } page.addBlock(new
				 * TextParser().parseDocx((IBodyElement) runs.subList(i, end)));
				 * } else { page.addBlock(new
				 * HyperlinkParser().parseDocx((IBodyElement) runs.subList(i,
				 * end))); } i = end; }
				 */
			} else if (bodyElement.getElementType().equals(
					BodyElementType.TABLE)) {
				block = new ParagraphBlock(
						new TableParser().parseDocx(bodyElement));
			}

			page.addParagraph(block);
		}

		return course;
	}

	public static boolean isNumericParagraphStyle(String stringStyleID) {
		if (stringStyleID != null) {
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher matcher = pattern.matcher(stringStyleID);
			return matcher.matches();
		}
		return false;
	}

	private static Integer getCurrentParagraphStyleID(
			XWPFParagraph xwpfParagraph) {
		int resultVariable = 0;
		try {
			if (xwpfParagraph.getStyleID() != null) {
				if (isNumericParagraphStyle(xwpfParagraph.getStyleID())) {
					resultVariable = Integer
							.valueOf(xwpfParagraph.getStyleID());
					if (resultVariable == 20) {
						resultVariable = (int) 2;
					}
				} else {
					resultVariable = getNonNumericHeaderParser(xwpfParagraph
							.getStyleID());
				}
			} else {
				resultVariable = (int) 100;
			}
		} catch (NumberFormatException e) {
			resultVariable = (int) 100;
		}
		return resultVariable;
	}

	private static Integer getNonNumericHeaderParser(String headId) {
		Integer resultVariable = 0;
		if (headId.equals("Heading1")) {
			resultVariable = 1;
		} else if (headId.equals("Heading2")) {
			resultVariable = 2;
		} else if (headId.equals("Heading3")) {
			resultVariable = 3;
		} else if (headId.equals("Heading4")) {
			resultVariable = 4;
		} else if (headId.equals("Heading5")) {
			resultVariable = 5;
		} else if (headId.equals("Heading6")) {
			resultVariable = 6;
		} else if (headId.equals("Heading7")) {
			resultVariable = 7;
		} else if (headId.equals("Heading8")) {
			resultVariable = 8;
		} else if (headId.equals("Heading9")) {
			resultVariable = 9;
		}
		return resultVariable;
	}

}
