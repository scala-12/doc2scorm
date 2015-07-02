package com.ipoint.coursegenerator.core.courseModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface Convertable {

	public Element toHtml(Document creatorTags);

}
