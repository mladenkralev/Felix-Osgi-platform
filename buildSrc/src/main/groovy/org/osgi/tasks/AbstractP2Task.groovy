package org.osgi.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask

import java.util.jar.Attributes
import java.util.jar.Manifest
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@Slf4j
class AbstractP2Task extends DefaultTask  {
    public static final String BUNDLES_INFO = "org.eclipse.equinox.simpleconfigurator\\bundles.info"

    static void createBundlesInfo(String configurationFolder, String pluginsFolder) {
        File bundlesInfo = new File(configurationFolder + File.separator + BUNDLES_INFO.toString())

        bundlesInfo.getParentFile().mkdirs();
        if (!bundlesInfo.exists()) {
            bundlesInfo.createNewFile();
        }

        addConfigurationInFile(bundlesInfo, pluginsFolder)
    }

    private static addConfigurationInFile(File outputFile, String bundlesFolder) {
        def configurationBundles = StringBuilder.newInstance()

        new File(bundlesFolder).eachFile {
            Pattern p = Pattern.compile("([^a-z]+\\Qsource\\E)|(\\Qgroovy\\E)|([^\\.]\\Qgradle\\E)");

            Matcher matcher = p.matcher(it.name);
            boolean findMatch = matcher.find()

            log.info("Entry jar is: " + it.getAbsolutePath())
            if (!findMatch) {
//                println it.absoluteFile.exists()
                ZipFile zipFile = new ZipFile(it.path)

                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.getName().equals("META-INF/MANIFEST.MF")) {

                        InputStream stream = zipFile.getInputStream(entry);

                        Manifest manifest = new Manifest(stream)
                        String bundleSymbolicName = manifest.mainAttributes.getValue(new Attributes.Name("Bundle-SymbolicName"))
                        String bundleVersion = manifest.mainAttributes.getValue(new Attributes.Name("Bundle-Version"))
                        boolean isFragment = manifest.getAttributes('Fragment-Host')
                        String path = it.path

                        int indexBadChar = bundleSymbolicName.indexOf(";")
                        if (indexBadChar != -1) {
                            bundleSymbolicName = bundleSymbolicName.substring(0, indexBadChar)
                        }

                        if (bundleSymbolicName != null && bundleVersion != null) {
                            if (isFragment) {
                                configurationBundles << getBundlesInfoLineFor(bundleSymbolicName, bundleVersion, path, false)
                            } else {
                                configurationBundles << getBundlesInfoLineFor(bundleSymbolicName, bundleVersion, path, true)
                            }
                        }
                    }
                }
            }
        }
        outputFile.append(configurationBundles.toString())
    }

    private static String getBundlesInfoLineFor(String symbolicName, String version, String path, boolean isStarted) {
        symbolicName + "," + version + "," + "file:/" + path + "," + "4" + "," + isStarted + "\n"
    }
}
