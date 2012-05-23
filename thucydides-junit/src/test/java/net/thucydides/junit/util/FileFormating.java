package net.thucydides.junit.util;

import org.apache.commons.codec.digest.DigestUtils;

public class FileFormating {

    public static String md5(String testFileName) {
        String testName = testFileName.substring(0, testFileName.indexOf("."));
        String suffix = testFileName.substring(testFileName.indexOf(".") + 1);
        return DigestUtils.md5Hex(testName) + "." + suffix;
    }

}
