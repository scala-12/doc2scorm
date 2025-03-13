package test.java.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import com.ipoint.coursegenerator.core.utils.FileTools;

public class FileToolsTest {

	private static final File testFile = new File("templates/js/systemFiles/APIWrapper.js");

	@Test
	public void getFileFromResources() {
		// TODO: take from prop file
		assertNotNull(FileTools.getFileFromResources(testFile));
	}

	@Test
	public void saveRawFileTest() throws IOException {
		File file = File.createTempFile("check-saving_", "d2s");
		file.deleteOnExit();
		assertTrue(FileTools.saveRawFile(FileTools.getFileFromResources(testFile), file));
	}

	@Test
	public void saveTextFileTest() throws IOException {
		File checkFile = File.createTempFile("check-saving_", "d2s");
		checkFile.deleteOnExit();
		// TODO: save several files in different encoding
		File[] files = { testFile };
		for (File file : files) {
			try (InputStreamReader resourceReader = new InputStreamReader(FileTools.getFileFromResources(file))) {
				StringBuilder testContent = new StringBuilder();
				char[] buffer = new char[1024];
				int readLength;
				while ((readLength = resourceReader.read(buffer)) != -1) {
					testContent.append(new String(buffer, 0, readLength));
				}

				assertTrue(FileTools.saveTextFile(testContent.toString(), checkFile));

				try (FileInputStream checkIS = new FileInputStream(checkFile);
						InputStreamReader checkReader = new InputStreamReader(checkIS)) {
					StringBuilder checkContent = new StringBuilder();
					while ((readLength = checkReader.read(buffer)) != -1) {
						checkContent.append(new String(buffer, 0, readLength));
					}

					assertEquals(testContent.toString(), checkContent.toString());
				}
			}
		}
	}

	@Test
	public void copyFileFromResourcesToDir() throws IOException {
		File checkFile = File.createTempFile("check-copy_", "d2s");
		checkFile.deleteOnExit();
		if (!(checkFile = new File(checkFile.getParentFile(), testFile.getName())).exists()) {
			assertTrue(FileTools.copyFileFromResourcesToDir(testFile, checkFile.getParentFile()));
		}
	}
}
