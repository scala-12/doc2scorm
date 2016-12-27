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
	protected AbstractThreeNode parent;

	public AbstractThreeNode() {
		this.childs = new ArrayList<>();
		this.parent = null;
	}

	public AbstractThreeNode createChild(String title) {
		CourseTreeNode child = new CourseTreeNode(title);
		this.childs.add(child);
		child.parent = this;

		return child;
	}

	public AbstractThreeNode createAfter(String title) {
		CourseTreeNode child = new CourseTreeNode(title);
		this.parent.childs.add(child);
		child.parent = this.parent;

		return child;
	}

	public boolean removeChild(CourseTreeNode node) {
		if (this.childs.remove(node)) {
			node.parent = null;

			return true;
		}

		return false;
	}

	public AbstractThreeNode getParent() {
		return this.parent;
	}

	public CourseTreeNode getChild(int index) {
		return ((this.childs.size() <= index) || this.childs.isEmpty()) ? null
				: this.childs.get(index);
	}

	public List<CourseTreeNode> getChilds() {
		return new ArrayList<>(this.childs);
	}

}
