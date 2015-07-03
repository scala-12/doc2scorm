package com.ipoint.coursegenerator.core.courseModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Interface for converting to html-code
 * 
 * @author Kalashnikov Vladislav
 *
 */
public interface Convertable {

	/**
	 * Conversion to html
	 * 
	 * @param creatorTags
	 *            html-document
	 * @return html element
	 */
	public Element toHtml(Document creatorTags);

}
