package com.ipoint.coursegenerator.core.courseModel.content;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseTreeNode;

/**
 * Page. These includes {@link AbstractParagraphBlock}
 * 
 * @see CourseModel
 * @author Kalashnikov Vladislav
 *
 */
public class TheoryPage extends AbstractPage {

	protected TheoryPage(CourseTreeNode parentNode) {
		super(parentNode);
	}

	public final static String CONTENT_DIV_ID = "content_div";

}
