package com.ipoint.coursegenerator.core.courseModel.structure;

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
	private boolean hasFormulas;

	private CourseModel(String courseName) {
		super();
		this.setTitle((isCorrectName(courseName)) ? courseName : "course_" + String.valueOf(this.hashCode()));
		this.hasFormulas = false;
	}

	public static CourseModel createEmptyCourseModel(String courseName) {
		return new CourseModel(courseName);
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		if (isCorrectName(title)) {
			this.title = title;
		}
	}

	private static boolean isCorrectName(String name) {
		return !((name == null) || name.isEmpty());
	}

	public boolean hasFormulas() {
		return this.hasFormulas;
	}

	public void setFormulasStatus(boolean hasFormulas) {
		this.hasFormulas = hasFormulas;
	}

}
