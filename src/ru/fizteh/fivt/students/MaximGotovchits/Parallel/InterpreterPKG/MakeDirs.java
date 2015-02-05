package ru.fizteh.fivt.students.MaximGotovchits.Parallel.InterpreterPKG;

import java.io.File;

public class MakeDirs {
    public void makeDirsFunction() throws Exception {
        File file = new File(Interpreter.DATA_BASE_NAME);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            if (!file.isDirectory()) {
                System.err.println(Interpreter.DATA_BASE_NAME + " is not a directory");
                System.exit(1);
            } else {
                for (File sub : file.listFiles()) {
                    if (!sub.isDirectory() && !sub.isHidden()) {
                        System.err.println(Interpreter.DATA_BASE_NAME + File.separator
                                + sub.getName() + " is not a directory");
                    }
                }
            }
        }
    }
}
