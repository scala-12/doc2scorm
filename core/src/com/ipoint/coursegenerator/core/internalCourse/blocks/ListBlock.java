package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.List;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;
import com.ipoint.coursegenerator.core.internalCourse.items.ListItem;

/**
 * List block which may includes several other blocks ({@link TextBlock},
 * {@link HyperlinkBlock}) as one item. This block is an extends of
 * {@link AbstractBlock}
 * 
 * @see AbstractBlock
 * @author Kalashnikov Vladislav
 *
 */
public class ListBlock extends AbstractBlock {

	/**
	 * Level of list. Start from zero as first level and may not be null.
	 */
	private int level;

	/**
	 * Type list marker
	 */
	private String type;

	/**
	 * @see AbstractBlock#AbstractBlock(List)
	 */
	public ListBlock(List<AbstractItem> items) {
		super(items);
		this.setLevel(level);
		this.setMarkerType(null);
	}

	/**
	 * @see AbstractBlock#AbstractBlock(AbstractItem)
	 */
	public ListBlock(AbstractItem item) {
		super(item);
		this.setLevel(level);
		this.setMarkerType(null);
	}

	/**
	 * Changing list marker type
	 * 
	 * @param type
	 *            Type of list
	 */
	public void setMarkerType(String type) {
		String resultVariable = null;
		
		if (type  != null) {
			if (type.equalsIgnoreCase("upperLetter")) {
				resultVariable = "A";
			} else if (type.equalsIgnoreCase("lowerLetter")) {
				resultVariable = "a";
			} else if (type.equalsIgnoreCase("upperRoman")) {
				resultVariable = "I";
			} else if (type.equalsIgnoreCase("lowerRoman")) {
				resultVariable = "i";
			} else if (type.equalsIgnoreCase("decimal")) {
				resultVariable = "1";
			}
		}
		
		this.type = resultVariable;
	}

	/**
	 * Returns type of list marker
	 * 
	 * @return type of list marker
	 */
	public String getMarkerType() {
		return this.type;
	}

	/**
	 * Changing list level
	 * 
	 * @param level
	 *            Level of list. If level equal to null, then level of list will
	 *            be set 0
	 */
	public void setLevel(Integer level) {
		this.level = (level == null) ? 0 : (level >= 0) ? level : 0;
	}

	/**
	 * Returns level of list
	 * 
	 * @return Level of list
	 */
	public int getLevel() {
		return this.level;
	}

	@Override
	public boolean isRightItem(AbstractItem item) {
		if (item.getClass().equals(ListItem.class)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Node toHtml() {
		// TODO Auto-generated method stub
		return null;
	}

}
