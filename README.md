# static-analyzer
A static analysis tool to collect information about Open Source Java projects from GitHub

### Troubleshooting:
- To fix `None of the basic classes could be loaded. Check your soot classpath` related exception, add the `rt.jar` library from your `Program Files/Java/<JDK-VERSION>/jre/lib` folder as `classPathString` in line `Options.v().set_soot_classpath(classPathString)`
- But you still need the location of the project to analyze. To achieve that, prepare the `classPathString` using `String.join(File.pathSeparator, rootFolder, libraryFolder)` (`File.separator` is the OS friendly way to add seperator (`;`/`:`/etc.) in `classPathString`)
