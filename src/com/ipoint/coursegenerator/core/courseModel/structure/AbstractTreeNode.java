package com.ipoint.coursegenerator.core.courseModel.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;
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
	private final String id;

	public AbstractTreeNode(String title) throws SimpleModelNodeCreationException {
		if (!this.setTitle(title)) {
			throw new SimpleModelNodeCreationException(title);
		}
		this.childs = new ArrayList<>();
		this.parent = null;
		this.id = UUID.randomUUID().toString();
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

	abstract protected String getNodeType();

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

		return "{ \"title\": \"" + this.title + "\", \"type\": \"" + this.getNodeType() + "\""
				+ ((hasOne) ? ", \"childs\": [" + childs.toString() + "]" : "") + "}";
	}

	public Map<String, String> getHierarchyInfo() {
		Map<String, String> hierarchy = new HashMap<>();

		hierarchy.put(
				new GsonBuilder().create()
						.toJson(Arrays.stream(new String[][] { { "type", this.getNodeType() }, { "title", this.title },
								{ "id", this.id } }).collect(Collectors.toMap(e -> e[0], e -> e[1]))),
				(this.parent == null) ? "" : this.parent.id);

		for (ModelTreeNode node : this.childs) {
			hierarchy.putAll(node.getHierarchyInfo());
		}

		return hierarchy;
	}

}
