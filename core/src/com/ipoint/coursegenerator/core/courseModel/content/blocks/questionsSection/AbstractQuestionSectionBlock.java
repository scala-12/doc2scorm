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

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * @author Kalashnikov Vladislav
 */
public abstract class AbstractQuestionSectionBlock<T extends AbstractQuestionSectionItem<?>>
		extends AbstractSectionBlock<T> {

	public static final String TASK_BLOCK_ID = "task_block";
	public static final String FORM_NAME = "examForm";
	public static final String ANSWER_BLOCK_ID = "answers_block";

	public interface QuestionType {
		public String name();

		public String getScormName();
	}

	public static enum ComplexQuestionType implements QuestionType {
		FILL_IN, LONG_FILL_IN, MATCHING, SEQUENCING;

		@Override
		public String getScormName() {
			if (this == FILL_IN) {
				return "fill-in";
			} else if (this == LONG_FILL_IN) {
				return "long-fill-in";
			} else if (this == MATCHING) {
				return "matching";
			} else if (this == SEQUENCING) {
				return "sequencing";
			} else {
				return null;
			}
		}
	}

	public static enum ChoiceQuestionType implements QuestionType {
		TRUE_FALSE, SINGLE, MULTIPLE;

		@Override
		public String getScormName() {
			return "choice";
		}
	}

	abstract public QuestionType getType();

	private String task;

	private final String[] correctAnswers;

	public String[] getCorrect() {
		return Arrays.copyOf(correctAnswers, correctAnswers.length);
	}

	protected AbstractQuestionSectionBlock(T item, String task, String correctAnswers) throws BlockCreationException {
		super(item);
		this.correctAnswers = new String[] { correctAnswers };
		this.task = ((task == null) || task.isEmpty()) ? null : task;
	}

	protected AbstractQuestionSectionBlock(List<T> items, String task, Function<T, String> correctAnswersSelector,
			Predicate<T> correctAnswersFilterBeforeSelect) throws BlockCreationException {
		super((items.size() > 1) ? AbstractQuestionSectionBlock.<T>shuffledItems(items) : items);

		Stream<T> stream = items.stream();
		if (correctAnswersFilterBeforeSelect != null) {
			stream = stream.filter(correctAnswersFilterBeforeSelect);
		}

		this.correctAnswers = stream.map(correctAnswersSelector).toArray(String[]::new);
		this.task = ((task == null) || task.isEmpty()) ? null : task;
	}

	private static <T extends AbstractQuestionSectionItem<?>> List<T> shuffledItems(List<T> items) {
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
	public Element toHtmlModel(Document creatorTags) {
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
			answers.appendChild(answer.toHtmlModel(creatorTags));
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
