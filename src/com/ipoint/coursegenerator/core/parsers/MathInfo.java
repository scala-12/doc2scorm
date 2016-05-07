package com.ipoint.coursegenerator.core.parsers;

import java.util.List;

import org.w3c.dom.Node;

/**
 * Class for read MathML formuls
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class MathInfo {

	private List<Node> mathMLFormulList;

	private int reedFormulCount;

	public MathInfo(List<Node> mathMLFormuls) {
		this.mathMLFormulList = mathMLFormuls;
		this.reset();
	}

	/**
	 * Read the next formula of document
	 * 
	 * @return Reed formul
	 */
	public Node read() {
		return this.mathMLFormulList.get(this.reedFormulCount++);
	}

	/**
	 * Returns true if all formulas read
	 * 
	 * @return true if all formulas read
	 */
	public boolean isReed() {
		return mathMLFormulList.size() == reedFormulCount;
	}

	/**
	 * Return to fist formula
	 */
	public void reset() {
		this.reedFormulCount = 0;
	}

}
