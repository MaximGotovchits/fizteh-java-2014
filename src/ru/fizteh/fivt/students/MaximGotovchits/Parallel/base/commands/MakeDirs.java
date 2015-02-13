package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;

import java.io.File;

public class MakeDirs extends Command {
    @Override
    public boolean execute(String[] cmd) throws Exception {
        File file = new File(CommadTools.DATA_BASE_NAME);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            if (!file.isDirectory()) {
                System.err.println(CommadTools.DATA_BASE_NAME + " is not a directory");
                System.exit(1);
            } else {
                for (File sub : file.listFiles()) {
                    if (!sub.isDirectory() && !sub.isHidden()) {
                        System.err.println(CommadTools.DATA_BASE_NAME + File.separator
                                + sub.getName() + " is not a directory");
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String getCmdName() {
        return "make";
    }
}
