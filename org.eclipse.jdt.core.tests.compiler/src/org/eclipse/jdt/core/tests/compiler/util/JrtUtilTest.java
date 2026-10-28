/*******************************************************************************
 * Copyright (c) 2022, 2023 Andrey Loskutov, and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Andrey Loskutov (loskutov@gmx.de) - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.compiler.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.tests.junit.extension.TestCase;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.util.JRTUtil;
import org.eclipse.jdt.internal.compiler.util.JrtFileSystem;
import org.junit.Before;
import org.junit.Test;

public class JrtUtilTest extends TestCase {

	private String javaSpecVersion;
	private String javaHome;
	private File image;
	private String jdkRelease;

	public JrtUtilTest(String name) {
		super(name);
	}

	@Before
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.javaSpecVersion = System.getProperty("java.specification.version", null);
		assertNotNull("java.specification.version is not defined", this.javaSpecVersion);
		this.javaHome = System.getProperty("java.home", null);
		assertNotNull("java.home is not defined", this.javaHome);
		this.image = Paths.get(this.javaHome).toFile();
		assertTrue("java.home points to invalid path", this.image.isDirectory());
		this.jdkRelease = JRTUtil.getJdkRelease(this.image);
	}

	@Test
	public void testGetReleaseVersion() {
		long expectedLevel = CompilerOptions.versionToJdkLevel(this.javaSpecVersion);
		long seenLevel = CompilerOptions.versionToJdkLevel(this.jdkRelease);
		assertEquals("Unexpected version: " + this.jdkRelease + ", not matching " + this.javaSpecVersion, expectedLevel, seenLevel);

		int sameRelease = JavaCore.compareJavaVersions(this.javaSpecVersion, this.jdkRelease);
		assertEquals("Unexpected version: " + this.jdkRelease + ", not matching " + this.javaSpecVersion, sameRelease, 0);
	}

	@Test
	public void testGetNewJrtFileSystem() throws Exception {
		int majorVersionSegment = getMajorVersionSegment(this.jdkRelease);
		Object jrtSystem = JRTUtil.getJrtSystem(this.image, null);
		Object jrtSystem2 = JRTUtil.getJrtSystem(this.image, String.valueOf(majorVersionSegment));
		assertSame(jrtSystem, jrtSystem2);

		jrtSystem2 = JRTUtil.getJrtSystem(this.image, String.valueOf(majorVersionSegment-2));
		assertNotSame(jrtSystem, jrtSystem2);

		Object jrtSystem3 = JRTUtil.getJrtSystem(this.image, null);
		assertSame(jrtSystem, jrtSystem3);
	}

	/**
	 * The JRT file system of a JDK and the file system used for an older release of the same JDK must not share the
	 * entries of the class file cache, whichever is asked first.
	 */
	@Test
	public void testClassCacheNotSharedWithOlderRelease() throws Exception {
		int majorVersionSegment = getMajorVersionSegment(this.jdkRelease);
		String olderRelease = String.valueOf(majorVersionSegment - 2);
		try {
			JRTUtil.reset();
			JrtFileSystem olderSystem = JRTUtil.getJrtSystem(this.image, olderRelease);
			JrtFileSystem system = JRTUtil.getJrtSystem(this.image, null);
			assertNotSame(system, olderSystem);

			byte[] olderContent = getObjectClassContent(olderSystem);
			byte[] content = getObjectClassContent(system);
			assertNotNull("Lookup with an older release must not hide the class of the JDK", content);
			assertNotSame("Older release must not answer the class of the JDK", content, olderContent);

			JRTUtil.reset();
			olderSystem = JRTUtil.getJrtSystem(this.image, olderRelease);
			system = JRTUtil.getJrtSystem(this.image, null);
			content = getObjectClassContent(system);
			assertNotNull(content);
			assertNotSame("Older release must not answer the class of the JDK", content, getObjectClassContent(olderSystem));
			assertSame("Class of the JDK must still be answered from the cache", content, getObjectClassContent(system));
		} finally {
			JRTUtil.reset();
		}
	}

	private static byte[] getObjectClassContent(JrtFileSystem system) throws IOException {
		try {
			return JRTUtil.getClassfileContent(system, "java/lang/Object.class", "java.base");
		} catch (NullPointerException e) {
			// the system of an older release has no file system if the release is not found in ct.sym
			return null;
		}
	}

	@Test
	public void testWorksUnderInterruptCondition() throws IOException {
		FileSystem fs = FileSystems.getFileSystem(URI.create("jrt:/"));
		try {
			Thread.currentThread().interrupt();
			assertNotNull(JRTUtil.safeReadBytes(fs.getPath("modules", "java.base", "java/lang/Object.class")));
			assertTrue(Thread.currentThread().isInterrupted());
			assertNull(JRTUtil.safeReadBytes(fs.getPath("xxxxxxxxx")));
			assertTrue(Thread.currentThread().isInterrupted());
		} finally {
			Thread.interrupted();
		}
	}

	/**
	 * A walk notified only of modules skips the content of the modules, and must still visit the same modules as a
	 * walk notified of everything.
	 */
	@Test
	public void testWalkModulesOnly() throws IOException {
		for (JrtFileSystem system : getSystems()) {
			List<String> packages = new ArrayList<>();
			List<String> files = new ArrayList<>();
			List<String> modules = walk(system, JRTUtil.NOTIFY_MODULES, FileVisitResult.CONTINUE, packages, files);
			assertEquals("Only modules were asked for", Collections.emptyList(), packages);
			assertEquals("Only modules were asked for", Collections.emptyList(), files);

			List<String> allModules = walk(system, JRTUtil.NOTIFY_ALL, FileVisitResult.CONTINUE, packages, files);
			assertEquals(system.toString(), sorted(allModules), sorted(modules));
			assertEquals(system.toString(), !allModules.isEmpty(), !packages.isEmpty());
			assertEquals(system.toString(), !allModules.isEmpty(), !files.isEmpty());
		}
		assertTrue(walk(JRTUtil.getJrtSystem(this.image, null), JRTUtil.NOTIFY_MODULES, FileVisitResult.CONTINUE,
				new ArrayList<>(), new ArrayList<>()).contains("java.base"));
	}

	/**
	 * The content of the modules is still walked when packages or files are asked for besides the modules.
	 */
	@Test
	public void testWalkModulesAndContent() throws IOException {
		JrtFileSystem system = JRTUtil.getJrtSystem(this.image, null);
		List<String> packages = new ArrayList<>();
		List<String> files = new ArrayList<>();
		List<String> modules = walk(system, JRTUtil.NOTIFY_MODULES | JRTUtil.NOTIFY_FILES, FileVisitResult.CONTINUE, packages, files);
		assertTrue(modules.contains("java.base"));
		assertEquals(Collections.emptyList(), packages);
		assertTrue(files.contains("java.base/java/lang/Object.class"));

		files.clear();
		modules = walk(system, JRTUtil.NOTIFY_MODULES | JRTUtil.NOTIFY_PACKAGES, FileVisitResult.CONTINUE, packages, files);
		assertTrue(modules.contains("java.base"));
		assertTrue(packages.contains("java.base/java/lang"));
		assertEquals(Collections.emptyList(), files);

		packages.clear();
		modules = walk(system, JRTUtil.NOTIFY_PACKAGES | JRTUtil.NOTIFY_FILES, FileVisitResult.CONTINUE, packages, files);
		assertEquals(Collections.emptyList(), modules);
		assertTrue(packages.contains("java.base/java/lang"));
		assertTrue(files.contains("java.base/java/lang/Object.class"));
	}

	/**
	 * Skipping the content of a module must not override what the visitor of the module answers.
	 */
	@Test
	public void testWalkModulesOnlyHonorsVisitor() throws IOException {
		for (JrtFileSystem system : getSystems()) {
			for (FileVisitResult result : FileVisitResult.values()) {
				List<String> modules = walk(system, JRTUtil.NOTIFY_MODULES, result, new ArrayList<>(), new ArrayList<>());
				// the walk of the content does not skip anything by itself
				List<String> expected = walk(system, JRTUtil.NOTIFY_ALL, result, new ArrayList<>(), new ArrayList<>());
				assertEquals(system + " " + result, expected, modules);
			}
		}
		// an older release walks each of its roots in ct.sym, the image of the JDK has a single root
		JrtFileSystem system = JRTUtil.getJrtSystem(this.image, null);
		assertEquals(1, walk(system, JRTUtil.NOTIFY_MODULES, FileVisitResult.SKIP_SIBLINGS, new ArrayList<>(), new ArrayList<>()).size());
		assertEquals(1, walk(system, JRTUtil.NOTIFY_MODULES, FileVisitResult.TERMINATE, new ArrayList<>(), new ArrayList<>()).size());
	}

	private List<JrtFileSystem> getSystems() throws IOException {
		int majorVersionSegment = getMajorVersionSegment(this.jdkRelease);
		return List.of(JRTUtil.getJrtSystem(this.image, null), JRTUtil.getJrtSystem(this.image, String.valueOf(majorVersionSegment - 2)));
	}

	private static List<String> walk(JrtFileSystem system, int notify, FileVisitResult moduleResult, List<String> packages,
			List<String> files) throws IOException {
		List<String> modules = new ArrayList<>();
		JRTUtil.walkModuleImage(system, new JRTUtil.JrtFileVisitor<Path>() {
			@Override
			public FileVisitResult visitModule(Path path, String name) {
				modules.add(name);
				return moduleResult;
			}

			@Override
			public FileVisitResult visitPackage(Path dir, Path mod, BasicFileAttributes attrs) {
				packages.add(mod + "/" + dir);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, Path mod, BasicFileAttributes attrs) {
				files.add(mod + "/" + file);
				return FileVisitResult.CONTINUE;
			}
		}, notify);
		return modules;
	}

	private static List<String> sorted(List<String> list) {
		List<String> copy = new ArrayList<>(list);
		Collections.sort(copy);
		return copy;
	}

	private static int getMajorVersionSegment(String releaseVersion) {
		int dot = releaseVersion.indexOf('.');
		if (dot > 0) {
			return Integer.parseInt(releaseVersion.substring(0, dot));
		}
		return Integer.parseInt(releaseVersion);
	}
}
