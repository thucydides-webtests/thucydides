package net.thucydides.core.reports.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.saucelabs.LinkGenerator;
import net.thucydides.core.reports.saucelabs.SaucelabsLinkGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JSONTestOutcomeReporter implements AcceptanceTestReporter {

	public static final String NEW_LINE_CHAR = "\n";
	public static final String ESCAPE_CHAR_FOR_NEW_LINE = "&amp;#10;";

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
			TestAnnotations testAnnotationsForClass = TestAnnotations
					.forClass(src);
			String[] annotatedIssuesForTestCase = testAnnotationsForClass
					.getAnnotatedIssuesForTestCase(src);
			addIssuesToCollectingJsonArray(issuesJsonArray,
					annotatedIssuesForTestCase);
			String annotatedIssueForTestCase = testAnnotationsForClass
					.getAnnotatedIssueForTestCase(src);
			if (annotatedIssueForTestCase != null) {
				issuesJsonArray
						.add(new JsonPrimitive(annotatedIssueForTestCase));
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
				e.printStackTrace();
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
				e.printStackTrace();
				return null;
			}
		}
	} 


	public class MyExclusionStrategy implements ExclusionStrategy {

		private final Class<?>[] typeToSkip;

		private MyExclusionStrategy(Class<?>... typeToSkip) {
			this.typeToSkip = typeToSkip;
		}

		public boolean shouldSkipClass(Class<?> clazz) {
			for (Class<?> currentClass : typeToSkip) {
				if (currentClass == clazz) {
					return true;
				}
			}
			return false;
		}

		public boolean shouldSkipField(FieldAttributes f) {
			return false;
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

	private class IssueTrackingInstanceCreator implements
			InstanceCreator<IssueTracking> {
		public IssueTracking createInstance(Type type) {
			return new SystemPropertiesIssueTracking();
		}
	}

	private class LinkGeneratorInstanceCreator implements
			InstanceCreator<LinkGenerator> {
		public LinkGenerator createInstance(Type type) {
			return new SaucelabsLinkGenerator();
		}
	}

	private class GsonOptionalDeserializer<T> implements
			JsonSerializer<Optional<T>>, JsonDeserializer<Optional<T>> {

		@Override
		public Optional<T> deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			final JsonArray asJsonArray = json.getAsJsonArray();
			final JsonElement jsonElement = asJsonArray.get(0);
			final T value = context.deserialize(jsonElement,
					((ParameterizedType) typeOfT).getActualTypeArguments()[0]);
			return Optional.fromNullable(value);
		}

		@Override
		public JsonElement serialize(Optional<T> src, Type typeOfSrc,
				JsonSerializationContext context) {
			final JsonElement element = context.serialize(src.orNull());
			final JsonArray result = new JsonArray();
			result.add(element);
			return result;
		}
	}

}