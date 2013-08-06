package net.thucydides.core.reports.json;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;

import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestTag;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TestOutcomeSerializer implements JsonSerializer<TestOutcome>,
		JsonDeserializer<TestOutcome> {

	private static final String TITLE_FIELD = "title";
    private static final String NAME_FIELD = "name";
    private static final String STEPS_FIELD = "steps";
    private static final String SUCCESSFUL_FIELD = "successful";
    private static final String FAILURES_FIELD = "failures";
    private static final String ERRORS_FIELD = "errors";
    private static final String SKIPPED_FIELD = "skipped";
    private static final String IGNORED_FIELD = "ignored";
    private static final String PENDING_FIELD = "pending";
    private static final String RESULT_FIELD = "result";
    private static final String TEST_STEPS = "test-steps";
    private static final String USER_STORY = "user-story";
    private static final String ISSUES = "issues";
    private static final String TAGS = "tags";
    private static final String QUALIFIER_FIELD = "qualifier";
    private static final String DURATION = "duration";
    private static final String TIMESTAMP = "timestamp";
    private static final String SESSION_ID = "session-id";
    private static final String EXAMPLES = "examples";
    private static final String MANUAL = "manual";
    public static final String NEW_LINE_CHAR = "\n";
    public static final String ESCAPE_CHAR_FOR_NEW_LINE = "&#10;";
	private static final String TEST_CASE_FIELD = "test-case";
	
	
	@Override
	public JsonElement serialize(TestOutcome testOutcome, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		obj.addProperty(TITLE_FIELD, escape(titleFrom(testOutcome)));
		obj.addProperty(NAME_FIELD, nameFrom(testOutcome));
		obj.add(TEST_CASE_FIELD, context.serialize(testOutcome.getTestCase()));
		obj.addProperty(RESULT_FIELD, testOutcome.getResult().name());
        if (testOutcome.getQualifier() != null && testOutcome.getQualifier().isPresent()) {
            obj.addProperty(QUALIFIER_FIELD, escape(testOutcome.getQualifier().get()));
        }
        obj.addProperty(STEPS_FIELD, Integer.toString(testOutcome.countTestSteps()));
        obj.addProperty(SUCCESSFUL_FIELD, Integer.toString(testOutcome.getSuccessCount()));
        obj.addProperty(FAILURES_FIELD, Integer.toString(testOutcome.getFailureCount()));
        if (testOutcome.getErrorCount() > 0) {
        	obj.addProperty(ERRORS_FIELD, Integer.toString(testOutcome.getErrorCount()));
        }
        obj.addProperty(SKIPPED_FIELD, Integer.toString(testOutcome.getSkippedCount()));
        obj.addProperty(IGNORED_FIELD, Integer.toString(testOutcome.getIgnoredCount()));
        obj.addProperty(PENDING_FIELD, Integer.toString(testOutcome.getPendingCount()));
        obj.addProperty(DURATION, Long.toString(testOutcome.getDuration()));
        obj.addProperty(TIMESTAMP, formattedTimestamp(testOutcome.getStartTime()));
        if (testOutcome.isManual()) {
        	 obj.addProperty(MANUAL, "true");
        }
        if (isNotEmpty(testOutcome.getSessionId())) {
        	obj.addProperty(SESSION_ID, testOutcome.getSessionId());
        }
        obj.add(USER_STORY, context.serialize(testOutcome.getUserStory()));
        obj.add(ISSUES, context.serialize(testOutcome.getIssues()));
        obj.add(TAGS, context.serialize(testOutcome.getTags()));
        obj.add(TEST_STEPS, context.serialize(testOutcome.getTestSteps()));
        obj.add(EXAMPLES, context.serialize(testOutcome.getDataTable()));
		return obj;
	}

	@Override
	public TestOutcome deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject outcomeJsonObject = json.getAsJsonObject();
		Class<?> testCase = context.deserialize(outcomeJsonObject.getAsJsonObject(TEST_CASE_FIELD), Class.class);
		String testOutcomeName = outcomeJsonObject.get(NAME_FIELD).getAsString();
		TestOutcome testOutcome = new TestOutcome(testOutcomeName,testCase);
		testOutcome.setTitle(unescape(outcomeJsonObject.get(TITLE_FIELD).getAsString()));
		TestResult savedTestResult = TestResult.valueOf(outcomeJsonObject.get(RESULT_FIELD).getAsString());
		JsonElement qualifierField = outcomeJsonObject.get(QUALIFIER_FIELD);
		if (qualifierField != null) {
            testOutcome = testOutcome.withQualifier(unescape(qualifierField.getAsString()));
        }
		Long duration = readDuration(outcomeJsonObject);
        testOutcome.setDuration(duration);
        testOutcome.setStartTime(readTimestamp(outcomeJsonObject));
        boolean isManualTest = readManualTest(outcomeJsonObject);
        if (isManualTest) {
            testOutcome = testOutcome.asManualTest();
        }
        String sessionId = readSessionId(outcomeJsonObject);        
        testOutcome.setSessionId(sessionId);
        
        Story story = context.deserialize(outcomeJsonObject.getAsJsonObject(USER_STORY), Story.class);
        testOutcome.setUserStory(story);
                
        Set<String> issues = context.deserialize(outcomeJsonObject.getAsJsonArray(ISSUES), Set.class);
        ArrayList<String> issuesAsString = new ArrayList<String>();
        issuesAsString.addAll(issues);
        testOutcome.addIssues(issuesAsString);        
                
        Set<TestTag> tags = context.deserialize(outcomeJsonObject.getAsJsonArray(TAGS), Set.class);
        testOutcome.setTags(tags);
        
        JsonArray testStepsJsonArray = outcomeJsonObject.getAsJsonArray(TEST_STEPS);
        for(JsonElement currentJsonElement : testStepsJsonArray ){

        	TestStep currentStep = context.deserialize(currentJsonElement, TestStep.class);
        	testOutcome.recordStep(currentStep);
        }        

        DataTable dataTable  = context.deserialize(outcomeJsonObject.getAsJsonObject(EXAMPLES), DataTable.class);        
        testOutcome.useExamplesFrom(dataTable);
        
        if(testOutcome.getStepCount().equals(0)) {
            testOutcome.setAnnotatedResult(savedTestResult);
        }
		return testOutcome;
	}
	
	private String escape(String attribute) {
		return StringUtils.replace(attribute, NEW_LINE_CHAR, ESCAPE_CHAR_FOR_NEW_LINE);
	}

	private String unescape(String attribute) {
		return StringUtils.replace(attribute, ESCAPE_CHAR_FOR_NEW_LINE, NEW_LINE_CHAR);
	}

	private String titleFrom(final TestOutcome testOutcome) {
		return testOutcome.getTitle();
	}
	
	private String nameFrom(final TestOutcome testOutcome) {
		if (testOutcome.getMethodName() != null) {
			return testOutcome.getMethodName();
		} else {
			return testOutcome.getTitle();
		}
	}

	private String formattedTimestamp(DateTime startTime) {
		return startTime.toString();
	}
		
	private long readDuration(JsonObject jsonObject) {
		String durationValue = jsonObject.get(DURATION).getAsString();
		if (StringUtils.isNumeric(durationValue)) {
			return Long.parseLong(jsonObject.get(DURATION).getAsString());
		} else {
			return 0;
		}
	}
	
    private DateTime readTimestamp(JsonObject jsonObject) {
        String timestamp = jsonObject.get(TIMESTAMP).getAsString();
        return DateTime.parse(timestamp);
    }
    
    private boolean readManualTest(JsonObject jsonObject) {
        boolean isManualTest = false;
        JsonElement manualTestAttribute = jsonObject.get(MANUAL);
        if (manualTestAttribute != null) {
            isManualTest = Boolean.valueOf(manualTestAttribute.getAsString());
        }
        return isManualTest;
    }

    private String readSessionId(JsonObject jsonObject) {
        JsonElement jsonElement = jsonObject.get(SESSION_ID);
        if(jsonElement != null) {
        	return jsonElement.getAsString();
        } else {
        	return null;
        }
    }    
}
