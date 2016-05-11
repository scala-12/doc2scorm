package test.java.utilsTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.ipoint.coursegenerator.core.utils.FileWork;

public class FileWorkTest {

	@Test
	public void getFileFromResources() {
		// TODO: take from prop file
		assertNotNull(FileWork.getFileFromResources("templates/js",
				"APIWrapper.js"));
	}

	@Test
	public void saveFileFromResourcesTest() throws IOException {
		File file;
		file = File.createTempFile("check-saving_", "d2s");
		assertTrue(FileWork.saveRawFile(
				FileWork.getFileFromResources("templates/js", "APIWrapper.js"),
				file));
		assertTrue(FileWork.saveTextFile(
				FileWork.getFileFromResources("templates/js", "APIWrapper.js"),
				file));
		assertTrue(FileWork.saveTextFile(
				FileWork.getFileFromResources("templates/js", "APIWrapper.js"),
				file));
		file.delete();
	}
}
