package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.internalCourse.Convertable;
import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;

//This is an analogue of paragraph
public abstract class AbstractBlock implements Convertable {

	private ArrayList<AbstractItem> items;

	public AbstractBlock(List<AbstractItem> items) {
		this.items = (items == null) ? new ArrayList<AbstractItem>()
				: (ArrayList<AbstractItem>) items;
	}
	
	public AbstractBlock(AbstractItem item) {
		this();
		if (item != null) {
			this.items.add(item);
		}
	}

	public AbstractBlock() {
		this(new ArrayList<AbstractItem>());
	}

	public List<AbstractItem> getItems() {
		return this.items;
	}

	public void addItem(AbstractItem element) {
		this.items.add(element);
	}

	public AbstractItem getItem(int i) {
		return this.items.get(i);
	}

}
