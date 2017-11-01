package com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock.AbstractItem;

public class ItemCreationException extends Exception {

	private static final long serialVersionUID = -1133646512099318158L;

	protected static final String UNEXPECTED = "Unexpected exception";

	private final AbstractItem<?> item;
	private final Object value;

	public <T> ItemCreationException(final AbstractItem<T> abstractItem, final T value) {
		this(abstractItem, value, abstractItem.isValidValue(value) ? UNEXPECTED : "Illegal value for block item");
	}

	protected <T> ItemCreationException(final AbstractItem<T> abstractItem, final T value, String errMsg) {
		super(((errMsg == null) || errMsg.isEmpty()) ? UNEXPECTED : errMsg);
		this.item = abstractItem;
		this.value = value;
	}

	public AbstractItem<?> getItem() {
		return this.item;
	}

	public Object getValue() {
		return this.value;
	}

}
