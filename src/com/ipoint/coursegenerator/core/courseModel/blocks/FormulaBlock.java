package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.FormulaItem;

/**
 * Block which includes formula
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FormulaBlock extends AbstractTextualParagraphBlock {

	public FormulaBlock(FormulaItem item) {
		super(toList(item));
	}

	private static ArrayList<FormulaItem> toList(FormulaItem item) {
		ArrayList<FormulaItem> items = new ArrayList<FormulaItem>();
		items.add(item);
		return items;
	}

	@Override
	public Element toHtml(Document creatorTags) {
		// TODO Auto-generated method stub
		return null;
	}
}
