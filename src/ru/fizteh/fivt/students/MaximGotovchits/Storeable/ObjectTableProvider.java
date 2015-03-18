package ru.fizteh.fivt.students.MaximGotovchits.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ObjectTableProvider extends CommandsTools implements TableProvider {
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
        if (new File(rootDirectory + File.separator + name).exists()) {
            return new ObjectTable(rootDirectory + File.separator + name);
        }
        return null;
    }
    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException, IllegalArgumentException  {
        checkException(name);
        File file = new File(rootDirectory + File.separator + name);
        File signatureFile = new File(rootDirectory + File.separator + name + File.separator + signatureFileName);
        if (file.exists()) {
            System.out.println(name + " exists");
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
        return new ObjectTable(name, columnTypes);
    }
    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || name.length() > longestName) {
            throw new IllegalArgumentException();
        }
        String tableName = rootDirectory + File.separator + name;
        File toBeRemoved = new File(tableName);
        if (toBeRemoved.exists()) {
            if (tableIsChosen) {
                if (usingTableName.equals(name)) {
                    currentTableObject.storage.clear();
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
        value = value.replaceAll("^\\s*\\[\\s*|\\s*\\]\\s*$", ""); // Removing [ and ].
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
                for (Integer i = 0; i < dirNum; ++i) {
                    currentFile = rootDirectory + File.separator + sub.getName() + File.separator
                            + i + dirExt;
                    File file1 = new File(currentFile);
                    if (file1.exists()) {
                        for (Integer j = 0; j < fileNum; ++j) {
                            currentFile = rootDirectory + File.separator + sub.getName() + File.separator
                                    + i + dirExt + File.separator + j + fileExt;
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
                            + (recordsAmount + currentTableObject.storage.size()));
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
        if (name == null || name.length() > longestName) {
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