package com.ipoint.coursegenerator.core.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.Parser;
import com.ipoint.coursegenerator.core.courseModel.content.AbstractPage;
import com.ipoint.coursegenerator.core.courseModel.content.TestingPage;
import com.ipoint.coursegenerator.core.courseModel.content.TheoryPage;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.AbstractContentSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionSectionBlock.ChoiceQuestionType;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionSectionBlock.ComplexQuestionType;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.choice.ChoiceBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.choice.ChoiceItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.fillIn.FillInItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.match.MatchBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.match.MatchItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.sequence.SequenceBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.sequence.SequenceItem;
import com.ipoint.coursegenerator.core.utils.Tools.HtmlType;
import com.ipoint.coursegenerator.core.utils.Tools.SimplePair;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

public class FileTools {

	public final static String IMAGE_WMF = "image/x-wmf";
	public final static String IMAGE_PNG = "image/png";
	public final static String FILETYPE_DOCX = ".docx";
	public final static Charset STANDARD_ENCODING = StandardCharsets.UTF_8;
	public final static String IMAGE_DIR_NAME = "img";
	private final static Map<String, String> DEFAULT_TMPL_VARS = Arrays.stream(new String[][] {
			{ "system_dir", Parser.COURSE_SYSTEM_DIR }, { "theory_css", TemplateFiles.CSS4THEORY.getName() },
			{ "course_css", TemplateFiles.CSS4COURSE.getName() }, { "test_css", TemplateFiles.CSS4TEST.getName() },
			{ "jquery_ver", TemplateFiles.JQUERY_VERSION }, { "jquery_ui_ver", TemplateFiles.JQUERY_UI_VERSION },
			{ "answer_block_id", AbstractQuestionSectionBlock.ANSWER_BLOCK_ID },
			{ "companion_class", MatchBlock.MATCH_LABEL_4_ANSWER_CLASS },
			{ "answer_fieldset_id", ChoiceBlock.CHOICE_ANSWERS_FIELDSET_ID },
			{ "fill_in_field_id", FillInItem.FILL_IN_ID }, { "single", ChoiceQuestionType.SINGLE.name() },
			{ "multiple", ChoiceQuestionType.MULTIPLE.name() }, { "fill_in", ComplexQuestionType.FILL_IN.name() },
			{ "matching", ComplexQuestionType.MATCHING.name() },
			{ "sequencing", ComplexQuestionType.SEQUENCING.name() }, { "html_type_4_01", HtmlType.HTML4_01.name() },
			{ "html_type_5", HtmlType.HTML5.name() } }).map(pair -> new SimplePair<String, String>(pair[0], pair[1]))
			.collect(Collectors.toMap(SimplePair::getLeft, SimplePair::getRight));

	private static final Version CFG_VERSION = Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

	public static final int BUFFER_SIZE = 1024;

	public static class TemplateFiles {
		private static final String TEMPLATE_DIR = "templates";

		private static final File JS_DIR = new File(TEMPLATE_DIR, "js");
		public static final File JS_SYSTEM_DIR = new File(JS_DIR, "systemFiles");
		public static final File MATHJAX_DIR = new File(JS_DIR, "MathJax-2.7.1");

		private static final File CSS_DIR = new File(TEMPLATE_DIR, "css");
		public static final File CSS_SYSTEM_DIR = new File(CSS_DIR, "systemFiles");

		private static final File IMG_DIR = new File(TEMPLATE_DIR, "images");
		public static final File IMG_TEST_SCO_DIR = new File(IMG_DIR, "testingSco");

		private static final File SCO_TEMPLATES_DIR = new File(TEMPLATE_DIR, "sco");
		public final static File SCO4THEORY = new File(SCO_TEMPLATES_DIR, "sco_theory_template.ftl");
		public final static File SCO4TEST = new File(SCO_TEMPLATES_DIR, "sco_testing_template.ftl");

		private final static File HTML_DIR = new File(TEMPLATE_DIR, "html");
		private final static File HTML_TESTING_DIR = new File(HTML_DIR, "testingSco");

		public final static File CSS4THEORY = new File(CSS_SYSTEM_DIR, "theory.css");
		public final static File CSS4TEST = new File(CSS_SYSTEM_DIR, "test.css");
		public final static File CSS4COURSE = new File(CSS_SYSTEM_DIR, "course.css");

		public static final String JQUERY_VERSION = "3.1.1";
		public static final String JQUERY_UI_VERSION = "1.12.1";
	}

	private static boolean saveFile(InputStream is, File outFile, boolean isText) {
		if (null != is) {
			if (outFile.getParentFile().exists() || outFile.getParentFile().mkdirs()) {
				try {
					outFile.createNewFile();
					try (FileOutputStream fileOS = new FileOutputStream(outFile)) {
						int bytesRead;
						if (isText) {
							char[] buffer = new char[BUFFER_SIZE];
							try (OutputStreamWriter writerOS = new OutputStreamWriter(fileOS, STANDARD_ENCODING);
									InputStreamReader readerIS = new InputStreamReader(is, STANDARD_ENCODING)) {
								while ((bytesRead = readerIS.read(buffer)) != -1) {
									writerOS.write(buffer, 0, bytesRead);
								}
							}
						} else {
							byte[] buffer = new byte[BUFFER_SIZE];
							while ((bytesRead = is.read(buffer)) != -1) {
								fileOS.write(buffer, 0, bytesRead);
							}
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				return true;
			}
		}

		return false;
	}

	public static boolean saveRawFile(InputStream is, File outFile) {
		return saveFile(is, outFile, false);
	}

	public static boolean saveTextFile(String content, File outFile) {
		return saveFile(new ByteArrayInputStream(content.getBytes(STANDARD_ENCODING)), outFile, true);
	}

	public static boolean copyFileFromResourcesToDir(File fileFromResource, File destDir) {
		return saveFile(getFileFromResources(fileFromResource), new File(destDir, fileFromResource.getName()), false);
	}

	private static void copyFileFromResourceDirToDir(File resourceDir, File destDir,
			Map<String, String> templateVariables) {
		for (File file : getFilesInResourceFromDir(resourceDir)) {
			File destFile = new File(destDir, file.getPath());
			File sourceFile = new File(resourceDir, file.getPath());
			if (file.getName().toLowerCase().endsWith(".ftl")) {
				saveTemplateFileWithVariables(sourceFile, destFile, templateVariables);
			} else {
				saveFile(getFileFromResources(sourceFile), destFile, false);
			}
		}
	}

	/** @return files with path from directory */
	private static Set<File> getFilesInResourceFromDir(File resourceDir) {
		HashSet<File> files = new HashSet<>();

		String resDirPath = resourceDir.getPath().replace(File.separatorChar, '/');

		String jarPath = FileTools.class.getResource("/" + resDirPath).getFile();
		if (jarPath.startsWith("file:")) {
			jarPath = jarPath.substring("file:/".length());
		}
		if (jarPath.contains("!")) {
			jarPath = jarPath.substring(0, jarPath.indexOf('!'));
		}

		try (JarFile jar = new JarFile(new File(jarPath));) {
			final Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (!entry.isDirectory()) {
					String fileName = entry.getName().replace(File.separatorChar, '/');
					if ((fileName.length() > resDirPath.length()) && fileName.startsWith(resDirPath + '/')) {
						files.add(new File(fileName.substring(resDirPath.length())));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return files;
	}

	public static void saveSystemDir(File systemDir, boolean withMathjax) {
		FileTools.copyFileFromResourceDirToDir(TemplateFiles.CSS_SYSTEM_DIR, systemDir, null);
		FileTools.copyFileFromResourceDirToDir(TemplateFiles.JS_SYSTEM_DIR, systemDir, null);
		if (withMathjax) {
			FileTools.copyFileFromResourceDirToDir(TemplateFiles.MATHJAX_DIR,
					new File(systemDir, TemplateFiles.MATHJAX_DIR.getName()), null);
		}
	}

	public static InputStream getFileFromResources(File fileFromResource) {
		String path = fileFromResource.getPath().replace(File.separatorChar, '/');

		return FileTools.class.getClassLoader().getResourceAsStream(
				(path.startsWith(File.separator)) ? path.substring(File.separator.length()) : path);
	}

	private static boolean saveNodes2Document(NodeList bodyChilds, boolean isTheoryPage, HtmlType htmlType,
			Map<String, String> extraVars, File destFile) {
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < bodyChilds.getLength(); i++) {
			content.append(Tools.convertNodeToString(bodyChilds.item(i), htmlType));
		}

		Map<String, String> vars = new HashMap<>(extraVars);
		vars.put("body_content", content.toString());

		return saveTemplateFileWithVariables((isTheoryPage) ? TemplateFiles.SCO4THEORY : TemplateFiles.SCO4TEST,
				destFile, vars);
	}

	public static boolean saveCoursePageAsHtmlDocument(AbstractPage<?> page, HtmlType htmlType, File courseDir,
			Optional<File> sOfficeFile) {
		HashMap<String, String> scoVars = new HashMap<>(DEFAULT_TMPL_VARS);
		scoVars.put("page_title", page.getParent().getTitle());
		scoVars.put("html_type", htmlType.name());

		boolean successful = true;
		if (page instanceof TheoryPage) {
			successful = saveNodes2Document(page.toHtmlModel(Tools.createNewHTMLDocument()).getChildNodes(), true,
					htmlType, scoVars, new File(courseDir, page.getParent().getPageLocation()));
		} else {
			TestingPage testingPage = (TestingPage) page;

			createTestingDir(courseDir, testingPage, scoVars);

			for (int i = 0; i < page.getBlocks().size(); i++) {
				HashMap<String, String> vars = new HashMap<>(scoVars);
				AbstractQuestionSectionBlock<?> question = testingPage.getBlocks().get(i);

				vars.put("type", question.getType().name());
				vars.put("scorm_type", question.getType().getScormName());

				if (question instanceof ChoiceBlock) {
					List<ChoiceItem> answerItems = ((ChoiceBlock) question).getItems();
					StringBuilder answerText = new StringBuilder();
					for (int i1 = 0; i1 < answerItems.size(); i1++) {
						if (i1 != 0) {
							answerText.append(", ");
						}
						answerText.append(i1).append(" : \"").append(answerItems.get(i1).getValue().getText())
								.append("\"");
					}

					vars.put("answers_text",
							new String(answerText.toString().getBytes(STANDARD_ENCODING), STANDARD_ENCODING));
				} else if (question instanceof MatchBlock) {
					vars.put("sortable_block_id", MatchBlock.MATCH_ANSWERS_BLOCK_ID);
					vars.put("sortable_elem_class", MatchItem.MATCH_ANSWER_CLASS);
					vars.put("answer_id_prefix", MatchItem.MATCH_ANSWER_ID_PREFIX);
				} else if (question instanceof SequenceBlock) {
					vars.put("sortable_block_id", SequenceBlock.SEQUENCE_ANSWERS_BLOCK_ID);
					vars.put("sortable_elem_class", SequenceItem.SEQUENCE_ANSWER_CLASS);
					vars.put("answer_id_prefix", SequenceItem.SEQUENCE_ANSWER_ID_PREFIX);
				}

				if (saveNodes2Document(question.toHtmlModel(Tools.createNewHTMLDocument()).getChildNodes(), false,
						htmlType, vars, new File(courseDir,
								page.getParent().getSystemName() + File.separator + String.valueOf(i + 1) + ".html"))) {
				} else {
					successful = false;
				}
			}
		}

		savePageImages(page, courseDir, sOfficeFile);

		return successful;
	}

	private static void createTestingDir(File courseDir, TestingPage page, Map<String, String> vars) {
		File scoDir = new File(courseDir, page.getParent().getSystemName());

		StringBuilder intro = new StringBuilder();
		if (page.getIntroBlocks().isEmpty()) {
			intro.append("<h3>Тестирование</h3><h1>Практические тестовые задания</h1>")
					.append("<p>Внимательно прочитайте вопрос.<br>")
					.append("Подумайте и выберите вариант ответа. Нажмите кнопку <b>" + "\"Продолжить\"</b>.</p>")
					.append("<img src=\"").append(IMAGE_DIR_NAME)
					.append("/VNIMANIE.gif\" width=\"110\" height=\"28\" class=\"left-img\" longdesc=\"sysimages/VNIMANIE.gif\">")
					.append("<p>Не пытайтесь вернуться к предыдущему вопросу при помощи кнопки <strong>\"Назад\"</strong>.<br>")
					.append("Единственный способ исправить результат &#8211; закончить тест, а затем пройти его сначала.</p>")
					.append("<p>Критерии оценки:</p>" + "<ul><li>").append(page.getPercents4markA())
					.append("%-100%  &#8211 отлично</li>").append("<li>").append(page.getPercents4markB()).append("%-")
					.append(page.getPercents4markA() - 1).append("% &#8211 хорошо</li>" + "<li>")
					.append(page.getPercents4markC()).append("%-").append(page.getPercents4markB() - 1)
					.append("% &#8211 удовлетворительно </li>").append("<li>меньше ").append(page.getPercents4markC())
					.append("% &#8211 неудовлетворительно </li></ul>")
					.append("<h5>В тесте Вам могут встретиться разные типы ответов:</h5>").append("<p><img src=\"")
					.append(IMAGE_DIR_NAME)
					.append("/1.gif\" align=\"absmiddle\"> &#8211; этот значок означает, что из всех вариантов надо выбрать один правильный.</p>")
					.append("<p><img src=\"").append(IMAGE_DIR_NAME)
					.append("/2.gif\" align=\"absmiddle\"> &#8211; может быть несколько правильных ответов (надо указать все варианты).</p>")
					.append("<p><img src=\"").append(IMAGE_DIR_NAME)
					.append("/3.gif\" align=\"absmiddle\"> &#8211; в это поле ввода надо впечатать текст с клавиатуры (будьте предельно внимательны при наборе текста, на результат тестирования может повлиять даже опечатка).</p>")
					.append("<p><img src=\"").append(IMAGE_DIR_NAME)
					.append("/4.gif\" align=\"absmiddle\"> &#8211; при нажатии на стрелку откроется список с ответами, выберите правильный (на Ваш взгляд) вариант.</p>");
			copyFileFromResourceDirToDir(TemplateFiles.IMG_TEST_SCO_DIR, new File(scoDir, IMAGE_DIR_NAME), null);
		} else {
			for (AbstractContentSectionBlock<?> block : page.getIntroBlocks()) {
				intro.append(block.toHtmlModel(Tools.createNewHTMLDocument()));
			}
		}

		vars.put("intro_content", intro.toString());
		vars.put("percents4markA", String.valueOf(page.getPercents4markA()));
		vars.put("percents4markB", String.valueOf(page.getPercents4markB()));
		vars.put("percents4markC", String.valueOf(page.getPercents4markC()));

		copyFileFromResourceDirToDir(TemplateFiles.HTML_TESTING_DIR, scoDir, vars);
	}

	private static boolean saveTemplateFileWithVariables(File tmplFile, File resultFile, Map<String, String> vars) {
		resultFile.getParentFile().mkdirs();

		if (resultFile.getName().toLowerCase().endsWith(".ftl")) {
			String name = resultFile.getName();
			resultFile = new File(resultFile.getParentFile(), name.substring(0, name.lastIndexOf("ftl")) + "html");
		}

		Configuration cfg = new Configuration(CFG_VERSION);
		cfg.setClassLoaderForTemplateLoading(FileTools.class.getClassLoader(), tmplFile.getParent());
		cfg.setObjectWrapper(new DefaultObjectWrapper(CFG_VERSION));

		try {
			Template tmpl = cfg.getTemplate(tmplFile.getName(), STANDARD_ENCODING.name());
			try (FileOutputStream htmlFOS = new FileOutputStream(resultFile);
					Writer writerOS = new OutputStreamWriter(htmlFOS, STANDARD_ENCODING)) {
				tmpl.process(vars, writerOS);
				writerOS.flush();

				return true;
			}
		} catch (TemplateException | IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static Set<PictureInfo> savePageImages(AbstractPage<?> page, File courseDir, Optional<File> sOfficeFile) {
		Set<PictureInfo> images = page.getImages();
		HashSet<PictureInfo> result = null;
		if (!images.isEmpty()) {
			final HashSet<PictureInfo> withErrors = new HashSet<>();
			File imgsDir = new File(courseDir,
					((page instanceof TestingPage) ? page.getParent().getSystemName() + File.separator : "")
							+ IMAGE_DIR_NAME);
			imgsDir.mkdirs();

			images.stream().forEach(image -> {
				byte[] byteImage;

				int imgType = image.getData().getPictureType();
				if ((org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG == imgType)
						|| (org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_BMP == imgType)
						|| (org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_GIF == imgType)
						|| (org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG == imgType)) {
					byteImage = image.getData().getData();
				} else if (org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_EMF == imgType) {
					byteImage = ImageFormatConverter.transcodeEmfToPng(image.getData().getData(), sOfficeFile);
				} else if (org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_WMF == imgType) {
					byteImage = ImageFormatConverter.transcodeWmfToPng(image.getData().getData(), sOfficeFile);
				} else {
					byteImage = null;
				}

				if ((byteImage == null)
						|| saveRawFile(new ByteArrayInputStream(byteImage), new File(imgsDir, image.getName()))) {
					withErrors.add(image);
				}
			});
			result = withErrors;
		}

		return (result == null) ? Collections.emptySet() : result;
	}

}
