package com.ipoint.coursegenerator.core.courseModel.structure;

import java.util.UUID;

import com.ipoint.coursegenerator.core.courseModel.structure.exceptions.SimpleModelNodeCreationException;

/**
 * Model of course document with {@link AbstractTreeNode}.
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CourseModel extends AbstractTreeNode {

	private boolean hasFormulas;

	private CourseModel(String courseName) throws SimpleModelNodeCreationException {
		super(((courseName != null) && !courseName.isEmpty()) ? courseName : "course_" + UUID.randomUUID().toString());
		this.hasFormulas = false;
	}

	public static CourseModel createEmptyCourseModel(String courseName) throws SimpleModelNodeCreationException {
		return new CourseModel(courseName);
	}

	public boolean hasFormulas() {
		return this.hasFormulas;
	}

	public void setFormulasStatus(boolean hasFormulas) {
		this.hasFormulas = hasFormulas;
	}

}
