package test.java.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ipoint.coursegenerator.core.utils.TransliterationTool;

public class TransliterationToolsTest {

	@Test
	public void convertRU2ENStringTest() {
		assertEquals(TransliterationTool.convertRU2ENString("Конвертер курсов"), "Konverter kursov");
	}

	@Test
	public void convertRU2ENCharTest() {
		assertEquals(TransliterationTool.convertRU2ENChar('В'), "V");
	}
}
