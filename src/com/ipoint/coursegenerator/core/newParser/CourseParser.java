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

		// map for document tree
		ArrayList<Integer> levelMap = new ArrayList<Integer>();
		CoursePage page = new CoursePage();
		for (int i = 0; i < document.getBodyElements().size(); i++) {
			// Get element of docx
			IBodyElement bodyElement = document.getBodyElements().get(i);

			// TODO check pages before course body (Title list)

			// elements for parsing
			Object forParsing = null;
			if (bodyElement.getElementType().equals(BodyElementType.PARAGRAPH)) {
				// This is text paragraph: text, image or list

				// element docx as paragraph with runs
				XWPFParagraph paragraph = (XWPFParagraph) bodyElement;

				if (!paragraph.getRuns().isEmpty()) {
					Integer headLevel = null;

					// search header
					if (paragraph.getStyleID() != null) {
						headLevel = getNumberOfHeader(paragraph.getStyleID());
						if (headLevel != null) {// This it is header
							if (levelMap.size() == headLevel) {
								// level not changed - go to next element
								levelMap.set(levelMap.size() - 1,
										levelMap.get(levelMap.size() - 1) + 1);
							} else {
								// have new level
								if (levelMap.size() > headLevel) {
									// up level
									ArrayList<Integer> newMap = new ArrayList<Integer>();
									newMap.addAll(levelMap.subList(0,
											headLevel - 1));
									newMap.add(levelMap.get(headLevel) + 1);
									levelMap = newMap; // remove extra levels
								} else {
									// down level
									while (levelMap.size() < headLevel) {
										levelMap.add(0); // create new levels
									}
								}
							}

							// search node of level in document tree
							CourseTreeItem treeNode = null;
							for (Integer lvl : levelMap) {
								if (treeNode == null) {
									// start node
									if (course.getItem(lvl) == null) {
										// new node
										course.addItem(new CourseTreeItem(
												paragraph.getText()));
									}
									treeNode = course.getItem(lvl);
								} else {
									// other nodes
									if (treeNode.getItem(lvl) == null) {
										// new node
										treeNode.addItem(new CourseTreeItem(
												paragraph.getText()));
									}
									treeNode = treeNode.getCourseTree()
											.get(lvl);
								}
							}

							if (page.getAllBlocks().isEmpty()) {
								// page is empty. Add this page to the node?
								if (treeNode.getCourseTree().isEmpty()) {
									// add because this page is first
									treeNode.setPage(page);
								}
							} else {
								// adding the page to this node
								page = new CoursePage();
								treeNode.setPage(page);
							}
						}
					}

					if (headLevel == null) { // It is text, not header
						Integer size = ParagraphParser.listSize(i, paragraph,
								document.getBodyElements());
						if (size == null) {
							// is simple text with image
							forParsing = (XWPFParagraph) bodyElement;
						} else {
							// It is list
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
					BodyElementType.TABLE)) { // It is a table
				forParsing = (XWPFTable) bodyElement;
			}

			// element for inserting in ParagraphBlock (List, text or table)
			List<AbstractBlock> blockItems = new ParagraphParser()
					.parseDocx(forParsing);

			if (blockItems != null) { // Have nothing for adding
				page.addParagraph(new ParagraphBlock(blockItems));
			}
		}

		return course;
	}

	/**
	 * Search header level
	 * 
	 * @param headId
	 *            ID of header
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
