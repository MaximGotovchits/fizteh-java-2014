package ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands.CommandTools;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ObjectTableProvider implements TableProvider {
    private static final Charset UTF = StandardCharsets.UTF_8;
    private static final int DIR_NUM = 16;
    private static final int FILE_NUM = 16;
    private static final String DIR_EXT = ".dir";
    private static final String FILE_EXT = ".dat";
    private static final String INCORRECT_SYMBOL = "^incorrect@";
    private static final String BRACES_KILLER = "^\\s*\\[\\s*|\\s*\\]\\s*$";
    private static final int LONGEST_NAME = 260;
    private static final String JSON_REG_EX = "\\s*,\\s*(?=(?:(?:[^\"]*\"){2})*[^\"]*$)";
    private static String usingTableName;
    private static ObjectTable currentTableObject;
    private static final String SIGNATURE_FILENAME = "signature.tsv";
    private static boolean tableIsChosen = false;
    private static String dataBaseName = System.getProperty("fizteh.db.dir");
    private volatile boolean writeSectionIsInUse = false;
    private String rootDirectory = "";
    public static final Map<Class<?>, Class<?>> ALLOWED_TYPES = new HashMap<Class<?>, Class<?>>();
    static {
        ALLOWED_TYPES.put(int.class, int.class);
        ALLOWED_TYPES.put(long.class, long.class);
        ALLOWED_TYPES.put(byte.class, byte.class);
        ALLOWED_TYPES.put(float.class, float.class);
        ALLOWED_TYPES.put(double.class, double.class);
        ALLOWED_TYPES.put(boolean.class, boolean.class);
        ALLOWED_TYPES.put(String.class, String.class);
        ALLOWED_TYPES.put(Integer.class, int.class);
        ALLOWED_TYPES.put(Long.class, long.class);
        ALLOWED_TYPES.put(Byte.class, byte.class);
        ALLOWED_TYPES.put(Float.class, float.class);
        ALLOWED_TYPES.put(Double.class, double.class);
        ALLOWED_TYPES.put(Boolean.class, boolean.class);
    }

    public ObjectTableProvider() {
        rootDirectory = dataBaseName;
    }


    public ObjectTableProvider(String dir) {
        dataBaseName = dir;
        rootDirectory = dir;
    }

    public int hashCode() {
        return Objects.hashCode(this.rootDirectory);
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public String getUsingTableName() {
        return usingTableName;
    }

    public String getSugnatureFilename() {
        return SIGNATURE_FILENAME;
    }

    @Override
    public boolean equals(Object obj) {
        ObjectTableProvider tempObj = (ObjectTableProvider) obj;
        return tempObj.rootDirectory.equals(this.rootDirectory);
    }
    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        checkException(name);
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        String tempPath = rootDirectory + File.separator + name;
        if (new File(tempPath).exists()) {
            readWriteLock.readLock().lock();
            ObjectTable tableToReturn = new ObjectTable(tempPath);
            readWriteLock.readLock().unlock();
            return tableToReturn;
        }
        return null;
    }
    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException, IllegalArgumentException  {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        if (writeSectionIsInUse) {
            return null;
        }
        writeSectionIsInUse = true;
        readWriteLock.writeLock().lock();
        checkException(name);
        File file = new File(rootDirectory + File.separator + name);
        File signatureFile = new File(rootDirectory + File.separator + name + File.separator + SIGNATURE_FILENAME);
        if (file.exists()) {
            return null;
        } else {
            file.mkdir();
            signatureFile.createNewFile();
            PrintWriter writer = new PrintWriter(signatureFile, UTF.toString());
            List<String> tempTypes = new LinkedList<>();
            for (Class<?> type : columnTypes) {
                if (type.equals(String.class)) {
                    tempTypes.add("String");
                } else {
                    tempTypes.add(type.getName());
                }
            }
            writer.println(String.join(" ", tempTypes));
            writer.close();
        }
        readWriteLock.writeLock().unlock();
        writeSectionIsInUse = false;
        return new ObjectTable(name, columnTypes);
    }
    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || name.length() > LONGEST_NAME) {
            throw new IllegalArgumentException();
        }
        String tableName = rootDirectory + File.separator + name;
        File toBeRemoved = new File(tableName);
        if (toBeRemoved.exists()) {
            if (tableIsChosen) {
                if (usingTableName.equals(name)) {
                    currentTableObject.getStorage().get().clear();
                    currentTableObject.getCommitStorage().clear();
                    tableIsChosen = false;
                    usingTableName = null;
                }
            }
            recRem(tableName);
            toBeRemoved.delete();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        ObjectTable usingTable = (ObjectTable) table;
        ObjectStoreable valueToReturn = new ObjectStoreable();
        String[] splittedValue = value.split("");
        if (!splittedValue[0].equals("[") || !splittedValue[splittedValue.length - 1].equals("]")) {
            throw new ParseException(value, 0);
        }
        valueToReturn.setSerialisedValue(value);
        value = value.replaceAll(BRACES_KILLER, ""); // Removing [ and ].
        String[] tempValue = value.split(JSON_REG_EX); // Split by comma.
        if (usingTable.getColumnsCount() != tempValue.length) {
            throw new ParseException(value, 0);
        }
        int index = 0;
        List<Class<?>> typeList = new LinkedList<>();
        for (String str : tempValue) {
            Object val = getValue(str, usingTable.getTypeKeeper().get(index));
            if (val.equals(INCORRECT_SYMBOL)) {
                throw new ParseException(value, 0);
            }
            valueToReturn.getSubValueList().add(val);
            typeList.add(val.getClass());
            ++index;
        }
        valueToReturn.setTypeKeeper(usingTable.getTypeKeeper());
        return valueToReturn;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        ObjectStoreable valueToSerialize = (ObjectStoreable) value;
        ObjectTable tableObj = (ObjectTable) table;
        int index = 0;
        for (Class<?> type : tableObj.getTypeKeeper()) {
            if (!type.equals(valueToSerialize.getTypeKeeper().get(index))) {
                throw new ColumnFormatException();
            }
            ++index;
        }
        return valueToSerialize.getSerialisedValue();
    }

    @Override
    public Storeable createFor(Table table) {
        return new ObjectStoreable((ObjectTable) table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        ObjectTable tempTable = new ObjectTable(table);
        List<Class<?>> valTypes;
        valTypes = convertToPrimitive(values);
        if (values.size() != tempTable.getColumnsCount()) {
            throw new IndexOutOfBoundsException();
        }
        if (!valTypes.equals(tempTable.getTypeKeeper())) {
            throw new ColumnFormatException();
        }
        ObjectStoreable toReturn = new ObjectStoreable(values);
        toReturn.setTypeKeeper(tempTable.getTypeKeeper());
        return toReturn;
    }

    @Override
    public List<String> getTableNames() {
        List<String> list = new LinkedList<>();
        File file = new File(rootDirectory);
        for (File sub : file.listFiles()) {
            if ((!sub.isHidden()) && sub.isDirectory()) {
                list.add(sub.getName());
            }
        }
        return list;
    }

    public int getChangesNumber() {
        return currentTableObject.getStorage().get().size() - currentTableObject.getCommitStorage().size();
    }

    public int getStorageSize() {
        return currentTableObject.getStorage().get().size();
    }

    public boolean changeUsingTable(String tableName, String oldTableName) throws IOException, ParseException {
        if (!tableName.equals(oldTableName)) {
            String outputName = tableName;
            String tablePath = CommandTools.DATA_BASE_NAME + File.separator + tableName;
            File file = new File(tablePath);
            if (file.exists()) {
                usingTableName = tableName;
                if (tableIsChosen) {
                    fillTable();
                    currentTableObject.getStorage().get().clear();
                    currentTableObject.getCommitStorage().clear();
                }
                currentTableObject = new ObjectTable(CommandTools.DATA_BASE_NAME + File.separator + usingTableName);
                for (Integer i = 0; i < DIR_NUM; ++i) {
                    for (Integer j = 0; j < FILE_NUM; ++j) {
                        tablePath = CommandTools.DATA_BASE_NAME + File.separator + tableName + File.separator
                                + i + DIR_EXT + File.separator + j + FILE_EXT;
                        if (new File(tablePath).exists()) {
                            fillStorage(tablePath);
                            PrintWriter writer = new PrintWriter(new File(tablePath));
                            writer.print("");
                            writer.close();
                        }
                    }
                }
                System.out.println("using " + outputName);
                tableIsChosen = true;
            } else {
                System.err.println(tableName + " not exists");
                return false;
            }
        } else {
            System.out.println("using " + oldTableName);
        }
        usingTableName = currentTableObject.getName();
        return true;
    }

    public List<String> getAdvancedTableNames() {
        String currentFile;
        int recordsAmount;
        List<String> tableNamesList = new LinkedList<>();
        File file = new File(CommandTools.DATA_BASE_NAME);
        for (File sub : file.listFiles()) {
            if ((!sub.isHidden()) && sub.isDirectory()) {
                recordsAmount = 0;
                for (Integer i = 0; i < DIR_NUM; ++i) {
                    currentFile = CommandTools.DATA_BASE_NAME + File.separator + sub.getName() + File.separator + i
                            + DIR_EXT;
                    File file1 = new File(currentFile);
                    if (file1.exists()) {
                        for (Integer j = 0; j < FILE_NUM; ++j) {
                            currentFile = CommandTools.DATA_BASE_NAME + File.separator + sub.getName() + File.separator
                                    + i + DIR_EXT + File.separator + j + FILE_EXT;
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
                tableNamesList.add(sub.getName() + " " + recordsAmount);
            }
        }
        return tableNamesList;
    }

    private Object getValue(String str, Class<?> expectedType) throws NumberFormatException {
        if (expectedType.equals(int.class)) { // Тут нельзя заменять на обращение к мапу.c
            if (str.equals("null")) {
                return null;
            }
            Integer toReturn = Integer.parseInt(str);
            return toReturn;
        }
        if (expectedType.equals(long.class)) {
            if (str.equals("null")) {
                return null;
            }
            Long toReturn = Long.parseLong(str);
            return toReturn;
        }
        if (expectedType.equals(boolean.class)) {
            if (str.equals("null")) {
                return null;
            }
            Boolean toReturn = Boolean.parseBoolean(str);
            return toReturn;
        }
        if (expectedType.equals(String.class)) {
            if (str.equals("null")) {
                return null;
            }
            String[] tmp = str.split("");
            if (tmp[0].equals("\"") && tmp[tmp.length - 1].equals("\"")) {
                return str;
            } else {
                throw new NumberFormatException();
            }
        }
        if (expectedType.equals(byte.class)) {
            if (str.equals("null")) {
                return null;
            }
            Byte toReturn = Byte.parseByte(str);
            return toReturn;
        }
        if (expectedType.equals(double.class)) {
            if (str.equals("null")) {
                return null;
            }
            Double toReturn = Double.parseDouble(str);
            return toReturn;
        }
        if (expectedType.equals(float.class)) {
            if (str.equals("null")) {
                return null;
            }
            Float toReturn = Float.parseFloat(str);
            return toReturn;
        }
        return "^incorrect@";
    }




    boolean isNull(String str) {
        if (str.equals("null")) {
            return true;
        }
        return false;
    }

    private void recRem(String myFile) {
        File file = new File(myFile);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recRem(f.getAbsolutePath());
            }
        }
        file.delete();
    }

    public void checkException(String name) throws IllegalArgumentException {
        if (name == null || name.length() > LONGEST_NAME) {
            throw new IllegalArgumentException();
        }
    }

    private List<Class<?>> convertToPrimitive(List<?> list) {
        List<Class<?>> toReturn = new LinkedList<>();
        for (Object object : list) {
            toReturn.add(ALLOWED_TYPES.get(object.getClass()));
            continue;
        }
        return toReturn;
    }

    public static void fillTable() {
        for (Map.Entry<String, ObjectStoreable> entry : currentTableObject
                .getCommitStorage().entrySet()) {
            int hashCode = entry.getKey().hashCode();
            Integer nDirectory = hashCode % DIR_NUM;
            Integer nFile = hashCode / DIR_NUM % FILE_NUM;
            Path fileName = Paths.get(dataBaseName, currentTableObject.getName(),
                    nDirectory + DIR_EXT);
            File file = new File(fileName.toString());
            if (!file.exists()) {
                file.mkdir();
            }
            fileName = Paths.get(fileName.toString(), nFile + FILE_EXT);
            file = new File(fileName.toString());
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                byte[] bytesKey = (" " + entry.getKey() + " ").getBytes(UTF);
                DataOutputStream stream = new DataOutputStream(new FileOutputStream(fileName.toString(), true));
                stream.write(bytesKey.length);
                stream.write(bytesKey);
                byte[] bytesVal = ((" " + entry.getValue().getSerialisedValue() + " ").getBytes(UTF));
                stream.write(bytesVal.length - 1);
                stream.write(bytesVal);
                stream.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    public static void fillStorage(String datName) throws IOException, ParseException {
        DataInputStream stream = new DataInputStream(new FileInputStream(datName));
        File file = new File(datName);
        byte[] data = new byte[(int) file.length()];
        stream.read(data);
        int counter = 0;
        int offset;
        String keyForMap;
        String value ;
        while (counter < file.length()) {
            offset = data[counter];
            keyForMap = new String(data, counter + 2, offset - 2, UTF);
            counter = counter + offset + 1;
            offset = data[counter];
            value = new String(data, counter + 2,  data.length - counter - 3, UTF);
            value = value.replaceAll("^\\s*|\\s*$", "");
            String tableName = new File(new File(datName).getParent()).getParent();
            ObjectStoreable valForMap = (ObjectStoreable)
                    new ObjectTableProvider().deserialize(new ObjectTable(tableName), value);
            currentTableObject.getStorage().get().put(keyForMap, valForMap);
            currentTableObject.getCommitStorage().put(keyForMap, valForMap);
            counter = counter + offset + 3;
        }
        stream.close();
    }
}

/*
package ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects;

        import ru.fizteh.fivt.storage.structured.ColumnFormatException;
        import ru.fizteh.fivt.storage.structured.Storeable;
        import ru.fizteh.fivt.storage.structured.Table;
        import ru.fizteh.fivt.storage.structured.TableProvider;
        import java.io.*;
        import java.nio.charset.StandardCharsets;
        import java.text.ParseException;
        import java.util.*;
        import java.util.concurrent.locks.ReadWriteLock;
        import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ObjectTableProvider implements TableProvider {
    static final Map<Class<?>, Class<?>> TYPES_MAP = new HashMap<Class<?>, Class<?>>() {
        {
            TYPES_MAP.put(int.class, int.class);
            TYPES_MAP.put(long.class, long.class);
            TYPES_MAP.put(byte.class, byte.class);
            TYPES_MAP.put(float.class, float.class);
            TYPES_MAP.put(double.class, double.class);
            TYPES_MAP.put(boolean.class, boolean.class);
            TYPES_MAP.put(String.class, String.class);
        }
    };
    private static final String BRACES_KILLER = "^\\s*\\[\\s*|\\s*\\]\\s*$";
    public static final String FILE_EXT = ".dat";
    public static final Integer FILE_NUM = 16;
    public static final String DIR_EXT = ".dir";
    public static final Integer DIR_NUM = 16;
    private static final String JSON_REG_EX = "\\s*,\\s*(?=(?:(?:[^\"]*\"){2})*[^\"]*$)";
    public static String usingTableName;
    public static ObjectTable currentTableObject;
    static final String SIGNATURE_FILENAME = "signature.tsv";
    private static Boolean tableIsChosen = false;
    public static String dataBaseName = System.getProperty("fizteh.db.dir");
    private volatile boolean writeSectionIsInUse = false;
    private String rootDirectory = "";

    public ObjectTableProvider() {
        rootDirectory = dataBaseName;
    }

    public ObjectTableProvider(String dir) {
        dataBaseName = dir;
        rootDirectory = dir;
    }

    public int hashCode() {
        return Objects.hashCode(this.rootDirectory);
    }

    @Override
    public boolean equals(Object obj) {
        ObjectTableProvider tempObj = (ObjectTableProvider) obj;
        return tempObj.rootDirectory.equals(this.rootDirectory);
    }
    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        checkException(name);
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        if (new File(rootDirectory + File.separator + name).exists()) {
            readWriteLock.readLock().lock();
            ObjectTable tableToReturn;
            try {
                tableToReturn = new ObjectTable(rootDirectory + File.separator + name);
            } catch (IOException e) {
                return null;
            }
            readWriteLock.readLock().unlock();
            return tableToReturn;
        }
        return null;
    }
    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException, IllegalArgumentException  {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        if (writeSectionIsInUse) {
            return null;
        }
        writeSectionIsInUse = true;
        readWriteLock.writeLock().lock();
        checkException(name);
        File file = new File(rootDirectory + File.separator + name);
        File signatureFile = new File(rootDirectory + File.separator + name + File.separator + SIGNATURE_FILENAME);
        if (file.exists()) {
            readWriteLock.writeLock().unlock();
            return new ObjectTable(name, columnTypes);
        } else {
            file.mkdir();
            signatureFile.createNewFile();
            PrintWriter writer = new PrintWriter(signatureFile, "UTF-8");
            String tempTypes = new String();
            for (Class<?> type : columnTypes) {
                if (type.equals(String.class)) {
                    tempTypes += "String ";
                } else {
                    tempTypes += type.getName() + " ";
                }
            }
            tempTypes = tempTypes.substring(0, tempTypes.length() - 1);
            writer.println(tempTypes);
            writer.close();
            System.out.println("created");
        }
        writeSectionIsInUse = false;
        return new ObjectTable(name, columnTypes);
    }
    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || name.length() > ObjectTableProviderFactory.LONGEST_NAME) {
            throw new IllegalArgumentException();
        }
        String tableName = rootDirectory + File.separator + name;
        File toBeRemoved = new File(tableName);
        if (toBeRemoved.exists()) {
            if (tableIsChosen) {
                if (usingTableName.equals(name)) {
                    currentTableObject.storage.get().clear();
                    currentTableObject.commitStorage.clear();
                    tableIsChosen = false;
                    usingTableName = null;
                }
            }
            recRem(tableName);
            toBeRemoved.delete();
            System.out.println("dropped");
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        ObjectTable usingTable = (ObjectTable) table;
        ObjectStoreable valueToReturn = new ObjectStoreable();
        String[] splittedValue = value.split("");
        if (!splittedValue[0].equals("[") || !splittedValue[splittedValue.length - 1].equals("]")) {
            throw new ParseException(value, 0);
        }
        valueToReturn.serialisedValue = value;
        value = value.replaceAll(BRACES_KILLER, ""); // Removing [ and ].
        String[] tempValue = value.split(JSON_REG_EX); // Split by comma.
        if (usingTable.getColumnsCount() != tempValue.length) {
            throw new ParseException(value, 0);
        }
        int index = 0;
        List<Class<?>> typeList = new LinkedList<>();
        for (String str : tempValue) {
            Object val = getValue(str, usingTable, usingTable.typeKeeper.get(index));
            if (val.equals("^incorrect$")) {
                throw new ParseException(value, 0);
            }
            valueToReturn.subValueList.add(val);
            typeList.add(val.getClass());
            ++index;
        }
        valueToReturn.typeKeeper = usingTable.typeKeeper;
        return valueToReturn;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        ObjectStoreable valueToSerialize = (ObjectStoreable) value;
        ObjectTable tableObj = (ObjectTable) table;
        int index = 0;
        for (Class<?> type : tableObj.typeKeeper) {
            if (!type.equals(valueToSerialize.typeKeeper.get(index))) {
                throw new ColumnFormatException();
            }
            ++index;
        }
        return valueToSerialize.serialisedValue;
    }

    @Override
    public Storeable createFor(Table table) {
        return new ObjectStoreable((ObjectTable) table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        int ind = 0;
        ObjectTable tempTable = new ObjectTable(table);
        List<Class<?>> valTypes = new LinkedList<Class<?>>();
        valTypes = convertToPrimitive(values);
        if (values.size() != tempTable.getColumnsCount()) {
            throw new IndexOutOfBoundsException();
        }
        if (!valTypes.equals(tempTable.typeKeeper)) {
            throw new ColumnFormatException();
        }
        ObjectStoreable toReturn = new ObjectStoreable(values);
        toReturn.typeKeeper = tempTable.typeKeeper;
        return toReturn;
    }

    @Override
    public List<String> getTableNames() {
        List<String> list = new LinkedList<String>();
        String currentFile = new String();
        int recordsAmount = 0;
        File file = new File(rootDirectory);
        for (File sub : file.listFiles()) {
            if ((!sub.isHidden()) && sub.isDirectory()) {
                recordsAmount = 0;
                for (Integer i = 0; i < DIR_NUM; ++i) {
                    currentFile = rootDirectory + File.separator + sub.getName() + File.separator
                            + i + DIR_EXT;
                    File file1 = new File(currentFile);
                    if (file1.exists()) {
                        for (Integer j = 0; j < FILE_NUM; ++j) {
                            currentFile = rootDirectory + File.separator + sub.getName() + File.separator
                                    + i + DIR_EXT + File.separator + j + FILE_EXT;
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
                                return null;
                            } catch (IOException e) {
                                return null;
                            }
                        }
                    }
                }
                currentFile = new File(new File(new File(currentFile).getParent()).getParent()).getName();
                list.add(sub.getName());
                if (sub.getName().equals(usingTableName)) {
                    System.out.println(sub.getName() + " "
                            + (recordsAmount + currentTableObject.storage.get().size()));
                } else {
                    System.out.println(sub.getName() + " " + recordsAmount);
                }
            }
        }
        return list;
    }

    private Object getValue(String str, ObjectTable usingTable, Class<?> expectedType) throws NumberFormatException {
        for (Class<?> type : usingTable.typeKeeper) {
            if (expectedType.equals(int.class)) {
                if (str.equals("null")) {
                    return null;
                }
                Integer toReturn = Integer.parseInt(str);
                return toReturn;
            }
            if (expectedType.equals(long.class)) {
                if (str.equals("null")) {
                    return null;
                }
                Long toReturn = Long.parseLong(str);
                return toReturn;
            }
            if (expectedType.equals(boolean.class)) {
                if (str.equals("null")) {
                    return null;
                }
                Boolean toReturn = Boolean.parseBoolean(str);
                return toReturn;
            }
            if (expectedType.equals(String.class)) {
                if (str.equals("null")) {
                    return null;
                }
                String[] tmp = str.split("");
                if (tmp[0].equals("\"") && tmp[tmp.length - 1].equals("\"")) {
                    return str;
                } else {
                    throw new NumberFormatException();
                }
            }
            if (expectedType.equals(byte.class)) {
                if (str.equals("null")) {
                    return null;
                }
                Byte toReturn = Byte.parseByte(str);
                return toReturn;
            }
            if (expectedType.equals(double.class)) {
                if (str.equals("null")) {
                    return null;
                }
                Double toReturn = Double.parseDouble(str);
                return toReturn;
            }
            if (expectedType.equals(float.class)) {
                if (str.equals("null")) {
                    return null;
                }
                Float toReturn = Float.parseFloat(str);
                return toReturn;
            }
        }
        return "^incorrect@";
    }

    void recRem(String myFile) {
        File file = new File(myFile);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recRem(f.getAbsolutePath());
            }
        }
        file.delete();
    }

    public void checkException(String name) throws IllegalArgumentException {
        if (name == null || name.length() > ObjectTableProviderFactory.LONGEST_NAME) {
            throw new IllegalArgumentException();
        }
    }

    private List<Class<?>> convertToPrimitive(List<?> list) {
        List<Class<?>> toReturn = new LinkedList<Class<?>>();
        for (Object object : list) {
            toReturn.add(TYPES_MAP.get(object));
            continue;
        }
        return toReturn;
    }

    private void initialization() {
        tableIsChosen = false;
        dataBaseName = System.getProperty("fizteh.db.dir");
        writeSectionIsInUse = false;
        rootDirectory = "";
    }
}
*/
