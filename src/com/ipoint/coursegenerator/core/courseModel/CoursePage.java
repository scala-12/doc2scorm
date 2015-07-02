package com.ipoint.coursegenerator.core.courseModel;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.items.AbstractTextItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.items.ImageOnlyItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.items.ListItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.items.ParagraphItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.items.TableCellItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.items.TableItem;
import com.ipoint.coursegenerator.core.utils.ImageInfo;

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
	
	public List<ImageInfo> getImages() {
		ArrayList<ImageInfo> imagesInfo = new ArrayList<ImageInfo>();
		this.recursiveSearchImages(this.getAllBlocks(), imagesInfo);
		return imagesInfo;
	}
	
	private void searchInParagraph(ParagraphBlock block, ArrayList<ImageInfo> imagesInfo) {
		for (ParagraphItem parItem : block.getItems()) {
			for (AbstractTextItem item : parItem.getValue().getItems()) {
				if (item instanceof ImageOnlyItem) {
					ImageOnlyItem imageItem = (ImageOnlyItem) item; 
					imagesInfo.add(new ImageInfo(imageItem.getImageFullName(), imageItem.getValue()));
				}
			}
		}
	}
	
	private void recursiveSearchImages(List<AbstractParagraphBlock> blocks, ArrayList<ImageInfo> imagesInfo) {
		for (AbstractParagraphBlock block : blocks) {
			if (block instanceof ParagraphBlock) {
				this.searchInParagraph((ParagraphBlock)block, imagesInfo);
			} else if (block instanceof ListBlock) {
				for (ListItem listItem : ((ListBlock) block).getItems()) {
					this.searchInParagraph(listItem.getValue(), imagesInfo);
				}
			} else if (block instanceof TableBlock) {
				for (TableItem row : ((TableBlock) block).getItems()) {
					for (TableCellItem cell : row.getValue()) {
						if (cell.getValue() != null) {
							this.recursiveSearchImages(cell.getValue(), imagesInfo);
						}
					}
				}
			}
		}		
	}

}
