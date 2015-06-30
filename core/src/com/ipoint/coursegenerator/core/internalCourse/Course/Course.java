package com.ipoint.coursegenerator.core.internalCourse.Course;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

	// TODO: methods for creating the manifest and delete manifest as field
	private String manifest;

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

	public String getManifest() {
		return manifest;
	}

	public void setManifest(String manifest) {
		this.manifest = manifest;
	}

}
