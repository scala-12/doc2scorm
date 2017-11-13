package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * @author Kalashnikov Vladislav
 */
public abstract class AbstractQuestionBlock<T extends AbstractQuestionItem<?>> extends AbstractSectionBlock<T> {

	public static final String TASK_BLOCK_ID = "task_block";
	public static final String FORM_NAME = "examForm";
	public static final String ANSWER_BLOCK_ID = "answers_block";

	public static enum QuestionType {
		TRUE_FALSE, CHOICE, MULTIPLE_CHOICE, FILL_IN, LONG_FILL_IN, MATCHING, SEQUENCING
	}

	abstract public QuestionType getType();

	private String task;

	private final String[] correctAnswers;

	public String[] getCorrect() {
		return Arrays.copyOf(correctAnswers, correctAnswers.length);
	}

	protected AbstractQuestionBlock(T item, String task, String correctAnswers) throws BlockCreationException {
		super(item);
		this.correctAnswers = new String[] { correctAnswers };
		this.task = ((task == null) || task.isEmpty()) ? null : task;
	}

	protected AbstractQuestionBlock(List<T> items, String task, Predicate<T> filter, Function<T, String> selector)
			throws BlockCreationException {
		super((items.size() > 1) ? AbstractQuestionBlock.<T>shuffledItems(items) : items);

		Stream<T> stream = items.stream();
		if (filter != null) {
			stream = stream.filter(filter);
		}

		this.correctAnswers = stream.map(selector).toArray(String[]::new);
		this.task = ((task == null) || task.isEmpty()) ? null : task;
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

	@Override
	/**
	 * @return div that contained 2 element: div with id and task text + form with
	 *         div of answers
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
				+ String.join(",\n",
						this.getItems().stream().map(item -> Tools.removeExtraSpaces(item.getText()))
								.collect(Collectors.toList()))
				+ "\n]" + ((withCorrectMarkers) ? "\nCorrect: " + Arrays.toString(this.correctAnswers) : "");
	}

}
