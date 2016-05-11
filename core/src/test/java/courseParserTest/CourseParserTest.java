package test.java.courseParserTest;

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

import test.java.TestUtils;

import com.ipoint.coursegenerator.core.courseModel.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.CourseTreeNode;
import com.ipoint.coursegenerator.core.parsers.courseParser.CourseParser;

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
		return Arrays.asList(new Object[][] { { "First Level", FIRST_LEVEL },
				{ "Three level", THREE_LEVEL }, { "", FALURE_LEVEL } });
	}

	public CourseParserTest(String courseName, int headerLevel) {
		this.courseName = courseName;
		// this.headerLevel = headerLevel;
		this.courseModel = new CourseParser().parse(TestUtils.getTestDocFile(),
				courseName, headerLevel);
	}

	@Test
	public void getCourseModel() {
		assertNotNull(courseModel);
		assertFalse(courseModel.getTitle().isEmpty());
		if (!courseName.isEmpty()) {
			assertEquals(courseModel.getTitle(), courseName);
		}
		assertFalse(courseModel.getNodes().isEmpty());
	}

	@Test
	@Ignore
	public void hasExtraLevels() {
		boolean hasChilds = false;
		for (CourseTreeNode node : this.courseModel.getNodes()) {
			hasChilds = hasChilds || !node.getNodes().isEmpty();
		}
	}
}
