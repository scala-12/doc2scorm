package com.ipoint.coursegenerator.core.courseModel.content;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.AbstractContentSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.structure.ModelTreeNode;

/**
 * Page. These includes {@link AbstractSectionBlock}
 * 
 * @see CourseModel
 * @author Kalashnikov Vladislav
 *
 */
public class TheoryPage extends AbstractPage<AbstractContentSectionBlock<?>> {

	private TheoryPage(ModelTreeNode parentNode) {
		super(parentNode);
	}

	public static TheoryPage createEmptyPage() {
		return new TheoryPage(null);
	}

}
