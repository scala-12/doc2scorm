package com.ipoint.coursegenerator.core.courseModel.content.blocks.questions;

import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.PictureInfo;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * @author Kalashnikov Vladislav
 */
public abstract class AbstractQuestionBlock<T extends AbstractQuestionItem<?>> extends AbstractBlock<T> {

	public static final String TASK_BLOCK_ID = "task_block";
	public static final String FORM_NAME = "examForm";
	public static final String ANSWER_BLOCK_ID = "answers_block";

	private String task;

	protected String[] correctOrder;

	public String[] getCorrect() {
		return correctOrder;
	}

	protected AbstractQuestionBlock(List<T> items) {
		this(items, null);
	}

	protected AbstractQuestionBlock(List<T> items, String task) {
		super(items);
		this.task = ((task == null) || task.isEmpty()) ? null : task;
	}

	public String getTask() {
		return this.task;
	}

	public String removeTask() {
		return this.task = null;
	}

	public boolean setTask(String task) {
		if ((task != null) && !task.isEmpty()) {
			this.task = task;

			return true;
		}

		return false;
	}

	@Override
	/**
	 * @return div that contained 2 element: div with id and task text + form
	 *         with div of answers
	 */
	public Element toHtml(Document creatorTags) {
		Element div = creatorTags.createElement("div");

		Element task = creatorTags.createElement("div");
		task.setAttribute("id", TASK_BLOCK_ID);
		task.setTextContent(this.task);
		div.appendChild(task);

		Element form = creatorTags.createElement("form");
		Element answers = creatorTags.createElement("div");
		answers.setAttribute("id", ANSWER_BLOCK_ID);
		form.appendChild(answers);
		div.appendChild(form);

		for (T answer : this.getItems()) {
			answers.appendChild(answer.toHtml(creatorTags));
		}

		return div;
	}

	public Set<PictureInfo> getImages() {
		return Tools.getImagesRecursive(this);
	}

}
