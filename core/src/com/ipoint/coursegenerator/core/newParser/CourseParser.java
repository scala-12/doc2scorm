package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;
import java.util.List;

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

/**
 * Parsing document
 * 
 * @author Kalashnikov
 *
 */
public class CourseParser {

	/**
	 * Parsing docx document
	 * 
	 * @param document
	 *            Document docx
	 * @param manifest
	 * @param courseName
	 * @return Special model of docx
	 */
	public static Course parse(XWPFDocument document, String manifest,
			String courseName) {
		Course course = new Course();
		ArrayList<Integer> levelMap = new ArrayList<Integer>(); // map for
																// document tree
		CoursePage page = new CoursePage();
		for (int i = 0; i < document.getBodyElements().size(); i++) {
			IBodyElement bodyElement = document.getBodyElements().get(i);
			// TODO check pages before course body (Title list)

			Object forParsing = null;
			if (bodyElement.getElementType().equals(BodyElementType.PARAGRAPH)) { // is
																					// text
																					// paragraph
				XWPFParagraph paragraph = (XWPFParagraph) bodyElement;
				if (!paragraph.getRuns().isEmpty()) {
					Integer headLevel = null;

					if (paragraph.getStyleID() != null) { // search header
						headLevel = getNumberOfHeader(paragraph.getStyleID());

						if (headLevel != null) { // it is header
							if (levelMap.size() == headLevel) { // level not
																// changed
								levelMap.set(levelMap.size() - 1,
										levelMap.get(levelMap.size() - 1) + 1); // to
																				// next
																				// element
																				// on
																				// the
																				// level
							} else {
								if (levelMap.size() > headLevel) { // new level
																	// is up
									ArrayList<Integer> newMap = new ArrayList<Integer>();
									newMap.addAll(levelMap.subList(0,
											headLevel - 1));
									newMap.add(levelMap.get(headLevel) + 1);
									levelMap = newMap; // remove extra levels
								} else { // new level is down
									while (levelMap.size() < headLevel) {
										levelMap.add(0); // create new levels
									}
								}
							}

							// search node of level in document tree
							CourseTreeItem treeNode = null;
							for (Integer lvl : levelMap) {
								if (treeNode == null) {
									if (course.getItem(lvl) == null) {
										course.addItem(new CourseTreeItem(
												paragraph.getText()));
									}
									treeNode = course.getItem(lvl);
								} else {
									if (treeNode.getItem(lvl) == null) {
										treeNode.addItem(new CourseTreeItem(
												paragraph.getText()));
									}
									treeNode = treeNode.getCourseTree()
											.get(lvl);
								}
							}

							// TODO: write describe
							if (page.getAllBlocks().isEmpty()) {
								treeNode.setPage(page);
							} else {
								page = new CoursePage();
								treeNode.setPage(page);
							}
						} else {

						}
					}

					if (headLevel == null) { // if it not header
						Integer size = ParagraphParser.listSize(i, paragraph, document.getBodyElements());
						if (size == null) { // is simple text with image
							forParsing = (XWPFParagraph) bodyElement;
						} else {
							ArrayList<IBodyElement> listItems = new ArrayList<IBodyElement>();
							for (int j = 0; j < size; j++, i++) {
								listItems.add((XWPFParagraph) document
										.getBodyElements().get(i));
							}
							i--;
							forParsing = listItems;
						}
					}
				}
			} else if (bodyElement.getElementType().equals(
					BodyElementType.TABLE)) { // is table
				forParsing = (XWPFTable) bodyElement;
			}

			// element for ParagraphBlock
			List<AbstractBlock> blockItems = new ParagraphParser()
					.parseDocx(forParsing);

			if (blockItems != null) {
				page.addParagraph(new ParagraphBlock(blockItems));
			}
		}

		return course;
	}

	/**
	 * Search header level
	 * @param headId ID of header
	 * @return
	 */
	private static Integer getNumberOfHeader(String headId) {
		Integer resultVariable = null;
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
