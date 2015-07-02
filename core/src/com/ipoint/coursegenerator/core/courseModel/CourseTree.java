package com.ipoint.coursegenerator.core.courseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree of document
 * 
 * @author Kalashnikov Vladislav
 *
 */
public abstract class CourseTree {

	/**
	 * Tree of document
	 */
	private ArrayList<CourseTreeItem> documenTree;

	/**
	 * Create empty Tree
	 */
	public CourseTree() {
		this.documenTree = new ArrayList<CourseTreeItem>();
	}

	/**
	 * Appends the node to the Tree
	 * 
	 * @param node
	 *            Node for adding
	 */
	public void addItem(CourseTreeItem node) {
		this.documenTree.add(node);
	}

	/**
	 * Returns the node at the specified position in this Tree
	 * 
	 * @param index
	 *            index of the element to return
	 * @return the node at the specified position in this Tree
	 */
	public CourseTreeItem getItem(int index) {
		return ((this.documenTree.size() <= index) || this.documenTree
				.isEmpty()) ? null : this.documenTree.get(index);
	}

	/**
	 * Returns all nodes of Tree
	 * 
	 * @return All nodes of Tree
	 */
	public List<CourseTreeItem> getCourseTree() {
		return this.documenTree;
	}

}
