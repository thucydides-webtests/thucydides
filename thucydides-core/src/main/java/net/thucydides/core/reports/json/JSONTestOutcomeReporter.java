package net.thucydides.core.reports.json;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.TestOutcomes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

public class JSONTestOutcomeReporter implements AcceptanceTestReporter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(JSONTestOutcomeReporter.class);

	private File outputDirectory;

	private transient String qualifier;

	private Gson gson;

	public JSONTestOutcomeReporter() {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.registerTypeAdapter(TestOutcome.class, new TestOutcomeSerializer());
		builder.registerTypeAdapter(Class.class, new ClassTypeAdapter());
		builder.registerTypeAdapter(Throwable.class, new ThrowableClassAdapter());		
		gson = builder.create();
	}

	@Override
	public String getName() {
		return "json";
	}

	@Override
	public File generateReportFor(TestOutcome testOutcome,
			TestOutcomes allTestOutcomes) throws IOException {
		TestOutcome storedTestOutcome = testOutcome.withQualifier(qualifier);	
		Preconditions.checkNotNull(outputDirectory);
		String json = gson.toJson(storedTestOutcome);
		String reportFilename = reportFor(storedTestOutcome);
		File report = new File(getOutputDirectory(), reportFilename);
		OutputStream outputStream = new FileOutputStream(report);
		OutputStreamWriter writer = new OutputStreamWriter(outputStream,
				Charset.forName("UTF-8"));
		writer.write(json);
		writer.flush();
		writer.close();
		outputStream.close();
		return report;
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

	private String reportFor(final TestOutcome testOutcome) {
		return testOutcome.withQualifier(qualifier).getReportName(
				ReportType.JSON);
	}

	public void setOutputDirectory(final File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	@Override
	public void setQualifier(final String qualifier) {
		this.qualifier = qualifier;
	}

	public void setResourceDirectory(String resourceDirectoryPath) {
	}

	private static class ClassTypeAdapter implements JsonSerializer<Class<?>>,
			JsonDeserializer<Class<?>> {

		private static final String ISSUES = "issues";
		private static final String CLASSNAME = "classname";

		@Override
		public JsonElement serialize(Class<?> src,
				java.lang.reflect.Type typeOfSrc,
				JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add(CLASSNAME, new JsonPrimitive(src.getName()));
			JsonArray issuesJsonArray = new JsonArray();
			TestAnnotations testAnnotationsForClass = TestAnnotations.forClass(src);
			String[] annotatedIssuesForTestCase = testAnnotationsForClass.getAnnotatedIssuesForTestCase(src);
			addIssuesToCollectingJsonArray(issuesJsonArray, annotatedIssuesForTestCase);
			String annotatedIssueForTestCase = testAnnotationsForClass.getAnnotatedIssueForTestCase(src);
			if (annotatedIssueForTestCase != null) {
				issuesJsonArray.add(new JsonPrimitive(annotatedIssueForTestCase));
			}
			for (Method currentMethod : src.getMethods()) {
				String[] annotatedIssuesForMethod = testAnnotationsForClass
						.getAnnotatedIssuesForMethod(currentMethod.getName());
				addIssuesToCollectingJsonArray(issuesJsonArray,
						annotatedIssuesForMethod);
				Optional<String> annotatedIssueForMethod = testAnnotationsForClass
						.getAnnotatedIssueForMethod(currentMethod.getName());
				if (annotatedIssueForMethod.isPresent()) {
					issuesJsonArray.add(new JsonPrimitive(
							annotatedIssueForMethod.get()));
				}
			}
			if (issuesJsonArray.size() > 0) {
				jsonObject.add(ISSUES, issuesJsonArray);
			}
			return jsonObject;
		}

		private void addIssuesToCollectingJsonArray(JsonArray issuesJsonArray,
				String[] issues) {
			if (issues != null) {
				for (String issue : issues) {
					issuesJsonArray.add(new JsonPrimitive(issue));
				}
			}
		}

		@Override
		public Class<?> deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			try {
				return Class.forName(jsonObject.get(CLASSNAME).getAsString());
			} catch (ClassNotFoundException e) {
                LOGGER.warn("Could not find test class when deserializing JSON file", e);
				return null;
			}
		}
	}
	
	private class ThrowableClassAdapter implements JsonSerializer<Throwable>, JsonDeserializer<Throwable> {

		@Override
		public JsonElement serialize(Throwable src, Type typeOfSrc,
				JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("class", new JsonPrimitive(src.getClass().getName()));		    
		    jsonObject.add("message", new JsonPrimitive(src.getMessage()));
		    return jsonObject;
		}

		@Override
		public Throwable deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			JsonElement messageElement = jsonObject.get("message");
			try {
				Class<?> throwableClass = Class.forName(jsonObject.get("class").getAsString());
				return (Throwable) throwableClass.getConstructor(String.class).newInstance(messageElement.getAsString());
			} catch (Throwable e) {
                LOGGER.warn("Could not find test class when deserializing JSON file", e);
				return null;
			}
		}
	}

	public Optional<TestOutcome> loadReportFrom(final File reportFile)
			throws IOException {
		try {
			String jsonString = Files.toString(reportFile,
					Charset.forName("UTF-8"));
			TestOutcome fromJson = gson.fromJson(jsonString, TestOutcome.class);
			return Optional.of(fromJson);
		} catch (Exception e) {
			LOGGER.error("Cannot load class ", e);
			return Optional.absent();
		}
	}
}