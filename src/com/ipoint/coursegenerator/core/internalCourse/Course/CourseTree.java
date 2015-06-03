package com.ipoint.coursegenerator.core.internalCourse.Course;

import java.util.ArrayList;
import java.util.List;

public abstract class CourseTree {

	private ArrayList<CourseTreeItem> courseTree;

	public CourseTree() {
		this.courseTree = new ArrayList<CourseTreeItem>();
	}

	public void addItem(CourseTreeItem item) {
		this.courseTree.add(item);
	}

	public CourseTreeItem getItem(int i) {
		return (this.courseTree.isEmpty()) ? null
				: this.courseTree.get(i);
	}

	public List<CourseTreeItem> getCourseTree() {
		return this.courseTree;
	}

}
