package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

	protected AbstractQuestionBlock(List<T> items, boolean needShuffle) {
		this(items, null, needShuffle);
	}

	protected AbstractQuestionBlock(List<T> items, String task, boolean needShuffle) {
		super((needShuffle) ? AbstractQuestionBlock.<T>shuffledItems(items) : items);
		this.task = ((task == null) || task.isEmpty()) ? null : task;
		this.correctAnswers = null;
	}

	private static <T extends AbstractQuestionItem<?>> List<T> shuffledItems(List<T> items) {
		ArrayList<T> shuffledItems = new ArrayList<>(items);
		Collections.shuffle(shuffledItems);

		return shuffledItems;
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
		return this.getQuestionText(false);
	}

	@Override
	public String toString() {
		return this.getQuestionText(true);
	}

	private String getQuestionText(boolean withCorrectMarkers) {

		return "Task: " + this.getTask() + ",\nAnswers: [\n"
				+ Tools.removeExtraSpaces(String.join(",\n",
						this.getItems().stream()
								.map((withCorrectMarkers) ? item -> item.toString() : item -> item.getText())
								.collect(Collectors.toList())))
				+ "\n]";
	}

}
