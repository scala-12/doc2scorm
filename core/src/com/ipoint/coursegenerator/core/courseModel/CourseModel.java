package com.ipoint.coursegenerator.core.courseModel;


/**
 * Model of course document with {@link AbstractThreeNode}.
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CourseModel extends AbstractThreeNode {

	/**
	 * id of div with content of page
	 */

	private String title;

	public CourseModel(String courseName) {
		super();
		this.setTitle(courseName);
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = ((title == null) || title.isEmpty()) ? "course_" + String.valueOf(this.hashCode())
				: title;
	}

}
