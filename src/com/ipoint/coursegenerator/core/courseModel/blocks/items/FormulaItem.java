package com.ipoint.coursegenerator.core.courseModel.blocks.items;

import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FormulaItem extends AbstractTextItem {

	private CTOMath value;

	/**
	 * Create oMath as Block Item
	 * 
	 * @param oMath
	 *            Formula of block. There cannot be null
	 */
	public FormulaItem(CTOMath formula) {
		if (!this.setValue(formula)) {
			// TODO:exception
		}
	}

	public CTOMath getValue() {
		return value;
	}

	/**
	 * Set value of item
	 * 
	 * @param oMath
	 *            formula of block
	 * @return If successful then true
	 */
	public boolean setValue(CTOMath oMath) {
		if (oMath == null) {
			return false;
		} else {
			this.value = oMath;
			return true;
		}
	}

	@Override
	public Element toHtml(Document creatorTags) {
		// TODO Auto-generated method stub
		return null;
	}
}
