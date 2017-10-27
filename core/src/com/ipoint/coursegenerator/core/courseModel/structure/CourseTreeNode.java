package com.ipoint.coursegenerator.core.courseModel.structure;

import com.ipoint.coursegenerator.core.courseModel.content.AbstractPage;
import com.ipoint.coursegenerator.core.courseModel.content.TheoryPage;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * Node of tree. These may includes one page.
 *
 * @author Kalashnikov Vladislav
 * @see AbstractTreeNode
 */
public class CourseTreeNode extends AbstractTreeNode {

	private AbstractPage<?> page;

	private String title;
	private String systemName;

	/**
	 * Create course node
	 *
	 * @param page
	 *            Page of node
	 * @param title
	 *            Title of node. These can't be null.
	 */
	public CourseTreeNode(TheoryPage page, String title) {
		super();
		if (!this.setTitle(title)) {
			// TODO:exception
		}
		this.setPage(page);
	}

	/**
	 * @see CourseTreeNode#CourseTreeNode(TheoryPage, String)
	 */
	public CourseTreeNode(String title) {
		this(null, title);
	}

	public AbstractPage<?> getPage() {
		return this.page;
	}

	public String getTitle() {
		return this.title;
	}

	public String getSystemName() {
		return this.systemName;
	}

	/**
	 * Set page title
	 *
	 * @param title
	 *            New title of page. If it is not string with chars then return
	 *            false
	 * @return If successful then false
	 */
	public boolean setTitle(String title) {
		if ((title != null) && !title.isEmpty()) {
			this.title = title;
			this.systemName = Tools.generateSystemName(title);

			return true;
		}

		return false;
	}

	public void setPage(AbstractPage<?> page) {
		if ((page != null) && ((page.getParent() != this) || (page.getParent() == null))) {
			// because class TheoryPage have call of this method
			// method that below need for set link between page and node
			page.setParent(this);
		}

		if ((this.page != page) && (this.page != null) && (page != null)) {
			this.page.setParent(null);
		}

		this.page = page;
	}

	public String getPageLocation() {
		String result = null;

		if (this.getPage() != null) {
			if (this.page instanceof TheoryPage) {
				result = this.getSystemName() + ".html";
			} else {
				result = this.getSystemName() + "/testframe.html";
			}
		}

		return result;
	}

}
