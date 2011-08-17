package net.thucydides.core.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.velocity.util.StringUtils;

/**
 * Utility class to convert test case and method names into human-readable form.
 * 
 * @author johnsmart
 *
 */
public final class NameConverter {

    private static final Pattern CAMEL_CASE_MATCHER_PATTERN = Pattern.compile("\\p{Lu}");

    private NameConverter() {}

    /**
     * Converts a class or method name into a human-readable sentence.
     */
    public static String humanize(final String name) {
        if ((name == null) || (name.trim().length() == 0)) {
            return "";
        }

        if (name.contains(" ") && !thereAreParametersIn(name)) {
            return name;
        } else if (thereAreParametersIn(name)){
            return humanizeNameWithParameters(name);
        } else {
            String noUnderscores = name.replaceAll("_", " ");
            String splitCamelCase = splitCamelCase(noUnderscores);
            return StringUtils.capitalizeFirstLetter(splitCamelCase.toLowerCase(Locale.getDefault()));
        }
    }

    private static String humanizeNameWithParameters(final String name) {
        int parametersStartAt = name.indexOf(": ");
        String bareName = name.substring(0, parametersStartAt);
        String humanizedBareName = humanize(bareName);
        String parameters = name.substring(parametersStartAt);
        return humanizedBareName + parameters;
    }

    private static boolean thereAreParametersIn(final String name) {
        return name.contains(": ");
    }

    /**
     * Inserts spaces between words in a CamelCase name.
     */
    public static String splitCamelCase(final String name) {
        Matcher m = CAMEL_CASE_MATCHER_PATTERN.matcher(name);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, " " + m.group());
        }
        m.appendTail(sb);
        return sb.toString().trim();
    }

    public static String withNoArguments(final String methodName) {
        return stripArgumentsFrom(stripIndexesFrom(methodName));
    }

    public static String stripArgumentsFrom(final String methodName)  {
        if (methodName == null) {
            return null;
        }
        int firstArgument = methodName.indexOf(':');
        if (firstArgument > 0) {
            return methodName.substring(0, firstArgument);
        } else {
            return methodName;
        }
    }

    public static String stripIndexesFrom(final String methodName)  {
        if (methodName == null) {
            return null;
        }
        int firstBracket = methodName.indexOf('[');
        if (firstBracket > 0) {
            return methodName.substring(0, firstBracket);
        } else {
            return methodName;
        }
    }

    /**
     * Transform a camel-case word to underscored-version.
     */
    public static String underscore(final String name) {
        if (name != null) {
            return name.replaceAll(" ", "_")
                    .replaceAll("<","_")
                    .replaceAll(">","_")
                    .replaceAll("'","_")
                    .replaceAll(",","_")
                    .replaceAll(":","_")
                    .replaceAll("/","_")
                    .replaceAll("\"","_")
                    .replaceAll("=","_")
                        .toLowerCase(Locale.getDefault()).trim();
        } else {
            return "";
        }
    }

}
