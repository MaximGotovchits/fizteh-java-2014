package ru.fizteh.fivt.students.MaximGotovchits.Parallel;

public abstract class CommandsTools {
    static final String JSON_REG_EX = "\\s*,\\s*(?=(?:(?:[^\"]*\"){2})*[^\"]*$)"; // Removes commas
    // outside of "...".
    static String usingTableName = new String();
    static ObjectTable currentTableObject;
    static String dataBaseName = System.getProperty("fizteh.db.dir");
    static String signatureFileName = "signature.tsv";
    static Integer dirNum = 16;
    static Integer fileNum = 16;
    static String dirExt = ".dir";
    static String fileExt = ".dat";
    static int longestName = 260;
    static Boolean tableIsChosen = false;
}

