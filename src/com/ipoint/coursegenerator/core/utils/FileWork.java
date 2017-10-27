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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.Parser;
import com.ipoint.coursegenerator.core.courseModel.content.AbstractPage;
import com.ipoint.coursegenerator.core.courseModel.content.PictureInfo;
import com.ipoint.coursegenerator.core.courseModel.content.TestingPage;
import com.ipoint.coursegenerator.core.courseModel.content.TheoryPage;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.choice.ChoiceBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.choice.ChoiceItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.fillIn.FillInItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.match.MatchBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.match.MatchItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.sequence.SequenceBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.sequence.SequenceItem;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FileWork {

	public final static String IMAGE_WMF = "image/x-wmf";
	public final static String IMAGE_PNG = "image/png";
	public final static String FILETYPE_DOCX = ".docx";
	public final static Charset STANDARD_ENCODING = StandardCharsets.UTF_8;
	public final static String IMAGE_DIR_NAME = "img";
	private final static Map<String, String> DEFAULT_TMPL_VARS = _getDefaultVars();

	public static final int BUFFER_SIZE = 1024;

	public static class TemplateFiles {
		private static final String TEMPLATE_DIR = "templates";

		private static final File JS_DIR = new File(TEMPLATE_DIR, "js");
		public static final File JS_SYSTEM_DIR = new File(JS_DIR, "systemFiles");

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

	private static Map<String, String> _getDefaultVars() {
		HashMap<String, String> vars = new HashMap<>();

		vars.put("system_dir", Parser.COURSE_SYSTEM_DIR);

		vars.put("theory_css", TemplateFiles.CSS4THEORY.getName());
		vars.put("course_css", TemplateFiles.CSS4COURSE.getName());
		vars.put("test_css", TemplateFiles.CSS4TEST.getName());

		vars.put("jquery_ver", TemplateFiles.JQUERY_VERSION);
		vars.put("jquery_ui_ver", TemplateFiles.JQUERY_UI_VERSION);

		vars.put("answer_block_id", AbstractQuestionBlock.ANSWER_BLOCK_ID);
		vars.put("companion_class", MatchItem.MATCH_LABEL_4_ANSWER_CLASS);
		vars.put("answer_fieldset_id", ChoiceBlock.CHOICE_ANSWERS_FIELDSET_ID);
		vars.put("fill_in_field_id", FillInItem.FILL_IN_ID);

		vars.put("choice", String.valueOf(AbstractQuestionBlock.CHOICE));
		vars.put("multiple", String.valueOf(AbstractQuestionBlock.MULTIPLE_CHOICE));
		vars.put("fill_in", String.valueOf(AbstractQuestionBlock.FILL_IN));
		vars.put("match", String.valueOf(AbstractQuestionBlock.MATCHING));
		vars.put("sequence", String.valueOf(AbstractQuestionBlock.SEQUENCING));

		return vars;
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

		String jarPath = FileWork.class.getResource("/" + resDirPath).getFile();
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

	public static void saveSystemDir(File systemDir) {
		FileWork.copyFileFromResourceDirToDir(TemplateFiles.CSS_SYSTEM_DIR, systemDir, null);
		FileWork.copyFileFromResourceDirToDir(TemplateFiles.JS_SYSTEM_DIR, systemDir, null);
	}

	public static InputStream getFileFromResources(File fileFromResource) {
		String path = fileFromResource.getPath().replace(File.separatorChar, '/');

		return FileWork.class.getClassLoader().getResourceAsStream(
				(path.startsWith(File.separator)) ? path.substring(File.separator.length()) : path);
	}

	private static boolean saveHtmlDocument(File tmplFile, Map<String, String> extraVars, Document doc, File destFile) {
		StringBuilder content = new StringBuilder();
		NodeList bodyChilds = doc.getElementsByTagName("body").item(0).getChildNodes();
		for (int i = 0; i < bodyChilds.getLength(); i++) {
			content.append(Tools.getNodeString(bodyChilds.item(i)));
		}

		Map<String, String> vars = new HashMap<>(extraVars);

		vars.put("system_dir", Parser.COURSE_SYSTEM_DIR);

		vars.put("theory_css", TemplateFiles.CSS4THEORY.getName());
		vars.put("course_css", TemplateFiles.CSS4COURSE.getName());
		vars.put("test_css", TemplateFiles.CSS4TEST.getName());

		vars.put("jquery_ver", TemplateFiles.JQUERY_VERSION);
		vars.put("jquery_ui_ver", TemplateFiles.JQUERY_UI_VERSION);

		vars.put("body_content", content.toString());

		return saveTemplateFileWithVariables(tmplFile, destFile, vars);
	}

	public static boolean saveCoursePageAsHtmlDocument(AbstractPage<?> page, File courseDir, File sOfficeFile) {
		HashMap<String, String> scoVars = new HashMap<>(DEFAULT_TMPL_VARS);
		scoVars.put("page_title", page.getParent().getTitle());

		if (page instanceof TheoryPage) {
			StringBuilder content = new StringBuilder();

			Document html = Tools.createNewHTMLDocument();
			html.getElementsByTagName("body").item(0).appendChild(page.toHtml(html));

			NodeList bodyChilds = html.getElementsByTagName("body").item(0).getChildNodes();
			for (int i = 0; i < bodyChilds.getLength(); i++) {
				content.append(Tools.getNodeString(bodyChilds.item(i)));
			}

			scoVars.put("body_content", content.toString());

			boolean successful = saveTemplateFileWithVariables(TemplateFiles.SCO4THEORY,
					new File(courseDir, page.getParent().getPageLocation()), scoVars);

			if (successful) {
				page.getImages().stream().forEach(image -> {
					savePageImages(page, courseDir, sOfficeFile);
				});
			}

			return successful;
		} else {
			TestingPage testingPage = (TestingPage) page;

			createTestingDir(courseDir, testingPage, scoVars);

			boolean allSuccessful = true;

			for (int i = 0; i < page.getBlocks().size(); i++) {
				HashMap<String, String> vars = new HashMap<>(scoVars);
				AbstractQuestionBlock<?> question = testingPage.getBlocks().get(i);

				vars.put("type", String.valueOf(question.getType()));

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
					vars.put("answer_id_prefix", MatchBlock.MATCH_ANSWER_ID_PREFIX);
				} else if (question instanceof SequenceBlock) {
					vars.put("sortable_block_id", SequenceBlock.SEQUENCE_ANSWERS_BLOCK_ID);
					vars.put("sortable_elem_class", SequenceItem.SEQUENCE_ANSWER_CLASS);
					vars.put("answer_id_prefix", SequenceBlock.SEQUENCE_ANSWER_ID_PREFIX);
				}

				Document html = Tools.createNewHTMLDocument();
				NodeList nodes = question.toHtml(html).getChildNodes();
				Node body = html.getElementsByTagName("body").item(0);
				while (nodes.getLength() != 0) {
					body.appendChild(nodes.item(0));
				}

				if (saveHtmlDocument(TemplateFiles.SCO4TEST, vars, html, new File(courseDir,
						page.getParent().getSystemName() + File.separator + String.valueOf(i + 1) + ".html"))) {
					Set<PictureInfo> images = page.getImages();
					if (!images.isEmpty()) {
						File imgDir = new File(courseDir, ((page instanceof TestingPage)
								? page.getParent().getPageLocation() + File.separator : "") + FileWork.IMAGE_DIR_NAME);
						if (!imgDir.exists()) {
							imgDir.mkdirs();
						}

						images.stream().forEach(image -> {
							savePageImages(page, courseDir, sOfficeFile);
						});
					}

				} else {
					allSuccessful = false;
				}
			}

			return allSuccessful;
		}
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
			for (AbstractParagraphBlock<?> block : page.getIntroBlocks()) {
				intro.append(block.toHtml(Tools.createNewHTMLDocument()));
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

		Configuration cfg = new Configuration();
		cfg.setClassLoaderForTemplateLoading(FileWork.class.getClassLoader(), tmplFile.getParent());
		cfg.setObjectWrapper(new DefaultObjectWrapper());

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

	private static Set<PictureInfo> savePageImages(AbstractPage<?> page, File courseDir, File sOfficeFile) {
		File imgsDir = new File(courseDir,
				((page instanceof TestingPage) ? page.getParent().getSystemName() + File.separator : "")
						+ IMAGE_DIR_NAME);
		HashSet<PictureInfo> withErrors = new HashSet<>();

		page.getImages().stream().forEach(image -> {
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

		return withErrors;
	}

}
