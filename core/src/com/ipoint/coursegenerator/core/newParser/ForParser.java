package com.ipoint.coursegenerator.core.newParser;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;

public interface ForParser {

	public AbstractBlock parseDoc();

	public Object parseDocx(Object element);

}
