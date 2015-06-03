package com.ipoint.coursegenerator.core.internalCourse.Course;

public class CourseTreeItem extends CourseTree {

	private CoursePage page;
	private String pageName;

	public CourseTreeItem(CoursePage page, String pageName) {
		super();
		this.page = page;
		this.pageName = pageName;
	}

	public CourseTreeItem(CoursePage page) {
		this(page, null);
	}
	
	public CourseTreeItem() {
		this(null);
	}

	public CoursePage getPage() {
		return this.page;
	}

	public String getPageName() {
		return this.pageName;
	}

	public void setPageName(String name) {
		this.pageName = name;
	}

	public void setPage(CoursePage page) {
		this.page = page;
	}

}
