package ru.fizteh.fivt.students.MaximGotovchits.Storeable;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public abstract class CommandsTools {
    static String usingTable;
    static ObjectTable currentTableObject;
    static String dataBaseName = System.getProperty("fizteh.db.dir");
    static String signatureFileName = "signature.tsv";
    static Integer dirNum = 16;
    static Integer fileNum = 16;
    static String dirExt = ".dir";
    static String fileExt = ".dat";
    static Map<String, ObjectStoreable> storage = new HashMap<String, ObjectStoreable>();
    static Map<String, ObjectStoreable> commitStorage = new HashMap<String, ObjectStoreable>();
    static Stack lastChanges = new Stack();
    static int longestName = 260;
    static int uncommitedChanges = 0;
    static Boolean tableIsChosen = false;
}


