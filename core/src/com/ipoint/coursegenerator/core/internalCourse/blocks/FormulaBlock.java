package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.internalCourse.items.FormulaItem;

public class FormulaBlock extends AbstractParagraphBlock {

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
