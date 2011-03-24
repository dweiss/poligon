package com.carrotsearch.apttest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

/**
 * Java6+ compatible annotation processor for parsing <code>Bindable</code>-annotated
 * types and generating their metadata.
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_5)
public class AptProcessor extends AbstractProcessor
{
    private int round;
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
        RoundEnvironment roundEnv)
    {
        // Check for any previous errors and skip.  
        if (roundEnv.errorRaised())
        {
            return false;
        }

        // Clear any previous junk.
        final long start = System.currentTimeMillis();

        // Scan for all types marked with @Bindable and processed in this round.
        int count = 0;
        for (TypeElement e : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(
            com.google.common.annotations.Beta.class)))
        {
            // e can be null in Eclipse, so check for this case.
            if (e == null) continue;
            try
            {
                processBindable(e);
            }
            catch (IOException ex)
            {
                processingEnv.getMessager().printMessage(Kind.ERROR, 
                    "Could not generate classes: " + ex.getMessage());
            }
            count++;
        }

        round++;
        if (count > 0)
        {
            System.out.println(
                String.format(Locale.ENGLISH,
                    "%d classes processed in round %d in %.2f secs.",
                    count,
                    round,
                    (System.currentTimeMillis() - start) / 1000.0));
        }

        return false;
    }

    private void processBindable(TypeElement e) throws IOException
    {
        Filer filer = processingEnv.getFiler();
        String clazzName = e.getQualifiedName().toString() + "Struct";
        PrintWriter w = new PrintWriter(filer.createSourceFile(
            clazzName, e).openWriter());
        
        Elements elements = processingEnv.getElementUtils();

        w.println("package " +
            elements.getPackageOf(e).getQualifiedName().toString() + ";");

        w.println("public class SimpleStruct { int a; }");
        
        w.close();
    }
}
