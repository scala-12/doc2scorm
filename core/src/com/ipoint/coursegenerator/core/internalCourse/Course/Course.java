package com.ipoint.coursegenerator.core.internalCourse.Course;

//TODO: rename class, because it is document, and write constructor
/**
 * Model of document
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class Course extends CourseTree {

	/**
	 * Name of course
	 */
	private String title;

	/**
	 * Returns name of document
	 * 
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Change course name
	 * 
	 * @param title
	 *            Name of course
	 * @return If name is not null, then true
	 */
	public boolean setTitle(String title) {
		this.title = (title == null) ? null : (title.isEmpty() ? null : title);
		return (title == null) ? false : (!title.isEmpty());
	}

}
