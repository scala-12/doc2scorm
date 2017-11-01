package com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock.AbstractItem;

public class BlockCreationException extends Exception {

	private static final long serialVersionUID = -1133646512099318158L;

	protected static final String UNEXPECTED = "Unexpected exception";

	private final AbstractBlock<?> block;
	private final List<? extends AbstractItem<?>> items;
	private final int nullCount;

	public BlockCreationException(final AbstractBlock<?> block, final List<? extends AbstractItem<?>> items) {
		this(block, items, ((items == null) ? "List items pointer is null"
				: (items.isEmpty()) ? "List items is empty" : UNEXPECTED));
	}

	private BlockCreationException(final AbstractBlock<?> block, final List<? extends AbstractItem<?>> items,
			String errMsg, Integer nullCount) {
		super(((errMsg == null) || errMsg.isEmpty()) ? UNEXPECTED : errMsg);
		this.block = block;
		this.items = items;
		this.nullCount = nullCount;
	}

	protected BlockCreationException(final AbstractBlock<?> block, final List<? extends AbstractItem<?>> items,
			String errMsg) {
		this(block, items, errMsg, 0);
	}

	public BlockCreationException(final AbstractBlock<?> block, final List<? extends AbstractItem<?>> items,
			int nullCount) {
		this(block, items, (nullCount == 0) ? UNEXPECTED : "Has null items in list", nullCount);
	}

	public AbstractBlock<?> getBlock() {
		return this.block;
	}

	public List<? extends AbstractItem<?>> getItems() {
		return this.items;
	}

	public int getNullCount() {
		return this.nullCount;
	}

}
