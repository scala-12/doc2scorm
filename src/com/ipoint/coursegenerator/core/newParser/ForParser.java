package com.ipoint.coursegenerator.core.newParser;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;

/**
 * Methods for parsing
 * 
 * @author Kalashnikov Vladislav
 *
 */
public interface ForParser {

	public AbstractBlock parseDoc();

	/**
	 * Parsing docx-elements
	 * 
	 * @param element
	 *            element docx
	 * @return Parsed element
	 */
	public Object parseDocx(Object element);

}
