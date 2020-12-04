
package org.liujk.java.framework.base.utils.lang;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 说明：
 * <p>
 * 与 jar 有关的工具。
 * <p>
 * 如果没有特别说明，向该类的方法传递 null 都会抛出 {@link NullPointerException}
 *
 */
public abstract class JarUtils {
    /**
     * 得到 path 指定目录中的 jar 文件列表。
     *
     * @param path 指定的目录。
     *
     * @return path 对应目录的 jar 文件列表。
     */
    public static List<JarFile> getJarResource (String path) {
        List<File> files = IOUtils.fileList (path, new FilenameFilter () {
            @Override
            public boolean accept (File dir, String name) {
                return name.endsWith (".jar");
            }
        });
        List<JarFile> jarFiles = new ArrayList<JarFile> (files.size ());
        for (File file : files) {
            try {
                jarFiles.add (new JarFile (file));
            } catch (IOException e) {
                // 不可能有异常
            }
        }
        return jarFiles;
    }

    /**
     * 得到 file 指定目录中的 jar 文件列表。
     *
     * @param file 指定的目录。
     *
     * @return file 对应目录的 jar 文件列表。
     */
    public static List<JarFile> getJarResource (File file) {
        return getJarResource (file.getPath ());
    }

    /**
     * 从 jar 中获取 fileName 对应的文件的输入流。
     *
     * @param jarFile  jar文件。
     * @param fileName 要获取输入流的文件名。
     *
     * @return fileName 对应的文件的输入流。
     * @throws IOException 发生 I/O 异常时。
     */
    public static InputStream findFileInputStreamByJar (JarFile jarFile, String fileName)
            throws IOException {
        JarEntry jarEntry = jarFile.getJarEntry (fileName);
        if (jarEntry != null) {
            return jarFile.getInputStream (jarEntry);
        }
        return null;
    }
}
