package com.soonsoft.uranus.web.spring;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;

import com.soonsoft.uranus.core.common.lang.StringUtils;

public class WebApplicationHome {

    private final File source;
	private final File dir;

	public WebApplicationHome(Class<?> sourceClass) {
		this.source = findSource(sourceClass);
		this.dir = findHomeDir(this.source);
	}

	private File findSource(Class<?> sourceClass) {
		try {
			ProtectionDomain domain = (sourceClass != null) ? sourceClass.getProtectionDomain() : null;
			CodeSource codeSource = (domain != null) ? domain.getCodeSource() : null;
			URL location = (codeSource != null) ? codeSource.getLocation() : null;
			File source = (location != null) ? findSource(location) : null;
			if (source != null && source.exists() && !isUnitTest()) {
				return source.getAbsoluteFile();
			}
		}
		catch (Exception ex) {
		}
		return null;
	}

	private boolean isUnitTest() {
		try {
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			for (int i = stackTrace.length - 1; i >= 0; i--) {
				if (stackTrace[i].getClassName().startsWith("org.junit.")) {
					return true;
				}
			}
		}
		catch (Exception ex) {
		}
		return false;
	}

	private File findSource(URL location) throws IOException, URISyntaxException {
		URLConnection connection = location.openConnection();
		if (connection instanceof JarURLConnection) {
			return getRootJarFile(((JarURLConnection) connection).getJarFile());
		}
		return new File(location.toURI());
	}

	private File getRootJarFile(JarFile jarFile) {
		String name = jarFile.getName();
		int separator = name.indexOf("!/");
		if (separator > 0) {
			name = name.substring(0, separator);
		}
		return new File(name);
	}

	private File findHomeDir(File source) {
		File homeDir = source;
		homeDir = (homeDir != null) ? homeDir : findDefaultHomeDir();
		if (homeDir.isFile()) {
			homeDir = homeDir.getParentFile();
		}
		homeDir = homeDir.exists() ? homeDir : new File(".");
		return homeDir.getAbsoluteFile();
	}

	private File findDefaultHomeDir() {
		String userDir = System.getProperty("user.dir");
		return new File(!StringUtils.isEmpty(userDir) ? userDir : ".");
	}

	/**
	 * Returns the underlying source used to find the home directory. This is usually the
	 * jar file or a directory. Can return {@code null} if the source cannot be
	 * determined.
	 * @return the underlying source or {@code null}
	 */
	public File getSource() {
		return this.source;
	}

	/**
	 * Returns the application home directory.
	 * @return the home directory (never {@code null})
	 */
	public File getDir() {
		return this.dir;
	}

	@Override
	public String toString() {
		return getDir().toString();
	}
}
