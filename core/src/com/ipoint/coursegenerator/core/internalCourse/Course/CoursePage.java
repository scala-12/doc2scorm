package com.ipoint.coursegenerator.core.internalCourse.Course;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.internalCourse.Convertable;
import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ListBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ParagraphBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TableBlock;
import com.ipoint.coursegenerator.core.internalCourse.items.AbstractTextItem;
import com.ipoint.coursegenerator.core.internalCourse.items.ImageOnlyItem;
import com.ipoint.coursegenerator.core.internalCourse.items.ListItem;
import com.ipoint.coursegenerator.core.internalCourse.items.ParagraphItem;
import com.ipoint.coursegenerator.core.internalCourse.items.TableCellItem;
import com.ipoint.coursegenerator.core.internalCourse.items.TableItem;

/**
 * Page of document
 * 
 * @see Course
 * @author Kalashnikov Vladislav
 *
 */
public class CoursePage implements Convertable {

	/**
	 * Paragraph of document
	 */
	private ArrayList<AbstractParagraphBlock> paragraphs;

	/**
	 * Create empty page
	 */
	public CoursePage() {
		this.paragraphs = new ArrayList<AbstractParagraphBlock>();
	}

	/**
	 * Returns the paragraph at the specified position in this page
	 * 
	 * @param index
	 *            index of the element to return
	 * @return the paragraph at the specified position in this page
	 */
	public AbstractBlock getBlock(Integer index) {
		return ((this.paragraphs.size() <= index) || this.paragraphs.isEmpty()) ? null
				: this.paragraphs.get(index);
	}

	/**
	 * Returns all paragraph of document
	 * 
	 * @return All paragraph of document
	 */
	public List<AbstractParagraphBlock> getAllBlocks() {
		return this.paragraphs;
	}

	/**
	 * Appends the paragraph to the page
	 * 
	 * @param block
	 *            Block for adding
	 */
	public void addBlock(AbstractParagraphBlock block) {
		this.paragraphs.add(block);
	}

	@Override
	public Element toHtml(Document creatorTags) {
		Element divAsPage = creatorTags.createElement("div");
		for (AbstractParagraphBlock par : this.getAllBlocks()) {
			divAsPage.appendChild(par.toHtml(creatorTags));
		}
		
		return divAsPage;
	}
	
	public List<String> getImagesNames() {
		ArrayList<String> imagesNames = new ArrayList<String>();
		this.recursiveAddImages(this.getAllBlocks(), imagesNames);
		return imagesNames;
	}
	
	private void workWithParagraph(ParagraphBlock block, ArrayList<String> imagesNames) {
		for (ParagraphItem parItem : block.getItems()) {
			for (AbstractTextItem item : parItem.getValue().getItems()) {
				if (item instanceof ImageOnlyItem) {
					imagesNames.add(((ImageOnlyItem) item).getImageFullName());
				}
			}
		}
	}
	
	private void recursiveAddImages(List<AbstractParagraphBlock> blocks, ArrayList<String> imagesNames) {
		for (AbstractParagraphBlock block : blocks) {
			if (block instanceof ParagraphBlock) {
				this.workWithParagraph((ParagraphBlock)block, imagesNames);
			} else if (block instanceof ListBlock) {
				for (ListItem listItem : ((ListBlock) block).getItems()) {
					this.workWithParagraph(listItem.getValue(), imagesNames);
				}
			} else if (block instanceof TableBlock) {
				for (TableItem row : ((TableBlock) block).getItems()) {
					for (TableCellItem cell : row.getValue()) {
						if (cell.getValue() != null) {
							this.recursiveAddImages(cell.getValue(), imagesNames);
						}
					}
				}
			}
		}		
	}

}
