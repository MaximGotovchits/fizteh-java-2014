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
    private String rootDirectory; // Только ради тестов. Или нет...
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
        if (tempObj.rootDirectory.equals(this.rootDirectory)) {
            return true;
        }
        return false;
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
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
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
        try {
            if (name == null || name.length() > longestName) {
                throw new IllegalArgumentException();
            }
            String tableName = rootDirectory + File.separator + name;
            File toBeRemoved = new File(tableName);
            if (toBeRemoved.exists()) {
                if (tableIsChosen) {
                    if (usingTable.equals(name)) {
                        storage.clear();
                        commitStorage.clear();
                        uncommitedChanges = 0;
                        tableIsChosen = false;
                        usingTable = null;
                    }
                }
                recRem(tableName);
                toBeRemoved.delete();
                System.out.println("dropped");
            } else {
                throw new IllegalStateException();
            }
        } catch (IllegalArgumentException s) {
            System.err.println(s);
            return;
        } catch (IllegalStateException s) {
            System.err.println(s);
            return;
        }
    }
    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (!checkCorrectness(value)) {
            return null;
        }
        try {
            ObjectStoreable tempStoreable = new ObjectStoreable();
            ObjectTable tempTable = (ObjectTable) table;
            String[] toBeParsed = value.split("");
            if (!toBeParsed[0].equals("[")) {
                throw new ParseException(value, 0);
            }
            if (!toBeParsed[toBeParsed.length - 1].equals("]")) {
                throw new ParseException(value, toBeParsed.length - 1);
            }
            int writingIndex = 0;
            int readingIndex = 1;
            boolean detector = false; // Detects if JSON format is incorrect.
            while (readingIndex < toBeParsed.length - 1) { // Parsing process.
                detector = false;
                if (toBeParsed[readingIndex].equals("\"")) { // String is found.
                    detector = true;
                    readingIndex = parseString(tempTable, toBeParsed, readingIndex, writingIndex, tempStoreable);
                    readingIndex += 3;
                    ++writingIndex;
                    if (whetherIsParsed(toBeParsed, readingIndex)) { // Checks if parsing has to be stopped immediately.
                        break;
                    }
                }
                if (toBeParsed[readingIndex].equals("t") || toBeParsed[readingIndex].equals("f")) { // Boolean is found.
                    detector = true;
                    readingIndex = parseBoolean(tempTable, toBeParsed, readingIndex, writingIndex, tempStoreable);
                    readingIndex += 2;
                    ++writingIndex;
                    if (whetherIsParsed(toBeParsed, readingIndex)) {
                        break;
                    }
                }
                if (toBeParsed[readingIndex].equals("n")) { // Null is found.
                    detector = true;
                    parseNull(tempTable, toBeParsed, readingIndex, writingIndex, tempStoreable);
                    readingIndex += 2;
                    ++writingIndex;
                    if (whetherIsParsed(toBeParsed, readingIndex)) {
                        break;
                    }
                }
                if (isNumber(toBeParsed[readingIndex])) {
                    detector = true;
                    readingIndex = parseNumber(tempTable, toBeParsed, readingIndex, writingIndex, tempStoreable);
                    if (!toBeParsed[readingIndex].equals(",") && !toBeParsed[readingIndex].equals("]")) {
                        throw new ParseException(value, readingIndex);
                    }
                    readingIndex += 2;
                    ++writingIndex;
                    if (whetherIsParsed(toBeParsed, readingIndex)) {
                        break;
                    }
                }
                if (!detector) {
                    throw new ParseException(value, readingIndex);
                }
            }
            tempStoreable.serialisedValue = value;
            return tempStoreable;
        } catch (ParseException s) {
            System.err.println(s);
            return null;
        }
    }
    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        ObjectStoreable valueToSerialize = (ObjectStoreable) value;
        ObjectTable tableObj = (ObjectTable) table;
        try {
            int index = 0;
            for (Class<?> type : tableObj.typeKeeper) {
                if (!type.equals(valueToSerialize.typeKeeper.get(index))) {
                    throw new ColumnFormatException();
                }
                ++index;
            }
        } catch (ColumnFormatException s) {
            System.out.println(s);
            return null;
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
        try {
            List<Class<?>> valTypes = new LinkedList<Class<?>>();
            valTypes = convertToPrimitive(values);
            if (values.size() != tempTable.getColumnsCount()) {
                throw new IndexOutOfBoundsException();
            }
            Class<?> tableType;
            Class<?> valueType;
            if (!valTypes.equals(tempTable.typeKeeper)) {
                throw new ColumnFormatException();
            }
        } catch (ColumnFormatException s) {
            System.err.println(s);
            return null;
        } catch (IndexOutOfBoundsException s) {
            System.err.println(s);
            return null;
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
                        } catch (IOException s) {
                            return null;
                        }
                    }
                }
            }
            currentFile = new File(new File(new File(currentFile).getParent()).getParent()).getName();
            if (!sub.isHidden() && sub.isDirectory()) {
                list.add(sub.getName());
                if (sub.getName().equals(usingTable)) {
                    System.out.println(sub.getName() + " " + (recordsAmount + storage.size()));
                } else {
                    System.out.println(sub.getName() + " " + recordsAmount);
                }
            }
        }
        return list;
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
    
    public Object checkException(String name) throws IllegalArgumentException {
        try {
            if (name == null || name.length() > longestName) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException s) {
            System.err.println(s);
            return null;
        }
        return null;
    }
    private boolean whetherIsParsed(String[] toBeParsed, int readingIndex) {
        if (toBeParsed.length - readingIndex < 3) {
            return true;
        }
        return false;
    }
    private int parseString(ObjectTable table, String[] toBeParsed,
                            int readingIndex, int writingIndex, ObjectStoreable tempStoreable) {
        ++readingIndex;
        String toAdd = new String();
        while (!toBeParsed[readingIndex].equals("\"")) {
            toAdd += toBeParsed[readingIndex];
            ++readingIndex;
        }
        tempStoreable.typeKeeper.add(writingIndex, String.class);
        tempStoreable.subValueList.add(writingIndex, "\"" + toAdd + "\"");
        return readingIndex;
    }
    
    private int parseBoolean(ObjectTable table, String[] toBeParsed,
                             int readingIndex, int writingIndex, ObjectStoreable tempStoreable) {
        if (toBeParsed[readingIndex].equals("t")) {
            tempStoreable.subValueList.add(writingIndex, true);
            readingIndex += 4;
        } else {
            tempStoreable.subValueList.add(writingIndex, false);
            readingIndex += 5;
        }
        tempStoreable.typeKeeper.add(writingIndex, boolean.class);
        return readingIndex;
    }
    private int parseNull(ObjectTable table, String[] toBeParsed,
                          int readingIndex, int writingIndex, ObjectStoreable tempStoreable) {
        tempStoreable.subValueList.add(writingIndex, null);
        readingIndex += 4;
        tempStoreable.typeKeeper.add(writingIndex, Object.class);
        return readingIndex;
    }
    private int parseNumber(ObjectTable table, String[] toBeParsed,
                            int readingIndex, int writingIndex, ObjectStoreable tempStoreable) {
        int firstIndex = readingIndex;
        int lastIndex = readingIndex;
        boolean isDouble = false;
        boolean isByte = false;
        while ((!toBeParsed[lastIndex].equals("]")) && (!toBeParsed[lastIndex].equals(","))) {
            // Checks if it's a boolean.
            if (toBeParsed[lastIndex].equals("x")) {
                if (isDouble) {
                    return lastIndex;
                }
                isByte = true;
                ++lastIndex;
                continue;
            }
            if (toBeParsed[lastIndex].equals(".") && !isByte) {
                if (isByte) {
                    return lastIndex;
                }
                isDouble = true;
                ++lastIndex;
                continue;
            }
            ++lastIndex;
        }
        String temp = new String();
        for (int i = firstIndex; i < lastIndex; ++i) {
            temp += toBeParsed[i];
        }
        firstIndex = lastIndex + 2;
        if (isByte) { // Is Byte.
            tempStoreable.subValueList.add(writingIndex, Byte.parseByte(temp));
            tempStoreable.typeKeeper.add(writingIndex, byte.class);
            return lastIndex;
        }
        if (isDouble) {
            try { // http://bit.ly/1v9GqBT
                if (table != null) {
                    if (table.typeKeeper.get(writingIndex).equals(double.class)) {
                        tempStoreable.subValueList.add(writingIndex, Double.parseDouble(temp));
                        tempStoreable.typeKeeper.add(writingIndex, double.class);
                        return lastIndex;
                    }
                    if (table.typeKeeper.get(writingIndex).equals(float.class)) {
                        tempStoreable.subValueList.add(writingIndex, Float.parseFloat(temp));
                        tempStoreable.typeKeeper.add(writingIndex, float.class);
                        return lastIndex;
                    }
                }
                Float.parseFloat(temp);
            } catch (NumberFormatException s) {
                tempStoreable.subValueList.add(writingIndex, Double.parseDouble(temp));
                tempStoreable.typeKeeper.add(writingIndex, double.class);
                return lastIndex;
            }
            tempStoreable.subValueList.add(writingIndex, Float.parseFloat(temp));
            tempStoreable.typeKeeper.add(writingIndex, float.class);
            return lastIndex;
        } else { // Int or long is found.
            try {
                if (table != null) {
                    if (table.typeKeeper.get(writingIndex).equals(int.class)) {
                        tempStoreable.subValueList.add(writingIndex, Integer.parseInt(temp));
                        tempStoreable.typeKeeper.add(writingIndex, int.class);
                        return lastIndex;
                    }
                    if (table.typeKeeper.get(writingIndex).equals(long.class)) {
                        tempStoreable.subValueList.add(writingIndex, Long.parseLong(temp));
                        tempStoreable.typeKeeper.add(writingIndex, long.class);
                        return lastIndex;
                    }
                    if (table.typeKeeper.get(writingIndex).equals(byte.class)) {
                        tempStoreable.subValueList.add(writingIndex, Byte.parseByte(temp));
                        tempStoreable.typeKeeper.add(writingIndex, byte.class);
                        return lastIndex;
                    }
                }
                Integer.parseInt(temp);
            } catch (NumberFormatException s) {
                tempStoreable.subValueList.add(writingIndex, Double.parseDouble(temp));
                tempStoreable.typeKeeper.add(writingIndex, double.class);
                return lastIndex;
            }
            tempStoreable.subValueList.add(writingIndex, Float.parseFloat(temp));
            tempStoreable.typeKeeper.add(writingIndex, float.class);
            return lastIndex;
        }
    }
    
    private boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    private boolean checkCorrectness(String value) {
        boolean byteIsUsed = false;
        boolean dotIsUsed = false;
        String[] valueList = value.split("");
        if (!valueList[0].equals("[") || !valueList[valueList.length - 1].equals("]")) {
            return false;
        }
        if (valueList.length == 2 && valueList[1].equals("]")) {
            return true;
        }
        if (valueList[1].equals(" ")) {
            return false;
        }
        for (int ind = 1; ind < valueList.length; ++ind) {
            dotIsUsed = false;
            byteIsUsed = false;
            if (valueList[ind].equals(" ")) {
                return false;
            }
            if (isNumber(valueList[ind])) {
                ++ind;
                while (isNumber(valueList[ind]) || valueList[ind].equals("x") || valueList[ind].equals(".")) {
                    if (valueList[ind].equals("x")) {
                        if (!byteIsUsed) {
                            byteIsUsed = true;
                        } else {
                            return false;
                        }
                    }
                    if (valueList[ind].equals(".")) {
                        if (!dotIsUsed) {
                            dotIsUsed = true;
                        } else {
                            return false;
                        }
                    }
                    ++ind;
                }
                if (!valueList[ind].equals(",") && ind != valueList.length - 1) {
                    return false;
                }
                if (valueList[ind].equals("]")) {
                    return ind == valueList.length - 1;
                }
                ++ind;
                if (!valueList[ind].equals(" ")) {
                    return false;
                }
            } else {
                if (valueList[ind].equals("\"")) {
                    ++ind;
                    while (!valueList[ind].equals("\"")) {
                        if (ind == valueList.length - 1) {
                            return false;
                        }
                        ++ind;
                    }
                    ++ind;
                    if (!valueList[ind].equals(",") && ind != valueList.length - 1) {
                        return false;
                    }
                    if (valueList[ind].equals("]")) {
                        return ind == valueList.length - 1;
                    }
                    ++ind;
                    if (!valueList[ind].equals(" ")) {
                        return false;
                    }
                } else {
                    if (valueList[ind].equals("t") || valueList[ind].equals("f")) {
                        if (valueList[ind].equals("t") && ind + 4 < valueList.length) {
                            if (!valueList[ind + 1].equals("r") || !valueList[ind + 2].equals("u")
                                || !valueList[ind + 3].equals("e")) {
                                return false;
                            }
                            ind += 4;
                            if (!valueList[ind].equals(",") && ind != valueList.length - 1) {
                                return false;
                            }
                            if (valueList[ind].equals("]")) {
                                return ind == valueList.length - 1;
                            }
                            ++ind;
                            if (!valueList[ind].equals(" ")) {
                                return false;
                            }
                        } else {
                            if (valueList[ind].equals("f") && ind + 5 < valueList.length) {
                                if (!valueList[ind + 1].equals("a") || !valueList[ind + 2].equals("l")
                                    || !valueList[ind + 3].equals("s") || !valueList[ind + 4].equals("e")) {
                                    return false;
                                }
                                ind += 5;
                                if (!valueList[ind].equals(",") && ind != valueList.length - 1) {
                                    return false;
                                }
                                if (valueList[ind].equals("]")) {
                                    return ind == valueList.length - 1;
                                }
                                ++ind;
                                if (!valueList[ind].equals(" ")) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    private List<Class<?>> convertToPrimitive(List<?> list) { // Этим методом пользуюсь только я. Все под контролем.
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
