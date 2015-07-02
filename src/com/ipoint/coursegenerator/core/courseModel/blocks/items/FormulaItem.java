package com.ipoint.coursegenerator.core.courseModel.blocks.items;

import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;

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
}
