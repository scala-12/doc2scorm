package com.ipoint.coursegenerator.core.internalCourse.Course;

public class CourseTreeItem extends CourseTree {

	private CoursePage page;
	private String header;

	public CourseTreeItem(CoursePage page, String header) {
		super();
		this.setPage(page);
		this.setHeader(header);
	}
	
	public CourseTreeItem(String header) {
		this(null, header);
	}

	public CourseTreeItem(CoursePage page) {
		this(page, null);
	}
	
	public CourseTreeItem() {
		this(null, null);
	}

	public CoursePage getPage() {
		return this.page;
	}

	public String getHeader() {
		return this.header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setPage(CoursePage page) {
		this.page = page;
	}

}
