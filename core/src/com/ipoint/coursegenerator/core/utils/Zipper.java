package com.ipoint.coursegenerator.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Zipper {
	private static final int BUFFER = 2048;

	private String zipFileName;

	private ZipFile zipFile;

	private File zipDir;

	private boolean isDir(String fileName) {
		File f = new File(fileName);
		return f.isDirectory();
	}

	public Zipper(String zip, String dir) {
		this.zipFileName = zip;
		this.zipDir = new File(dir);
	}

	private void addDir(ZipOutputStream out, FileOutputStream dest, File subDir, String root, String[] ignoreList) {
		try {
			String files[] = subDir.list();

			BufferedInputStream origin = null;
			byte data[] = new byte[BUFFER];

			for (int i = 0; i < files.length; i++) {
				String currentName = subDir.getPath().replace(File.separatorChar, '/') + "/" + files[i];
				boolean isInIgnoreList = false;
				for (int j = 0; j < ignoreList.length; j++) {
					if (currentName.endsWith(ignoreList[j])) {
						isInIgnoreList = true;
					}
				}
				if (!isInIgnoreList) {
					if (isDir(currentName)) {
						addDir(out, dest, new File(currentName), root, ignoreList);
					} else {
						FileInputStream fi = new FileInputStream(currentName);
						origin = new BufferedInputStream(fi, BUFFER);
						ZipEntry entry = new ZipEntry(currentName.substring(root.length() + 1));
						out.putNextEntry(entry);
						int count;
						while ((count = origin.read(data, 0, BUFFER)) != -1) {
							out.write(data, 0, count);
						}
						origin.close();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void addToZip(String[] ignoreList) {
		try {
			FileOutputStream dest = new FileOutputStream(zipFileName);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			addDir(out, dest, zipDir, zipDir.getAbsolutePath(), ignoreList);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void extractFromZip() {
		try {
			BufferedOutputStream dest = null;
			BufferedInputStream is = null;
			ZipEntry entry;
			Enumeration<?> e = zipFile.entries();
			while (e.hasMoreElements()) {
				entry = (ZipEntry) e.nextElement();
				if (entry.isDirectory()) {
					System.out.println("    DIRECTORY: " + zipDir + "\\" + entry.getName());
					String newDir = zipDir + "\\" + entry.getName();
					newDir = newDir.replace('/', '\\');
					newDir = newDir.substring(0, newDir.length() - 1);

					System.out.println(newDir);
					boolean success = (new File(newDir).mkdirs());
					System.out.println(success);
				} else {
					System.out.println("Extracting: " + entry);

					is = new BufferedInputStream(zipFile.getInputStream(entry));
					int count;
					byte data[] = new byte[BUFFER];
					FileOutputStream fos = new FileOutputStream(entry.getName());
					dest = new BufferedOutputStream(fos, BUFFER);
					while ((count = is.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
					is.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printZipList() {
		// try {
		for (Enumeration<?> entries = zipFile.entries(); entries.hasMoreElements();) {
			String zipEntryName = ((ZipEntry) entries.nextElement()).getName();
			System.out.println(zipEntryName);
		}
		// } catch (IOException e) {}

		// return null;
	}

	public void printDirList(File dir, String pref) {
		if (!dir.isDirectory()) {
			System.out.println(pref + dir.getName());
		}

		String[] files; // The names of the files in the directory.
		files = dir.list();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File f; // One of the files in the directory.
				f = new File(dir, files[i]);
				if (f.isDirectory()) {
					printDirList(f, pref + files[i] + "\\");
				} else {
					System.out.println(pref + files[i]);
				}
			}
		}

	}
}