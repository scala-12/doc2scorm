package com.ipoint.coursegenerator.core.courseModel.content;

import java.util.ArrayList;
import java.util.List;

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
public class TestingPage extends AbstractPage {

	private final ArrayList<AbstractParagraphBlock<?>> introBlocks;

	private TestingPage(CourseTreeNode parentNode) {
		super(parentNode);
		this.introBlocks = new ArrayList<>();
	}

	public static TestingPage createEmptyPage() {
		return new TestingPage(null);
	}

	public List<AbstractParagraphBlock<?>> getIntroBlocks() {
		return new ArrayList<>(this.introBlocks);
	}

	public void clearIntroBlocks() {
		this.introBlocks.clear();
	}

	public boolean setIntroBlocks(List<AbstractParagraphBlock<?>> blocks) {
		ArrayList<AbstractParagraphBlock<?>> newBlocks = new ArrayList<>();
		for (AbstractParagraphBlock<?> block : blocks) {
			if (block != null) {
				newBlocks.add(block);
			}
		}

		if (!newBlocks.isEmpty()) {
			clearBlocks();
			this.introBlocks.addAll(newBlocks);
		}

		return !newBlocks.isEmpty();
	}

}
