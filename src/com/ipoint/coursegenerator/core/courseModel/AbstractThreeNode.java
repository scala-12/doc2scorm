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
public abstract class AbstractThreeNode {

	private ArrayList<CourseTreeNode> childs;

	public AbstractThreeNode() {
		this.childs = new ArrayList<>();
	}

	/**
	 * Method for adding node in tree on this level
	 * 
	 * @param node
	 *            Node for adding. If it is null then return false
	 * @return If successful then true
	 */
	public boolean addChild(CourseTreeNode node) {
		if (node == null) {
			return false;
		} else {
			this.childs.add(node);
			return true;
		}
	}

	public CourseTreeNode getChild(int index) {
		return ((this.childs.size() <= index) || this.childs.isEmpty()) ? null
				: this.childs.get(index);
	}

	public List<CourseTreeNode> getChilds() {
		return this.childs;
	}

}
