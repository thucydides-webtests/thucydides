package net.thucydides.core.pages.components;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by john on 30/10/2014.
 */
public class FileToDownload {
    private final URL url;

    public static FileToDownload fromUrl(URL url) {
        return new FileToDownload(url);
    }

    public FileToDownload(URL url) {
        this.url = url;
    }

    public byte[] asByteArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try(InputStream in = new BufferedInputStream(url.openStream())) {
            IOUtils.copy(in, out);
        }
        return out.toByteArray();
    }

    public String asString() throws IOException {
        return new String(asByteArray());
    }
}
