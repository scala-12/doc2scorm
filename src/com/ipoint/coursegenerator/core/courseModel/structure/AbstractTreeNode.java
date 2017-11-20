package com.ipoint.coursegenerator.core.courseModel.structure;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.structure.exceptions.SimpleModelNodeCreationException;

/**
 * Tree as list of nodes that may includes several {@link ModelTreeNode}
 *
 *
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractTreeNode {

	private ArrayList<ModelTreeNode> childs;
	protected String title;
	AbstractTreeNode parent;

	public AbstractTreeNode(String title) throws SimpleModelNodeCreationException {
		this.childs = new ArrayList<>();
		this.parent = null;
		if (!this.setTitle(title)) {
			throw new SimpleModelNodeCreationException(title);
		}
	}

	public String getTitle() {
		return this.title;
	}

	public boolean setTitle(String title) {
		if (this.isCorrectTitle(title)) {
			this.title = title;

			return true;
		}

		return false;
	}

	protected boolean isCorrectTitle(String title) {
		return (title != null) && !title.isEmpty();
	}

	public AbstractTreeNode createChild(String title) throws SimpleModelNodeCreationException {
		ModelTreeNode child = new ModelTreeNode(title);
		this.childs.add(child);
		child.parent = this;

		return child;
	}

	public AbstractTreeNode createAfter(String title) throws SimpleModelNodeCreationException {
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

	protected String getInfoAsJsonContext() {
		return "'title': " + this.title;
	}

	@Override
	public String toString() {
		StringBuilder childs = new StringBuilder();

		boolean hasOne = false;
		for (ModelTreeNode node : this.childs) {
			if (hasOne) {
				childs.append(", ");
			} else {
				hasOne = true;
			}
			childs.append(node.toString());
		}

		return "{" + this.getInfoAsJsonContext() + ((hasOne) ? ", 'childs': [" + childs.toString() + "]" : "") + "}";
	}

}
