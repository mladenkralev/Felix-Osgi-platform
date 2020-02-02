package org.osgi.utils

class FileUtil {
    public static String getBundleFromDirectory(String directory, String bundle) {
        return new File(directory).listFiles().find() {
            it.name.contains(bundle)
        }.toString()
    }
}
