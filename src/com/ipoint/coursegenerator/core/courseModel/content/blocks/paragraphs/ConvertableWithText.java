package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface ConvertableWithText {

	abstract public Element toHtmlWithoutStyles(Document creatorTags);

	abstract public String getText();

}
