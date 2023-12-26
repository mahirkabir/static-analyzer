package com.mahirkabir.app;

import java.util.List;

import sootup.core.Project;
import sootup.core.cache.provider.LRUCacheProvider;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.SootClass;
import sootup.core.types.ClassType;
import sootup.core.views.View;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaProject;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootClassSource;
import sootup.java.core.language.JavaLanguage;

public class App {
    public static void main(String[] args) {
        AnalysisInputLocation<JavaSootClass> inputLocation = new JavaClassPathAnalysisInputLocation("analysis");

        JavaLanguage language = new JavaLanguage(15);

        Project project = JavaProject.builder(language)
                .addInputLocation(inputLocation).build();

        ClassType classType = project.getIdentifierFactory().getClassType("HelloWorld");

        View view = project.createView(new LRUCacheProvider(50));

        SootClass<JavaSootClassSource> sootClass = (SootClass<JavaSootClassSource>) view
                .getClass(classType).get();

        sootClass.getMethods().forEach(sootMethod -> {
            System.out.println("Details about method: " + sootMethod.getName());
            System.out.println("==================");
            List<Stmt> stmnts = sootMethod.getBody().getStmts();
            stmnts.forEach(stmnt -> {
                System.out.println(stmnt.toString());
                System.out.println("------------------");
            });
            System.out.println("==================");
        });
    }
}