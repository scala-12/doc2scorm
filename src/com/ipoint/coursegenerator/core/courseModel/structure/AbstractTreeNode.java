package com.ipoint.coursegenerator.core.courseModel.structure;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.structure.exceptions.ModelTreeNodeCreationException;

/**
 * Tree as list of nodes that may includes several {@link ModelTreeNode}
 *
 *
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractTreeNode {

	private ArrayList<ModelTreeNode> childs;
	AbstractTreeNode parent;

	public AbstractTreeNode() {
		this.childs = new ArrayList<>();
		this.parent = null;
	}

	public AbstractTreeNode createChild(String title) throws ModelTreeNodeCreationException {
		ModelTreeNode child = new ModelTreeNode(title);
		this.childs.add(child);
		child.parent = this;

		return child;
	}

	public AbstractTreeNode createAfter(String title) throws ModelTreeNodeCreationException {
		ModelTreeNode child = new ModelTreeNode(title);
		this.parent.childs.add(child);
		child.parent = this.parent;

		return child;
	}

	public boolean removeChild(ModelTreeNode node) {
		if (this.childs.remove(node)) {
			node.parent = null;

			return true;
		}

		return false;
	}

	public AbstractTreeNode getParent() {
		return this.parent;
	}

	public ModelTreeNode getChild(int index) {
		return ((this.childs.size() <= index) || this.childs.isEmpty()) ? null : this.childs.get(index);
	}

	public List<ModelTreeNode> getChilds() {
		return new ArrayList<>(this.childs);
	}

}
