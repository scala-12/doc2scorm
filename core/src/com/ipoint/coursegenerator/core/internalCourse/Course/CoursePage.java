package com.ipoint.coursegenerator.core.internalCourse.Course;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.Convertable;
import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ParagraphBlock;

public class CoursePage implements Convertable {

	private ArrayList<ParagraphBlock> paragraphList;

	public CoursePage() {
		this.paragraphList = new ArrayList<ParagraphBlock>();
	}

	public AbstractBlock getBlock(Integer i) {
		return this.paragraphList.get(i);
	}

	public List<ParagraphBlock> getAllBlocks() {
		return this.paragraphList;
	}

	public void addParagraph(ParagraphBlock block) {
		this.paragraphList.add(block);
	}

	@Override
	public Node toHtml() {
		// TODO Auto-generated method stub

		return null;
	}

}
