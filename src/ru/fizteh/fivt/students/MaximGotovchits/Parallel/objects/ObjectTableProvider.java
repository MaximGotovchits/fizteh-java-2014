package ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import java.io.*;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ObjectTableProvider implements TableProvider {
    private static final String INCORRECT_SYMBOL = "^incorrect@";
    private static final String BRACES_KILLER = "^\\s*\\[\\s*|\\s*\\]\\s*$";
    private static final String JSON_REG_EX = "\\s*,\\s*(?=(?:(?:[^\"]*\"){2})*[^\"]*$)";
    private static String usingTableName;
    private static ObjectTable currentTableObject;
    private static final int LONGEST_NAME = 260;
    private static final String SIGNATURE_FILENAME = "signature.tsv";
    private static Boolean tableIsChosen = false;
    private static String dataBaseName = System.getProperty("fizteh.db.dir");
    private volatile boolean writeSectionIsInUse = false;
    private String rootDirectory = new String();

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
                    currentTableObject.storage.get().clear();
                    currentTableObject.commitStorage.clear();
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
        valueToReturn.serialisedValue = value;
        value = value.replaceAll(BRACES_KILLER, ""); // Removing [ and ].
        String[] tempValue = value.split(JSON_REG_EX); // Split by comma.
        if (usingTable.getColumnsCount() != tempValue.length) {
            throw new ParseException(value, 0);
        }
        int index = 0;
        List<Class<?>> typeList = new LinkedList<>();
        for (String str : tempValue) {
            Object val = getValue(str/*, usingTable*/, usingTable.typeKeeper.get(index));
            if (val.equals(INCORRECT_SYMBOL)) {
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
        ObjectTable tempTable = new ObjectTable(table);
        List<Class<?>> valTypes;
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
        List<String> list = new LinkedList<>();
        File file = new File(rootDirectory);
        for (File sub : file.listFiles()) {
            if ((!sub.isHidden()) && sub.isDirectory()) {
                list.add(sub.getName());
            }
        }
        return list;
    }

    private Object getValue(String str, Class<?> expectedType)
            throws NumberFormatException {
        if (expectedType.equals(int.class)) {
            if (isNull(str)) {
                return null;
            }
            Integer toReturn = Integer.parseInt(str);
            return toReturn;
        }
        if (expectedType.equals(long.class)) {
            if (isNull(str)) {
                return null;
            }
            Long toReturn = Long.parseLong(str);
            return toReturn;
        }
        if (expectedType.equals(boolean.class)) {
            if (isNull(str)) {
                return null;
            }
            Boolean toReturn = Boolean.parseBoolean(str);
            return toReturn;
        }
        if (expectedType.equals(String.class)) {
            if (isNull(str)) {
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
            if (isNull(str)) {
                return null;
            }
            Byte toReturn = Byte.parseByte(str);
            return toReturn;
        }
        if (expectedType.equals(double.class)) {
            if (isNull(str)) {
                return null;
            }
            Double toReturn = Double.parseDouble(str);
            return toReturn;
        }
        if (expectedType.equals(float.class)) {
            if (isNull(str)) {
                return null;
            }
            Float toReturn = Float.parseFloat(str);
            return toReturn;
        }
        return INCORRECT_SYMBOL;
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
        List<Class<?>> toReturn = new LinkedList<Class<?>>();
        for (Object object : list) {
            if (object.getClass().equals(Integer.class)) {
                toReturn.add(int.class);
                continue;
            }
            if (object.getClass().equals(Long.class)) {
                toReturn.add(long.class);
                continue;
            }
            if (object.getClass().equals(Boolean.class)) {
                toReturn.add(boolean.class);
                continue;
            }
            if (object.getClass().equals(String.class)) {
                toReturn.add(String.class);
                continue;
            }
            if (object.getClass().equals(Byte.class)) {
                toReturn.add(byte.class);
                continue;
            }
            if (object.getClass().equals(Double.class)) {
                toReturn.add(double.class);
                continue;
            }
            if (object.getClass().equals(Float.class)) {
                toReturn.add(float.class);
                continue;
            }
        }
        return toReturn;
    }
}
