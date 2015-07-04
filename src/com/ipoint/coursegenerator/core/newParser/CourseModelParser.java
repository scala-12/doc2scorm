package com.ipoint.coursegenerator.core.newParser;

import java.util.ArrayList;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ipoint.coursegenerator.core.courseModel.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.CoursePage;
import com.ipoint.coursegenerator.core.courseModel.CourseTreeNode;
import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.ParagraphHeaderBlock;

/**
 * Class for parsing to {@link CourseModel}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CourseModelParser {

	/**
	 * Parsing to {@link CourseModel} from {@link XWPFDocument}
	 * 
	 * @param document
	 *            Course as document MS Word
	 * @param courseName
	 *            Course name
	 * @param maxHeaderLevel
	 *            Max level of header that is paragraph. Max value is 1 that
	 *            first level of header. If this value is no more than 1 then
	 *            first level
	 * @return {@link CourseModel} of course
	 */
	public static CourseModel parse(XWPFDocument document, String courseName,
			Integer maxHeaderLevel) {
		CourseModel courseModel = new CourseModel(courseName);
		int maxHead = (maxHeaderLevel == null) ? 1 : ((maxHeaderLevel < 1) ? 1
				: maxHeaderLevel);

		// map for document tree
		ArrayList<Integer> levelMap = new ArrayList<Integer>();
		CoursePage page = new CoursePage();
		for (int i = 0; i < document.getBodyElements().size(); i++) {
			// Get element of docx
			IBodyElement bodyElement = document.getBodyElements().get(i);

			// TODO check pages before course body (Title list)

			Integer headLevel = null;
			if (bodyElement.getElementType().equals(BodyElementType.PARAGRAPH)) {
				// This is text paragraph: text, image or list
				XWPFParagraph paragraph = (XWPFParagraph) bodyElement;

				if (!paragraph.getRuns().isEmpty()) {
					// search header
					if (paragraph.getStyleID() != null) {
						headLevel = getNumberOfHeader(paragraph.getStyleID());
						if (headLevel != null) {// This it is header
							if (headLevel <= maxHead) {
								if (levelMap.size() == headLevel) {
									// level not changed - go to next element
									levelMap.set(
											levelMap.size() - 1,
											levelMap.get(levelMap.size() - 1) + 1);
								} else {
									// have new level
									if (levelMap.size() > headLevel) {
										// up level
										ArrayList<Integer> newMap = new ArrayList<Integer>();
										newMap.addAll(levelMap.subList(0,
												headLevel - 1));
										newMap.add(levelMap.get(headLevel - 1) + 1);
										levelMap = newMap; // remove extra
															// levels
									} else {
										// down level
										while (levelMap.size() < headLevel) {
											levelMap.add(0); // create new
																// levels
										}
									}
								}

								// search node of level in document tree
								CourseTreeNode treeNode = null;
								for (Integer lvl : levelMap) {
									if (treeNode == null) {
										// start node
										if (courseModel.getNode(lvl) == null) {
											// new node
											courseModel
													.addNode(new CourseTreeNode(
															paragraph.getText()));
										}
										treeNode = courseModel.getNode(lvl);
									} else {
										// other nodes
										if (treeNode.getNode(lvl) == null) {
											// new node
											treeNode.addNode(new CourseTreeNode(
													paragraph.getText()));
										}
										treeNode = treeNode.getNodes().get(lvl);
									}
								}

								if (!page.getBlocks().isEmpty()) {
									// new page because prev node have old page
									page = new CoursePage();
								}

								// set page to node
								treeNode.setPage(page);
							}
						}
					}
				}
			}

			if (headLevel == null) {
				AbstractParagraphBlock paragraphBlock = AbstractParagraphParser
						.parse(document.getBodyElements().subList(i,
								document.getBodyElements().size()));

				if (paragraphBlock instanceof ListBlock) {
					i += ((ListBlock) paragraphBlock).getItems().size() - 1;
				}

				if (paragraphBlock != null) {
					page.addBlock(paragraphBlock);
				}
			} else if (headLevel > maxHead) {
				ParagraphHeaderBlock paragraphBlock = ParagraphHeaderParser
						.parseDocx((XWPFParagraph) document.getBodyElements()
								.get(i), headLevel - maxHead);

				if (paragraphBlock != null) {
					page.addBlock(paragraphBlock);
				}
			}
		}

		return courseModel;
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
		if ((headId.equals("Heading1")) || (headId.equals("1"))) {
			resultVariable = 1;
		} else if ((headId.equals("Heading2")) || (headId.equals("2"))) {
			resultVariable = 2;
		} else if ((headId.equals("Heading3")) || (headId.equals("3"))) {
			resultVariable = 3;
		} else if ((headId.equals("Heading4")) || (headId.equals("4"))) {
			resultVariable = 4;
		} else if ((headId.equals("Heading5")) || (headId.equals("5"))) {
			resultVariable = 5;
		} else if ((headId.equals("Heading6")) || (headId.equals("6"))) {
			resultVariable = 6;
		} else if ((headId.equals("Heading7")) || (headId.equals("7"))) {
			resultVariable = 7;
		} else if ((headId.equals("Heading8")) || (headId.equals("8"))) {
			resultVariable = 8;
		} else if ((headId.equals("Heading9")) || (headId.equals("9"))) {
			resultVariable = 9;
		}
		return resultVariable;
	}

}
