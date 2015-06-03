package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.ArrayList;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;

public class ListBlock extends AbstractBlock {

	private Integer level;

	public ListBlock() {
		this(0);
	}

	public ListBlock(int lvl, ArrayList<AbstractItem> itemList) {
		super(itemList);
		this.level = lvl;
	}

	public ListBlock(int lvl) {
		this(lvl, null);
	}

	public void setLevel(int i) {
		this.level = i;
	}

	public int getLevel() {
		return this.level;
	}

	@Override
	public Node toHtml() {
		// TODO Auto-generated method stub
		return null;
	}

}
