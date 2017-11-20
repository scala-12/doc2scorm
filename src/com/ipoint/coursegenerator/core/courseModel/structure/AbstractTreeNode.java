package com.ipoint.coursegenerator.core.courseModel.structure;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.structure.exceptions.TreeNodeCreationException;

/**
 * Tree as list of nodes that may includes several {@link CourseTreeNode}
 *
 *
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractTreeNode {

	private ArrayList<CourseTreeNode> childs;
	AbstractTreeNode parent;

	public AbstractTreeNode() {
		this.childs = new ArrayList<>();
		this.parent = null;
	}

	public AbstractTreeNode createChild(String title) throws TreeNodeCreationException {
		CourseTreeNode child = new CourseTreeNode(title);
		this.childs.add(child);
		child.parent = this;

		return child;
	}

	public AbstractTreeNode createAfter(String title) throws TreeNodeCreationException {
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

	public AbstractTreeNode getParent() {
		return this.parent;
	}

	public CourseTreeNode getChild(int index) {
		return ((this.childs.size() <= index) || this.childs.isEmpty()) ? null : this.childs.get(index);
	}

	public List<CourseTreeNode> getChilds() {
		return new ArrayList<>(this.childs);
	}

}
