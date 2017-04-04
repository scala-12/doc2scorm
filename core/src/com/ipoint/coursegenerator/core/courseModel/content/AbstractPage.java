package com.ipoint.coursegenerator.core.courseModel.content;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.Convertable;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock;
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
public abstract class AbstractPage<T extends AbstractBlock<?>> implements Convertable {

	private final ArrayList<T> blocks;

	private CourseTreeNode parentNode;

	public final static String CONTENT_DIV_ID = "page_content";

	protected AbstractPage(CourseTreeNode parentNode) {
		this.blocks = new ArrayList<>();
		this.setParent(parentNode);
	}

	public List<T> getBlocks() {
		return new ArrayList<>(this.blocks);
	}

	/** @return true if replaced */
	public boolean setBlocks(List<T> blocks) {
		ArrayList<T> newBlocks = new ArrayList<>();
		for (T block : blocks) {
			if (block != null) {
				newBlocks.add(block);
			}
		}

		if (!newBlocks.isEmpty()) {
			clearBlocks();
			this.blocks.addAll(newBlocks);
		}

		return !newBlocks.isEmpty();
	}

	public void clearBlocks() {
		this.blocks.clear();
	}

	public void setParent(CourseTreeNode node) {
		if (node != this.parentNode) {
			// because class CourseTreeNode have call of this method
			// method that below need for set link between page and node
			if (this.parentNode != null) {
				this.parentNode.setPage(null);
			}
			this.parentNode = node;
			node.setPage(this);
		}
	}

	public CourseTreeNode getParent() {
		return this.parentNode;
	}

	/**
	 * @return html-element div
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element pageBody = creatorTags.createElement("div");
		pageBody.setAttribute("id", CONTENT_DIV_ID);

		if (this.getParent() != null) {
			Element pageHeader = creatorTags.createElement("h1");
			pageHeader.setTextContent(this.getParent().getTitle());
			pageBody.appendChild(pageHeader);
		}

		for (T par : this.getBlocks()) {
			pageBody.appendChild(par.toHtml(creatorTags));
		}

		return pageBody;
	}

}
