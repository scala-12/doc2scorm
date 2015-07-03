package com.ipoint.coursegenerator.core.courseModel;


/**
 * Model of course document with {@link CourseTree}.
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CourseModel extends CourseTree {

	/**
	 * id of div with content of page
	 */

	private String title;

	public CourseModel(String courseName) {
		super();
		this.title = courseName;
	}

	public String getTitle() {
		return this.title;
	}

	/**
	 * Change course title
	 * 
	 * @param title
	 *            New title of course. If it is null then return false
	 * @return If successful then true
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

}
