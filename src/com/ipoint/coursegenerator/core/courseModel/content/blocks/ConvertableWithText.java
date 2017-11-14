package com.ipoint.coursegenerator.core.courseModel.content.blocks;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public interface ConvertableWithText {

	/** @return simple nodes */
	abstract public NodeList toSimpleHtml(Document creatorTags);

	abstract public String getText();

}
