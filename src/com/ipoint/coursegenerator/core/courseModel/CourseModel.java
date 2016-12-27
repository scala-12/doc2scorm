package com.ipoint.coursegenerator.core.courseModel;


/**
 * Model of course document with {@link AbstractTreeNode}.
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CourseModel extends AbstractTreeNode {

	/**
	 * id of div with content of page
	 */

	private String title;

	private CourseModel(String courseName) {
		super();
		this.setTitle(courseName);
	}

	public static CourseModel createEmptyCourseModel(String courseName) {
		return new CourseModel(courseName);
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = ((title == null) || title.isEmpty()) ? "course_" + String.valueOf(this.hashCode())
				: title;
	}

}
