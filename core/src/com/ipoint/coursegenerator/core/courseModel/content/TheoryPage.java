package com.ipoint.coursegenerator.core.courseModel.content;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseTreeNode;

/**
 * Page. These includes {@link AbstractParagraphBlock}
 * 
 * @see CourseModel
 * @author Kalashnikov Vladislav
 *
 */
public class TheoryPage extends AbstractPage<AbstractParagraphBlock<?>> {

	private TheoryPage(CourseTreeNode parentNode) {
		super(parentNode);
	}

	public static TheoryPage createEmptyPage() {
		return new TheoryPage(null);
	}

}
