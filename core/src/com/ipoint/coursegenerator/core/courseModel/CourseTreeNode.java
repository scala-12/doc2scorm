package com.ipoint.coursegenerator.core.courseModel;

/**
 * Node of tree. These may includes one page.
 * 
 * @see CourseTree
 * @author Kalashnikov Vladislav
 *
 */
public class CourseTreeNode extends CourseTree {

	private CoursePage page;

	private String title;

	/**
	 * Create course node
	 * 
	 * @param page
	 *            Page of node
	 * @param title
	 *            Title of node. These can't be null.
	 */
	public CourseTreeNode(CoursePage page, String title) {
		super();
		if (!this.setTitle(title)) {
			// TODO:exception
		}
		this.setPage(page);
	}

	/**
	 * @see CourseTreeNode#CourseTreeNode(CoursePage, String)
	 */
	public CourseTreeNode(String title) {
		this(null, title);
	}

	public CoursePage getPage() {
		return this.page;
	}

	public String getTitle() {
		return this.title;
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
		if (title != null) {
			if (!title.isEmpty()) {
				this.title = title;
				return true;
			}
		}

		return false;
	}

	public void setPage(CoursePage page) {
		if (page != null) {
			if ((page.getParent() != this) || (page.getParent() == null)) {
				// because class CoursePage have call of this method
				// method that below need for set link between page and node
				page.setParent(this);
			}
		}
		if (this.page != page) {
			if ((this.page != null) && (page != null)) {
				this.page.setParent(null);
			}
		}
		this.page = page;
	}

}
