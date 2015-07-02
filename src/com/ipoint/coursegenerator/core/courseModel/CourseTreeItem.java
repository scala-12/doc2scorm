package com.ipoint.coursegenerator.core.courseModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Node of the document tree
 * 
 * @see CourseTree
 * @author Kalashnikov Vladislav
 *
 */
public class CourseTreeItem extends CourseTree implements Convertable {

	/**
	 * Page of node
	 */
	private CoursePage page;

	/**
	 * Title of node
	 */
	private String title;

	/**
	 * Create node which has preset page and page name
	 * 
	 * @param page
	 *            Page of document
	 * @param title
	 *            Title of page. May not null
	 */
	public CourseTreeItem(CoursePage page, String title) {
		super();
		this.setPage(page);

		// TODO: exception if title is null (then setTitle = false)
		this.setTitle(title);
	}

	/**
	 * Create node which has preset page title without page
	 * 
	 * @param title
	 *            Title of page. May not null
	 */
	public CourseTreeItem(String title) {
		this(null, title);
	}

	/**
	 * Returns page of node
	 * 
	 * @return Page of node
	 */
	public CoursePage getPage() {
		return this.page;
	}

	/**
	 * Returns title of page
	 * 
	 * @return Title of page
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Change title of page
	 * 
	 * @param title
	 *            New title
	 * @return True if title not null
	 */
	public boolean setTitle(String title) {
		this.title = (title == null) ? null : (title.isEmpty() ? null : title);
		return (title == null) ? false : (!title.isEmpty());
	}

	public void setPage(CoursePage page) {
		this.page = page;
	}

	@Override
	public Element toHtml(Document creatorTags) {
		Element pageBody = null;
		pageBody = creatorTags.createElement("div");
		Element pageHeader = creatorTags.createElement("h1");
		pageHeader.setTextContent(this.getTitle());
		pageBody.appendChild(pageHeader);
		NodeList pageElements = this.getPage().toHtml(creatorTags)
				.getChildNodes();
		for (int i = 0; i < pageElements.getLength(); ++i) {
			pageBody.appendChild(pageElements.item(i));
		}

		return pageBody;
	}

}
