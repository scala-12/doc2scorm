package com.ipoint.coursegenerator.core.courseModel;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.tabular.TableItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.tabular.cell.CellBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.AbstractTextualBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.list.ListItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.ParagraphItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.AbstractContentItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.items.ImageContentItem;

/**
 * Page. These includes {@link AbstractParagraphBlock}
 *
 * @see CourseModel
 * @author Kalashnikov Vladislav
 *
 */
public class CoursePage implements Convertable {

	public final static String CONTENT_DIV_ID = "content_div";

	private ArrayList<AbstractParagraphBlock<?>> blocks;

	private CourseTreeNode parentNode;

	private CoursePage(CourseTreeNode parentNode) {
		this.blocks = new ArrayList<>();
		this.setParent(parentNode);
	}

	public static CoursePage createEmptyPage(CourseTreeNode parentNode) {
		return new CoursePage(parentNode);
	}

	public AbstractBlock<?> getBlock(int index) {
		return ((this.blocks.size() <= index) || this.blocks.isEmpty()) ? null : this.blocks.get(index);
	}

	public List<AbstractParagraphBlock<?>> getBlocks() {
		return this.blocks;
	}

	/**
	 * Method for add block on page
	 *
	 * @param block
	 *            Block for adding. If it is null then return false
	 * @return If successful then true
	 */
	public boolean addBlock(AbstractParagraphBlock<?> block) {
		if (block == null) {
			return false;
		} else {
			this.blocks.add(block);
			return true;
		}
	}

	public boolean addBlocks(List<AbstractParagraphBlock<?>> blocks) {
		return this.blocks.addAll(blocks);
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

		for (AbstractParagraphBlock<?> par : this.getBlocks()) {
			pageBody.appendChild(par.toHtml(creatorTags));
		}

		return pageBody;
	}

	public List<PictureInfo> getImages() {
		return this.getImagesRecursive(this.getBlocks());
	}

	private List<PictureInfo> getImagesOfParagraph(ParagraphBlock paragraph) {
		ArrayList<PictureInfo> images = new ArrayList<PictureInfo>();

		for (ParagraphItem parItem : paragraph.getItems()) {
			for (AbstractContentItem<?> item : parItem.getValue().getItems()) {
				if (item instanceof ImageContentItem) {
					ImageContentItem imageItem = (ImageContentItem) item;
					PictureInfo image = new PictureInfo(imageItem.getImageFullName(), imageItem.getValue());
					images.add(image);
				}
			}
		}

		return images;
	}

	private List<PictureInfo> getImagesRecursive(List<AbstractParagraphBlock<?>> blocks) {
		ArrayList<PictureInfo> images = new ArrayList<PictureInfo>();

		for (AbstractParagraphBlock<?> block : blocks) {
			if (block instanceof AbstractTextualBlock) {
				if (block instanceof ParagraphBlock) {
					images.addAll(this.getImagesOfParagraph((ParagraphBlock) block));
				} else if (block instanceof ListBlock) {
					for (ListItem listItem : ((ListBlock) block).getItems()) {
						if (listItem.getValue() instanceof ParagraphBlock) {
							images.addAll(this.getImagesOfParagraph((ParagraphBlock) listItem.getValue()));
						} else if (listItem.getValue() instanceof ListBlock) {
							ArrayList<AbstractParagraphBlock<?>> listBlock = new ArrayList<>();
							listBlock.add((ListBlock) listItem.getValue());
							images.addAll(this.getImagesRecursive(listBlock));
						}
					}
				}
			} else if (block instanceof TableBlock) {
				for (TableItem row : ((TableBlock) block).getItems()) {
					for (CellBlock cell : row.getValue()) {
						if (cell.getFirstItem().getValue() != null) {
							images.addAll(this.getImagesRecursive(cell.getFirstItem().getValue()));
						}
					}
				}
			}
		}

		return images;
	}

}
