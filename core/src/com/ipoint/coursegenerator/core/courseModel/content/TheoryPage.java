package com.ipoint.coursegenerator.core.courseModel.content;

import java.util.ArrayList;
import java.util.Set;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseTreeNode;
import com.ipoint.coursegenerator.core.utils.Tools;

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

	public Set<PictureInfo> getImages() {
		return Tools.getImagesRecursive(new ArrayList<AbstractBlock<?>>(this.getBlocks()));
	}

}
