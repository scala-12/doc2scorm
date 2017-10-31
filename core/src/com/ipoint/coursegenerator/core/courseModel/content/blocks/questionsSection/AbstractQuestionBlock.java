package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * @author Kalashnikov Vladislav
 */
public abstract class AbstractQuestionBlock<T extends AbstractQuestionItem<?>> extends AbstractSectionBlock<T> {

	public static final String TASK_BLOCK_ID = "task_block";
	public static final String FORM_NAME = "examForm";
	public static final String ANSWER_BLOCK_ID = "answers_block";

	public final static int TRUE_FALSE = 0;
	public final static int CHOICE = 1;
	public final static int MULTIPLE_CHOICE = 2;
	public final static int FILL_IN = 3;
	public final static int LONG_FILL_IN = 4;
	public final static int MATCHING = 5;
	public final static int SEQUENCING = 6;

	abstract public int getType();

	private String task;

	protected String[] correctAnswers;

	public String[] getCorrect() {
		return correctAnswers;
	}

	protected AbstractQuestionBlock(List<T> items) {
		this(items, null);
	}

	protected AbstractQuestionBlock(List<T> items, String task) {
		super(items);
		this.task = ((task == null) || task.isEmpty()) ? null : task;
		this.correctAnswers = null;
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

	@Override
	public String getText() {
		return getQuestionText(false);
	}

	@Override
	public String toString() {
		return getQuestionText(true);
	}

	private String getQuestionText(boolean withCorrectMarkers) {
		StringBuilder text = new StringBuilder();
		text.append("Задание: ").append(this.getTask()).append("\nОтветы:\n");

		this.getItems().stream().forEach((withCorrectMarkers) ? item -> text.append(item.toString()).append('\n')
				: item -> text.append(item.getText()).append('\n'));

		return Tools.removeExtraSpaces(text.toString()).trim();
	}

}
