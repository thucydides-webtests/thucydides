package net.thucydides.core.util;

import java.io.File;
import java.io.IOException;

import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedTemporaryFolder extends TemporaryFolder {
	
	private static final Logger logger = LoggerFactory.getLogger(ExtendedTemporaryFolder.class);

	protected static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}
	
	@Override
	public File newFolder() throws IOException {
		if (isWindows()) {
			try {
				return super.newFolder();
			} catch (IOException e) {
				logger.error("Error when invoke newFolder(): {}", e);
				return super.newFolder("junit");
			}
		} else {
			return super.newFolder();
		}
	}
	
	public File newFile(String fileName) throws IOException {
		if (isWindows()) {
			File file= new File(getRoot(), fileName);
			try {
				file.setWritable(true);
				file.setReadable(true);
				file.createNewFile();
			} catch (IOException e) {
				logger.error("Error when invoke newFile(fileName): {}", e.toString());
				
			}
			return file;
		} else {
			return super.newFile(fileName);
		}
	}
}
