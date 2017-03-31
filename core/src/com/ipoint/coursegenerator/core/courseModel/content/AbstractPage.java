package com.ipoint.coursegenerator.core.courseModel.content;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.Convertable;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.tabular.TableItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.tabular.cell.CellBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.AbstractTextualBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.list.ListItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph.ParagraphItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph.content.AbstractContentItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph.content.items.ImageContentItem;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseTreeNode;

/**
 * Page. These includes {@link AbstractParagraphBlock}
 * 
 * @see CourseModel
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractPage<T extends AbstractBlock<?>> implements Convertable {

	private final ArrayList<T> blocks;

	private CourseTreeNode parentNode;

	public final static String CONTENT_DIV_ID = "page_content";

	protected AbstractPage(CourseTreeNode parentNode) {
		this.blocks = new ArrayList<>();
		this.setParent(parentNode);
	}

	public List<T> getBlocks() {
		return new ArrayList<>(this.blocks);
	}

	/** @return true if replaced */
	public boolean setBlocks(List<T> blocks) {
		ArrayList<T> newBlocks = new ArrayList<>();
		for (T block : blocks) {
			if (block != null) {
				newBlocks.add(block);
			}
		}

		if (!newBlocks.isEmpty()) {
			clearBlocks();
			this.blocks.addAll(newBlocks);
		}

		return !newBlocks.isEmpty();
	}

	public void clearBlocks() {
		this.blocks.clear();
	}

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

	public Set<PictureInfo> getImages() {
		return this.getImagesRecursive(new ArrayList<AbstractBlock<?>>(this.getBlocks()));
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

	private Set<PictureInfo> getImagesRecursive(List<AbstractBlock<?>> blocks) {
		HashSet<PictureInfo> images = new HashSet<>();

		for (AbstractBlock<?> block : blocks) {
			images.addAll(getImagesRecursive(block));
		}

		return images;
	}

	private Set<PictureInfo> getImagesRecursive(AbstractBlock<?> block) {
		HashSet<PictureInfo> images = new HashSet<>();

		if (block instanceof AbstractTextualBlock) {
			if (block instanceof ParagraphBlock) {
				images.addAll(this.getImagesOfParagraph((ParagraphBlock) block));
			} else if (block instanceof ListBlock) {
				for (ListItem listItem : ((ListBlock) block).getItems()) {
					if (listItem.getValue() instanceof ParagraphBlock) {
						images.addAll(this.getImagesOfParagraph((ParagraphBlock) listItem.getValue()));
					} else if (listItem.getValue() instanceof ListBlock) {
						images.addAll(this.getImagesRecursive(listItem.getValue()));
					}
				}
			}
		} else if (block instanceof TableBlock) {
			for (TableItem row : ((TableBlock) block).getItems()) {
				for (CellBlock cell : row.getValue()) {
					if (cell.getFirstItem().getValue() != null) {
						images.addAll(this
								.getImagesRecursive(new ArrayList<AbstractBlock<?>>(cell.getFirstItem().getValue())));
					}
				}
			}
		}

		return images;
	}

	/**
	 * @return html-element div
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element pageBody = creatorTags.createElement("div");
		pageBody.setAttribute("id", CONTENT_DIV_ID);

		if (this.getParent() != null) {
			Element pageHeader = creatorTags.createElement("h1");
			pageHeader.setTextContent(this.getParent().getTitle());
			pageBody.appendChild(pageHeader);
		}

		for (T par : this.getBlocks()) {
			pageBody.appendChild(par.toHtml(creatorTags));
		}

		return pageBody;
	}

}
