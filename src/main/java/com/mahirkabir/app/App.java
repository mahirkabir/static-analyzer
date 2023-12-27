package com.mahirkabir.app;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeStmt;
import soot.jimple.JimpleBody;
import soot.options.Options;

public class App {
    public static String sourceDirectory = System.getProperty("user.dir") + File.separator + "analysis";
    public static String clsName = "HelloWorld";

    public static void setupSoot() {
        G.reset();
        Options.v().set_allow_phantom_refs(true);
        String classPathString = String.join(
                File.pathSeparator,
                sourceDirectory,
                Paths.get("lib", "rt.jar").toString());
        Options.v().set_soot_classpath(classPathString);
        SootClass sc = Scene.v().loadClassAndSupport(clsName);
        sc.setApplicationClass();
        Scene.v().loadNecessaryClasses();
    }

    public static boolean invokesMethod(Unit u, String methodSubsignature, String classSignature) {
        AtomicBoolean result = new AtomicBoolean(false);
        u.apply(new AbstractStmtSwitch() {
            @Override
            public void caseInvokeStmt(InvokeStmt invokeStmt) {
                String invokedSubsignature = invokeStmt.getInvokeExpr().getMethod().getSubSignature();
                String invokedClassSignature = invokeStmt.getInvokeExpr().getMethod().getDeclaringClass().getName();
                if (invokedSubsignature.equals(methodSubsignature)) {
                    if (classSignature == null || invokedClassSignature.equals(classSignature)) {
                        result.set(true);
                    }
                }

            }
        });
        return result.get();
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Need method signature and usage class signature");
            return;
        }
        setupSoot();

        String methodSignature = args[0];
        String classSignature = args[1];

        SootClass mainClass = Scene.v().getSootClass(clsName);
        for (SootMethod sm : mainClass.getMethods()) {
            JimpleBody body = (JimpleBody) sm.retrieveActiveBody();

            List<Unit> usages = new ArrayList<>();
            for (Iterator<Unit> it = body.getUnits().snapshotIterator(); it.hasNext();) {
                Unit u = it.next();
                if (invokesMethod(u, methodSignature, classSignature))
                    usages.add(u);

            }
            if (usages.size() > 0) {
                System.out.println("Method: " + sm.getSignature() + " has been invoked: " + usages.size() + " time(s)");
                for (Unit u : usages) {
                    System.out.println("\t" + u.toString());
                }
            }
        }
    }
}