package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.ipoint.coursegenerator.core.internalCourse.Course.Course;
import com.ipoint.coursegenerator.core.internalCourse.Course.CoursePage;
import com.ipoint.coursegenerator.core.internalCourse.Course.CourseTreeItem;
import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ParagraphBlock;

public class CourseParser {

	public static Course parse(XWPFDocument document, String manifest,
			String courseName) {
		Course course = new Course();
		ArrayList<Integer> levelMap = new ArrayList<Integer>();
		CoursePage page = new CoursePage();
		for (int i = 0; i < document.getBodyElements().size(); i++) {
			IBodyElement bodyElement = document.getBodyElements().get(i);
			// TODO check pages before course body

			Object forParsing = null;

			if (bodyElement.getElementType().equals(BodyElementType.PARAGRAPH)) {
				XWPFParagraph paragraph = (XWPFParagraph) bodyElement;
				if (!paragraph.getRuns().isEmpty()) {
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
								levelMap = (ArrayList<Integer>) levelMap
										.subList(
												0,
												getNonNumericHeaderParser(paragraph
														.getStyleID())); // remove
																			// extra
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
									treeNode = treeNode.getCourseTree()
											.get(lvl);
								}
							}

							if (page.getAllBlocks().isEmpty()) {
								treeNode.setPage(page);
							} else {
								treeNode.setPage(page);
								page = new CoursePage();
							}
						}
						forParsing = (XWPFParagraph) bodyElement;
					} else {
						Integer size = ParagraphParser.listSize(i, paragraph);
						if (size == null) {
							forParsing = (XWPFParagraph) bodyElement;
						} else {
							ArrayList<IBodyElement> listItems = new ArrayList<IBodyElement>();
							for (int j = 0; j < size; j++, i++) {
								listItems.add((XWPFParagraph) document.getBodyElements().get(i));
							}
							i--;
							forParsing = listItems;
						}
					}
				}
			} else if (bodyElement.getElementType().equals(
					BodyElementType.TABLE)) {
				forParsing = (XWPFTable) bodyElement;
			}

			List<AbstractBlock> blockItems = new ParagraphParser()
					.parseDocx(forParsing);

			if (blockItems != null) {
				page.addParagraph(new ParagraphBlock(blockItems));
			}
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
