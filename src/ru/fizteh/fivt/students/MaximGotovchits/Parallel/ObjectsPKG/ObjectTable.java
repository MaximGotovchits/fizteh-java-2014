package ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ObjectTable implements Table {
    static int overwriteNum = 0;
    private static final String JSON_REG_EX = "\\s*,\\s*(?=(?:(?:[^\"]*\"){2})*[^\"]*$)"; // Removes commas
    // outside of "...".
    public static String usingTableName = new String(); // OR public static String usingTableName;
    private static final String DATA_BASE_NAME = System.getProperty("fizteh.db.dir");
    private static final String SIGNATURE_FILENAME = "signature.tsv";

    public ThreadLocal<Stack> lastChanges = new ThreadLocal<Stack>() {
        @Override
        protected Stack initialValue() {
            return new Stack();
        }
    };

    public ThreadLocal<HashMap<String, ObjectStoreable>> storage = new ThreadLocal<HashMap<String, ObjectStoreable>>() {
        @Override
        protected HashMap<String, ObjectStoreable> initialValue() {
            return new HashMap<String, ObjectStoreable>();
        }
    };

    public Map<String, ObjectStoreable> commitStorage = new HashMap<>();
    public String tableName = new String();
    public List<Class<?>> typeKeeper = new LinkedList<Class<?>>();

    public ObjectTable(Table table) {
        ObjectTable temp = (ObjectTable) table;
        this.tableName = temp.tableName;
        this.typeKeeper = temp.typeKeeper;
    }

    public ObjectTable(String name) {
        if (!new File(name).isAbsolute()) {
            name = DATA_BASE_NAME + File.separator + name;
        }
        this.tableName = new File(name).getName();
        String content = new String();
        try {
            if (!new File(name + File.separator + SIGNATURE_FILENAME).isAbsolute()) {
                name = DATA_BASE_NAME + File.separator + name;
            }
            content = readFile(name + File.separator + SIGNATURE_FILENAME, Charset.defaultCharset());
            content.replaceAll("\\s+", " ");
            String[] types = content.split(" ");
            for (String type : types) {
                typeKeeper.add(getType(type));
            }
        } catch (IOException s) {
            System.err.println(s);
        }
    }

    public ObjectTable(String name, List<Class<?>> typeList) {
        for (Class<?> type : typeList) {
            typeKeeper.add(type);
        }
        tableName = name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.tableName);
    }

    @Override
    public boolean equals(Object obj) {
        ObjectTable tableObj = (ObjectTable) obj;
        if (this.tableName.equals(tableObj.tableName) && this.typeKeeper.equals(tableObj.typeKeeper)) {
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException { // В документации в Index
        // не сказано, когда кидать ParseException, однако согласно интерфейсу тут все-таки написано
        // throws ParseException, но на самом деле это исключение тут не бросатется.
        if (key == null) {
            throw new IllegalArgumentException();
        }
        ObjectStoreable value = storage.get().get(key);
        if (value == null) {
            return null;
        }
        String serializedValue = value.serialisedValue;
        return value;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        if (!this.typeKeeper.equals(((ObjectStoreable) value).typeKeeper)) {
            throw new ColumnFormatException();
        }
        ObjectStoreable previousValue = storage.get().put(key, (ObjectStoreable) value);
        if (previousValue == null) {
            lastChanges.get().push(key);
            lastChanges.get().push("remove");
        } else {
            lastChanges.get().push(previousValue);
            lastChanges.get().push(key);
            lastChanges.get().push("put");
            ++overwriteNum;
            System.out.println(previousValue.serialisedValue);
        }
        return previousValue;
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        ObjectStoreable value = storage.get().remove(key);
        commitStorage.remove(key);
        if (value != null) {
            lastChanges.get().push(value);
            lastChanges.get().push(key);
            lastChanges.get().push("put");
        }
        return value;
    }

    @Override
    public int size() {
        return storage.get().size();
    }

    @Override
    public List<String> list() {
        LinkedList<String> list = new LinkedList<String>();
        int size = 0;
        Set<String> k = storage.get().keySet();
        for (Object iter : k) {
            list.add(iter.toString());
            if (size < storage.get().size() - 1) {
                System.out.print(iter + ", ");
            } else {
                System.out.print(iter);
            }
            ++size;
        }
        if (size != 0) {
            System.out.println();
        }
        return list;
    }

    @Override
    public int commit() {
        int savedKeys = Math.abs(storage.get().size() - commitStorage.size());
        commitStorage = new HashMap<String, ObjectStoreable>(storage.get());
        lastChanges.get().clear();
        return savedKeys;
    }

    @Override
    public int rollback() throws IllegalArgumentException {
        int changes = Math.abs(storage.get().size() - commitStorage.size() + overwriteNum);
            while (!lastChanges.get().isEmpty()) {
            Object tmpCmd = lastChanges.get().pop();
            if (tmpCmd.equals("put")) {
                String key = lastChanges.get().pop().toString();
                ObjectStoreable value = (ObjectStoreable) lastChanges.get().pop();
                storage.get().put(key, value);
            }
            if (tmpCmd.equals("remove")) {
                String key = lastChanges.get().pop().toString();
                storage.get().remove(key);
            }
        }
        overwriteNum = 0;
        return changes;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        int numberOfUncommitedChanges = Math.abs(storage.get().size() - commitStorage.size());
        return numberOfUncommitedChanges;
    }

    @Override
    public int getColumnsCount() {
        return typeKeeper.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (this.getColumnsCount() < columnIndex - 1) {
            throw new IndexOutOfBoundsException();
        }
        Class<?> objectToReturn = typeKeeper.get(columnIndex);
        return objectToReturn;
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        String temp = new String(encoded, encoding);
        return temp.replaceAll("^\\s*|\\s*$", "");
    }



    private Class<?> getType(String typeName) {
        if (typeName.equals("int")) {
            return int.class;
        }
        if (typeName.equals("long")) {
            return long.class;
        }
        if (typeName.equals("byte")) {
            return byte.class;
        }
        if (typeName.equals("float")) {
            return float.class;
        }
        if (typeName.equals("double")) {
            return double.class;
        }
        if (typeName.equals("boolean")) {
            return boolean.class;
        }
        if (typeName.equals("String")) {
            return String.class;
        }
        return null;
    }
}
