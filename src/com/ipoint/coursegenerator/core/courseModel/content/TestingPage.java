package com.ipoint.coursegenerator.core.courseModel.content;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionBlock;
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
public class TestingPage extends AbstractPage<AbstractQuestionBlock<?>> {

	private final ArrayList<AbstractParagraphBlock<?>> introBlocks;
	private BigDecimal maxTimeAllowed;
	private int percents4markA = 90;
	private int percents4markB = 80;
	private int percents4markC = 60;

	private TestingPage(CourseTreeNode parentNode) {
		super(parentNode);
		this.introBlocks = new ArrayList<>();
		maxTimeAllowed = null;
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

	@Override
	public Set<PictureInfo> getImages() {
		HashSet<PictureInfo> images = new HashSet<>(super.getImages());
		images.addAll(getImagesRecursive(this.getIntroBlocks()));

		return images;
	}

	public String getLaunchData() {
		StringBuilder launchDate = new StringBuilder();
		launchDate.append("3|1-").append(this.getBlocks().size());

		for (int i = 0; i < this.getBlocks().size(); i++) {
			AbstractQuestionBlock<?> block = this.getBlocks().get(i);
			launchDate.append('\n').append(i + 1).append("|").append(String.join("~", block.getCorrect()));
		}

		return launchDate.toString();
	}

	public BigDecimal getMaxTimeAllowed() {
		return this.maxTimeAllowed;
	}

	public boolean setMaxTimeAllowed(BigDecimal maxTimeAllowed) {
		if (maxTimeAllowed.compareTo(Tools.BIG_ZERO) == 1) {
			this.maxTimeAllowed = maxTimeAllowed;

			return true;
		} else {
			return false;
		}
	}

	public int getPercents4markA() {
		return this.percents4markA;
	}

	public int getPercents4markB() {
		return this.percents4markB;
	}

	public int getPercents4markC() {
		return this.percents4markC;
	}

	public boolean setPercents4markA(int percents) {
		if ((percents <= 100) && (percents > this.percents4markB)) {
			this.percents4markA = percents;

			return true;
		} else {

			return false;
		}
	}

	public boolean setPercents4markB(int percents) {
		if ((percents < this.percents4markA) && (percents > this.percents4markC)) {
			this.percents4markB = percents;

			return true;
		} else {

			return false;
		}
	}

	public boolean setPercents4markC(int percents) {
		if ((percents < this.percents4markC) && (percents > 0)) {
			this.percents4markC = percents;

			return true;
		} else {

			return false;
		}
	}
}
