package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;

public class ListItem extends AbstractItem {

	private ArrayList<AbstractBlock> text;

	@Override
	public List<AbstractBlock> getValue() {
		return this.text;
	}

	@Override
	public void setValue(Object textBlockList) {
		this.text = (ArrayList<AbstractBlock>) textBlockList;
	}

}
