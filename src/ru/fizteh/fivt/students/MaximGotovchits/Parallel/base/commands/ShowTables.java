package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ShowTables extends Command {
    @Override
    public boolean execute(String[] cmd) {
        if (cmd.length == 2) {
            List<String> list = new ObjectTableProvider().getTableNames();
            String currentFile;
            int recordsAmount;
            File file = new File(CommandTools.DATA_BASE_NAME);
            for (File sub : file.listFiles()) {
                if ((!sub.isHidden()) && sub.isDirectory()) {
                    recordsAmount = 0;
                    for (Integer i = 0; i < CommandTools.DIR_NUM; ++i) {
                        currentFile = CommandTools.DATA_BASE_NAME + File.separator + sub.getName() + File.separator + i
                                + CommandTools.DIR_EXT;
                        File file1 = new File(currentFile);
                        if (file1.exists()) {
                            for (Integer j = 0; j < CommandTools.FILE_NUM; ++j) {
                                currentFile = CommandTools.DATA_BASE_NAME + File.separator + sub.getName()
                                        + File.separator + i + CommandTools.DIR_EXT + File.separator + j
                                        + CommandTools.FILE_EXT;
                                file1 = new File(currentFile);
                                try {
                                    if (file1.exists()) {
                                        DataInputStream stream = new DataInputStream(new FileInputStream(currentFile));
                                        byte[] data = new byte[(int) file1.length()];
                                        stream.read(data);
                                        String temp = new String(data, StandardCharsets.UTF_8);
                                        recordsAmount += (temp.length() - temp.replaceAll(" ", "").length()) / 4;
                                    }
                                } catch (FileNotFoundException e) {
                                    System.err.println(e);
                                } catch (IOException e) {
                                    System.err.println(e);
                                }
                            }
                        }
                    }
                    System.out.println(sub.getName() + " " + recordsAmount);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String getCmdName() {
        return "show";
    }
}
