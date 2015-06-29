package com.ipoint.coursegenerator.core.internalCourse.Course;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.internalCourse.Convertable;
import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractParagraphBlock;

/**
 * Page of document
 * 
 * @see Course
 * @author Kalashnikov Vladislav
 *
 */
public class CoursePage implements Convertable {

	/**
	 * Paragraph of document
	 */
	private ArrayList<AbstractParagraphBlock> paragraphs;

	/**
	 * Create empty page
	 */
	public CoursePage() {
		this.paragraphs = new ArrayList<AbstractParagraphBlock>();
	}

	/**
	 * Returns the paragraph at the specified position in this page
	 * 
	 * @param index
	 *            index of the element to return
	 * @return the paragraph at the specified position in this page
	 */
	public AbstractBlock getBlock(Integer index) {
		return ((this.paragraphs.size() <= index) || this.paragraphs.isEmpty()) ? null
				: this.paragraphs.get(index);
	}

	/**
	 * Returns all paragraph of document
	 * 
	 * @return All paragraph of document
	 */
	public List<AbstractParagraphBlock> getAllBlocks() {
		return this.paragraphs;
	}

	/**
	 * Appends the paragraph to the page
	 * 
	 * @param block
	 *            Block for adding
	 */
	public void addBlock(AbstractParagraphBlock block) {
		this.paragraphs.add(block);
	}

	@Override
	public Element toHtml(Document creatorTags) {
		Element pageBody = creatorTags.createElement("body");
		for (AbstractParagraphBlock par : this.getAllBlocks()) {
			pageBody.appendChild(par.toHtml(creatorTags));
		}
		
		return pageBody;
	}

}
