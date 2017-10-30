package com.ipoint.coursegenerator.core.courseModel.content;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseTreeNode;

/**
 * Page. These includes {@link AbstractSectionBlock}
 * 
 * @see CourseModel
 * @author Kalashnikov Vladislav
 *
 */
public class TheoryPage extends AbstractPage<AbstractSectionBlock<?>> {

	private TheoryPage(CourseTreeNode parentNode) {
		super(parentNode);
	}

	public static TheoryPage createEmptyPage() {
		return new TheoryPage(null);
	}

}
