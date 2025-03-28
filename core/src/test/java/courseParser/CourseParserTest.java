package test.java.courseParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.ipoint.coursegenerator.core.courseModel.structure.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.structure.ModelTreeNode;
import com.ipoint.coursegenerator.core.parsers.courseParser.CourseParser;

import test.utils.TestTools;

@RunWith(Parameterized.class)
public class CourseParserTest {

	private static final int FIRST_LEVEL = 1;
	private static final int THREE_LEVEL = 3;
	private static final int FALURE_LEVEL = -11;

	private final CourseModel courseModel;
	private final String courseName;

	// TODO: private final int headerLevel;

	@SuppressWarnings("rawtypes")
	@Parameters
	public static Collection data() {
		return Arrays.asList(new Object[][] { { "First Level", FIRST_LEVEL }, { "Three level", THREE_LEVEL },
				{ "", FALURE_LEVEL } });
	}

	public CourseParserTest(String courseName, int headerLevel) {
		this.courseName = courseName;
		// this.headerLevel = headerLevel;
		this.courseModel = CourseParser.parse(TestTools.getTestDocFile(), courseName, headerLevel);
	}

	@Test
	public void getCourseModel() {
		assertNotNull(courseModel);
		assertFalse(courseModel.getTitle().isEmpty());
		if (!courseName.isEmpty()) {
			assertEquals(courseModel.getTitle(), courseName);
		}
		assertFalse(courseModel.getChilds().isEmpty());
	}

	@Test
	@Ignore
	public void hasExtraLevels() {
		boolean hasChilds = false;
		for (ModelTreeNode node : this.courseModel.getChilds()) {
			hasChilds = hasChilds || !node.getChilds().isEmpty();
		}
	}
}
