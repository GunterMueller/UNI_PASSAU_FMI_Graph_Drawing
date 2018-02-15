package org.graffiti.plugins.scripting.doc;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graffiti.plugins.scripting.delegate.ScriptedField;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;

/**
 * Generator for automatically creating runtime help for the scripting system
 * from the javadoc. Simply add a {@code @scripted} tag to the doc comment of
 * your method annotated by {@link ScriptedMethod} and the text will be added to
 * the respective {@code .properties} file on the next run of {@code
 * DocumentationGenerator}. To run the documentation generator, execute the
 * {@link #main(String[])} method. It is recommended to increase the java heap
 * space for the run.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DocumentationGenerator {
    private static final Pattern NAME_PATTERN = Pattern
            .compile(".*\\\"(.*)\\\".*");
    private Map<String, DocumentationFile> map;

    private DocumentationGenerator(RootDoc root) {
        map = new HashMap<String, DocumentationFile>();
        for (ClassDoc classDoc : root.classes()) {
            processClass(classDoc);
        }
        for (DocumentationFile file : map.values()) {
            file.commit();
        }
    }

    public static boolean start(RootDoc root) {
        new DocumentationGenerator(root);
        return true;
    }

    public static void main(String[] args) {
        String[] javaDocArgs = { "-doclet",
                "org.graffiti.plugins.scripting.doc.DocumentationGenerator",
                "-docletpath", ".", "-sourcepath", "../Graffiti_Plugins/",
                "-subpackages", "org" };
        com.sun.tools.javadoc.Main.execute(javaDocArgs);
    }

    private void processClass(ClassDoc classDoc) {
        AnnotationDesc annotation = getAnnotation(classDoc.annotations(),
                DocumentedDelegate.class);
        if (annotation == null)
            return;
        String docClassName = extract(annotation, "value");
        DocumentationFile file = map.get(docClassName);
        if (file == null) {
            file = new DocumentationFile(docClassName);
            map.put(docClassName, file);
        }
        processClass(file, classDoc);
    }

    static AnnotationDesc getAnnotation(AnnotationDesc[] annotations,
            Class<? extends Annotation> annotationClass) {
        String annotationName = annotationClass.getCanonicalName();
        for (AnnotationDesc annotation : annotations) {
            AnnotationTypeDoc atd = annotation.annotationType();
            if (atd.qualifiedName().equals(annotationName))
                return annotation;
        }
        return null;
    }

    static String extract(AnnotationDesc annotation, String key) {
        for (ElementValuePair pair : annotation.elementValues()) {
            if (pair.element().name().equals(key))
                return pair.value().toString();
        }
        return null;
    }

    private void processClass(DocumentationFile file, ClassDoc classDoc) {
        String className = classDoc.qualifiedName();
        for (MethodDoc methodDoc : classDoc.methods()) {
            AnnotationDesc annotation = getAnnotation(methodDoc.annotations(),
                    ScriptedMethod.class);
            if (annotation == null) {
                continue;
            }
            String name = extract(annotation, "name");
            if (name == null || name.length() == 0) {
                name = methodDoc.name();
            }
            processElement(file, className, name, methodDoc);
        }
        for (FieldDoc fieldDoc : classDoc.fields()) {
            AnnotationDesc annotation = getAnnotation(fieldDoc.annotations(),
                    ScriptedField.class);
            if (annotation == null) {
                continue;
            }
            String namesStr = extract(annotation, "names");
            String[] names = namesStr == null ? new String[0] : namesStr
                    .split(",");
            if (names.length == 0) {
                processElement(file, className, fieldDoc.name(), fieldDoc);
            } else {
                for (String name : names) {
                    Matcher matcher = NAME_PATTERN.matcher(name);
                    if (!matcher.matches())
                        throw new RuntimeException(
                                "NAME_PATTERN does not match tag value.");
                    processElement(file, className, matcher.group(1), fieldDoc);
                }
            }
            System.out.println("Names: " + names);
        }
        processElement(file, className, null, classDoc);
    }

    private void processElement(DocumentationFile file, String className,
            String elementName, Doc doc) {
        Tag[] tags = doc.tags("scripted");
        if (tags.length == 0)
            return;
        List<String> lines = new LinkedList<String>();
        for (Tag tag : tags) {
            StringBuffer buffer = new StringBuffer();
            String[] parts = tag.text().split("\\n");
            if (parts.length > 0) {
                buffer.append(parts[0].trim());
            }
            for (int i = 1; i < parts.length; i++) {
                buffer.append(" ").append(parts[i].trim());
            }
            lines.add(buffer.toString());
        }

        String summary = null;
        StringBuffer buffer = new StringBuffer();
        Iterator<String> iter = lines.iterator();
        if (iter.hasNext()) {
            String line = iter.next();
            buffer.append(line);
            int index = line.indexOf('.');
            if (index == -1) {
                summary = line;
            } else {
                summary = line.substring(0, index + 1);
            }
        }
        if (summary == null) {
            summary = "";
        }
        while (iter.hasNext()) {
            buffer.append("\n").append(iter.next());
        }
        file.put(className, elementName, summary, buffer.toString());
    }
}
