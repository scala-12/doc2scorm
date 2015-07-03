package com.ipoint.coursegenerator.core.courseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree as list of nodes that may includes several {@link CourseTreeNode}
 * 
 * 
 * @author Kalashnikov Vladislav
 *
 */
public abstract class CourseTree {

	private ArrayList<CourseTreeNode> nodes;

	public CourseTree() {
		this(new ArrayList<CourseTreeNode>());
	}

	protected CourseTree(ArrayList<CourseTreeNode> courseTree) {
		this.nodes = courseTree;
	}

	/**
	 * Method for adding node in tree on this level
	 * 
	 * @param node
	 *            Node for adding. If it is null then return false
	 * @return If successful then true
	 */
	public boolean addNode(CourseTreeNode node) {
		if (node == null) {
			return false;
		} else {
			this.nodes.add(node);
			return true;
		}
	}

	public CourseTreeNode getNode(int index) {
		return ((this.nodes.size() <= index) || this.nodes.isEmpty()) ? null
				: this.nodes.get(index);
	}

	public List<CourseTreeNode> getNodes() {
		return this.nodes;
	}

}
