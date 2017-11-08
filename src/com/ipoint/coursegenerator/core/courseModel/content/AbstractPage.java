package com.ipoint.coursegenerator.core.courseModel.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.Convertable;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.AbstractTextualSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.list.ListSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.ParagraphItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.AbstractContentRunItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.runs.ImageRunItem;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseTreeNode;
import com.ipoint.coursegenerator.core.utils.PictureInfo;

/**
 * Page. These includes {@link AbstractSectionBlock}
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

	public Set<PictureInfo> getImages() {
		return getImagesRecursive(this.getBlocks());
	}

	protected static Set<PictureInfo> getImagesRecursive(List<? extends AbstractBlock<?>> blocks) {
		final HashSet<PictureInfo> images = new HashSet<>();

		blocks.stream().forEach(block -> images.addAll(getImagesRecursive(block)));

		return images;
	}

	private static Set<PictureInfo> getImagesRecursive(AbstractBlock<?> block) {
		HashSet<PictureInfo> images = new HashSet<>();

		if (block instanceof AbstractTextualSectionBlock) {
			if (block instanceof ParagraphBlock) {
				images.addAll(getImagesOfParagraph((ParagraphBlock) block));
			} else if (block instanceof ListSectionBlock) {
				((ListSectionBlock) block).getItems().stream().forEach(listItem -> {
					images.addAll((listItem.getValue() instanceof ParagraphBlock)
							? getImagesOfParagraph((ParagraphBlock) listItem.getValue())
							: (listItem.getValue() instanceof ListSectionBlock)
									? getImagesRecursive(listItem.getValue()) : Collections.emptySet());
				});
			}
		} else if (block instanceof TableBlock) {
			((TableBlock) block).getItems().stream().forEach(row -> {
				row.getValue().stream().filter(cell -> cell.getItem().getValue() != null)
						.forEach(cell -> images.addAll(getImagesRecursive(cell.getItem().getValue())));
			});
		}

		return images;
	}

	private static List<PictureInfo> getImagesOfParagraph(ParagraphBlock paragraph) {
		ArrayList<PictureInfo> images = new ArrayList<PictureInfo>();

		for (ParagraphItem parItem : paragraph.getItems()) {
			for (AbstractContentRunItem<?> item : parItem.getValue().getItems()) {
				if (item instanceof ImageRunItem) {
					ImageRunItem imageItem = (ImageRunItem) item;
					PictureInfo image = new PictureInfo(imageItem.getImageFullName(), imageItem.getValue());
					images.add(image);
				}
			}
		}

		return images;
	}

}
