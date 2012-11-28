package net.thucydides.junit.util;

import net.thucydides.core.digest.Digest;
import org.apache.commons.codec.digest.DigestUtils;

public class FileFormating {

    public static String digest(String testFileName) {
        String testName = testFileName.substring(0, testFileName.indexOf("."));
        String suffix = testFileName.substring(testFileName.indexOf(".") + 1);
        return Digest.ofTextValue(testName) + "." + suffix;
    }

}
