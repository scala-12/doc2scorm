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
 * Page. These includes {@link AbstractParagraphBlock}
 * 
 * @see CourseModel
 * @author Kalashnikov Vladislav
 *
 */
public class CoursePage implements Convertable {

	public final static String CONTENT_DIV_ID = "content_div";

	private ArrayList<AbstractParagraphBlock> blocks;

	private CourseTreeNode parentNode;

	public CoursePage() {
		this.blocks = new ArrayList<AbstractParagraphBlock>();
		this.parentNode = null;
	}

	public AbstractBlock getBlock(int index) {
		return ((this.blocks.size() <= index) || this.blocks.isEmpty()) ? null
				: this.blocks.get(index);
	}

	public List<AbstractParagraphBlock> getBlocks() {
		return this.blocks;
	}

	/**
	 * Method for add block on page
	 * 
	 * @param block
	 *            Block for adding. If it is null then return false
	 * @return If successful then true
	 */
	public boolean addBlock(AbstractParagraphBlock block) {
		if (block == null) {
			return false;
		} else {
			this.blocks.add(block);
			return true;
		}
	}

	/**
	 * Set parent of page
	 * 
	 * @param node
	 *            Is parent of page. If there is null then return false
	 * @return If successful then true
	 */
	public void setParent(CourseTreeNode node) {
		if (node != this.parentNode) {
			// because class CourseTreeNode have call of this method
			// method that below need for set link between page and node
			if (this.parentNode != null) {
				this.parentNode.setPage(null);
			}
			this.parentNode = node;
			node.setPage(this);
		}
	}

	public CourseTreeNode getParent() {
		return this.parentNode;
	}

	/**
	 * @return html-element div
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element pageBody = creatorTags.createElement("div");
		pageBody.setAttribute("id", CONTENT_DIV_ID);

		if (this.parentNode != null) {
			Element pageHeader = creatorTags.createElement("h1");
			pageHeader.setTextContent(this.parentNode.getTitle());
			pageBody.appendChild(pageHeader);
		}

		for (AbstractParagraphBlock par : this.getBlocks()) {
			pageBody.appendChild(par.toHtml(creatorTags));
		}

		return pageBody;
	}

	public List<ImageInfo> getImages() {
		return this.getImagesRecursive(this.getBlocks());
	}

	private List<ImageInfo> getImagesOfParagraph(ParagraphBlock paragraph) {
		ArrayList<ImageInfo> images = new ArrayList<ImageInfo>();

		for (ParagraphItem parItem : paragraph.getItems()) {
			for (AbstractTextItem item : parItem.getValue().getItems()) {
				if (item instanceof ImageOnlyItem) {
					ImageOnlyItem imageItem = (ImageOnlyItem) item;
					images.add(new ImageInfo(imageItem.getImageFullName(),
							imageItem.getValue()));
				}
			}
		}

		return images;
	}

	private List<ImageInfo> getImagesRecursive(
			List<AbstractParagraphBlock> blocks) {
		ArrayList<ImageInfo> images = new ArrayList<ImageInfo>();

		for (AbstractParagraphBlock block : blocks) {
			if (block instanceof ParagraphBlock) {
				images.addAll(this.getImagesOfParagraph((ParagraphBlock) block));

			} else if (block instanceof ListBlock) {
				for (ListItem listItem : ((ListBlock) block).getItems()) {
					images.addAll(this.getImagesOfParagraph(listItem.getValue()));
				}

			} else if (block instanceof TableBlock) {
				for (TableItem row : ((TableBlock) block).getItems()) {
					for (TableCellItem cell : row.getValue()) {
						if (cell.getValue() != null) {
							images.addAll(this.getImagesRecursive(cell
									.getValue()));
						}
					}
				}
			}
		}

		return images;
	}

}
