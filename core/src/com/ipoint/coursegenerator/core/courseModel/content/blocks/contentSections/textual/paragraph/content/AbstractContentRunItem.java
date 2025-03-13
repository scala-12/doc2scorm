package com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content;

import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.runs.FormulaRunItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.runs.ImageRunItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.runs.TextRunItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;

/**
 * Item that may be {@link TextRunItem}, {@link FormulaRunItem} or
 * {@link ImageRunItem}
 * 
 * @author Kalashnikov Vladislav
 * 
 * @param <T>
 *            Type of item that included in block
 *
 */
public abstract class AbstractContentRunItem<T> extends AbstractSectionItem<T> {

	protected AbstractContentRunItem(XWPFRun run, T value) throws ItemCreationException {
		super(value);
	}

}
