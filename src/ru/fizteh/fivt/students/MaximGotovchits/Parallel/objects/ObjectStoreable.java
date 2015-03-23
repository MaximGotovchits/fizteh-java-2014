package ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import java.text.ParseException;
import java.util.*;

public class ObjectStoreable implements Storeable {
    private static Set<Class<?>> allowedTypes;
    private static final String LEFT_BRACE = "[";
    private static final String RIGHT_BRACE = "]";
    private static final String VALUE_SEPARATOR = ", ";
    private List<Object> subValueList = new LinkedList<>();
    private static ObjectTable currentTableObject;
    private String serialisedValue;
    private List<Class<?>> typeKeeper = new LinkedList<>();

    private void fillAllowedTypes() {
        allowedTypes.add(Integer.class);
        allowedTypes.add(Long.class);
        allowedTypes.add(Boolean.class);
        allowedTypes.add(String.class);
        allowedTypes.add(Byte.class);
        allowedTypes.add(Double.class);
        allowedTypes.add(Float.class);
    }

    public ObjectStoreable() {
        fillAllowedTypes();
    }

    public List<Object> getSubValueList() {
        return subValueList;
    }

    public void setSerialisedValue(String toAssign) {
        serialisedValue = toAssign;
    }

    public String getSerialisedValue() {
        return serialisedValue;
    }

    public void setTypeKeeper(List<Class<?>> toAssign) {
        typeKeeper = toAssign;
    }

    public List<Class<?>> getTypeKeeper() {
        return typeKeeper;
    }

    public ObjectStoreable(List<?> values) {
        fillAllowedTypes();
        serialisedValue = LEFT_BRACE;
        for (Object val : values) {
            subValueList.add(val);
        }
        serialisedValue = String.join(VALUE_SEPARATOR, (List<String>) values);
        completeSerialisedValue();
    }

    public ObjectStoreable(ObjectTable table) {
        fillAllowedTypes();
        typeKeeper = table.getTypeKeeper();
    }

    public ObjectStoreable(String value) throws ParseException {
        fillAllowedTypes();
        ObjectStoreable obj = (ObjectStoreable) new ObjectTableProvider().deserialize(currentTableObject, value);
        if (obj == null) {
            return;
        }
        subValueList = obj.subValueList;
        serialisedValue = obj.serialisedValue;
        typeKeeper = obj.typeKeeper;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.serialisedValue);
    }

    @Override
    public boolean equals(Object obj) {
        ObjectStoreable storeableObj = (ObjectStoreable) obj;
        return this.serialisedValue.equals(storeableObj.serialisedValue)
                && this.typeKeeper.equals(storeableObj.typeKeeper)
                && this.subValueList.equals(storeableObj.subValueList);
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= this.subValueList.size()) {
            throw new IndexOutOfBoundsException();
        }
        List<Class<?>> toConvert = new LinkedList<>();
        toConvert.add(value.getClass());
        toConvert = convertToPrimitive(toConvert);
        if (!typeKeeper.get(columnIndex).equals(toConvert.get(0))) {
            throw new ColumnFormatException();
        }
        subValueList.set(columnIndex, value);
        serialisedValue = "";
        serialisedValue = LEFT_BRACE;
        for (Object val : subValueList) { // Can't use join cuz serialisedValue has type List<Object>.
            serialisedValue += val + VALUE_SEPARATOR;
        }
        serialisedValue = serialisedValue.substring(0, serialisedValue.length() - 1);
        completeSerialisedValue();
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (subValueList.size() < columnIndex - 1) {
            throw new IndexOutOfBoundsException();
        }
        return subValueList.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetSomethingAtException(columnIndex);
        return (Integer) subValueList.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetSomethingAtException(columnIndex);
        return (Long) subValueList.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetSomethingAtException(columnIndex);
        return (Byte) subValueList.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetSomethingAtException(columnIndex);
        return (Float) subValueList.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetSomethingAtException(columnIndex);
        return (Double) subValueList.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetSomethingAtException(columnIndex);
        return (Boolean) subValueList.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= this.typeKeeper.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (!typeKeeper.get(columnIndex).equals(String.class)) {
            throw new ColumnFormatException();
        }
        return subValueList.get(columnIndex).toString();
    }

    private void checkGetSomethingAtException(int columnIndex) {
        if (columnIndex >= this.typeKeeper.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (!typeKeeper.get(columnIndex).equals(boolean.class)) {
            throw new ColumnFormatException();
        }
    }



    private List<Class<?>> convertToPrimitive(List<Class<?>> list) {
        List<Class<?>> toReturn = new LinkedList<>();
        for (Class<?> object : list) {
            if (allowedTypes.contains(object)) {
                toReturn.add(object);
                continue;
            }
        }
        return toReturn;
    }

    private void completeSerialisedValue() {
        serialisedValue = serialisedValue.substring(0, serialisedValue.length() - 1);
        serialisedValue += RIGHT_BRACE;
    }
}


/*package ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects;

        import ru.fizteh.fivt.storage.structured.ColumnFormatException;
        import ru.fizteh.fivt.storage.structured.Storeable;
        import java.text.ParseException;
        import java.util.*;

public class ObjectStoreable implements Storeable {
    private static final String LEFT_BRACE = "[";
    private static final String RIGHT_BRACE = "]";
    private static final String VALUE_SEPARATOR = ", ";
    public List<Object> subValueList = new LinkedList<>();
    private static ObjectTable currentTableObject;
    public String serialisedValue;
    public List<Class<?>> typeKeeper = new LinkedList<>();

    public ObjectStoreable() {}

    public ObjectStoreable(List<?> values) {
        serialisedValue = LEFT_BRACE;
        for (Object val : values) {
            subValueList.add(val);
        }
        serialisedValue += String.join(VALUE_SEPARATOR, (List<String>) values);
        serialisedValue += RIGHT_BRACE;
    }

    public ObjectStoreable(ObjectTable table) {
        typeKeeper = table.typeKeeper;
    }

    public ObjectStoreable(String value) throws ParseException {
        ObjectStoreable obj = (ObjectStoreable) new ObjectTableProvider().deserialize(currentTableObject, value);
        if (obj == null) {
            return;
        }
        subValueList = obj.subValueList;
        serialisedValue = obj.serialisedValue;
        typeKeeper = obj.typeKeeper;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.serialisedValue);
    }

    @Override
    public boolean equals(Object obj) {
        ObjectStoreable storeableObj = (ObjectStoreable) obj;
        return this.serialisedValue.equals(storeableObj.serialisedValue)
                && this.typeKeeper.equals(storeableObj.typeKeeper)
                && this.subValueList.equals(storeableObj.subValueList);
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= this.subValueList.size()) {
            throw new IndexOutOfBoundsException();
        }
        List<Class<?>> toConvert = new LinkedList<>();
        toConvert.add(value.getClass());
        toConvert = convertToPrimitive(toConvert);
        if (!typeKeeper.get(columnIndex).equals(toConvert.get(0))) {
            throw new ColumnFormatException();
        }
        this.subValueList.set(columnIndex, value);
        this.serialisedValue = "";
        serialisedValue = LEFT_BRACE;
        for (Object val : this.subValueList) {
            serialisedValue += val + VALUE_SEPARATOR;
        }
        serialisedValue = serialisedValue.substring(0, serialisedValue.length() - 2);
        serialisedValue += RIGHT_BRACE;
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (subValueList.size() < columnIndex - 1) {
            throw new IndexOutOfBoundsException();
        }
        return subValueList.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetSomethingAtException(columnIndex);
        return (Integer) subValueList.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetSomethingAtException(columnIndex);
        return (Long) subValueList.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetSomethingAtException(columnIndex);
        return (Byte) subValueList.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetSomethingAtException(columnIndex);
        return (Float) subValueList.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetSomethingAtException(columnIndex);
        return (Double) subValueList.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkGetSomethingAtException(columnIndex);
        return (Boolean) subValueList.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= this.typeKeeper.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (!typeKeeper.get(columnIndex).equals(String.class)) {
            throw new ColumnFormatException();
        }
        return subValueList.get(columnIndex).toString();
    }

    private void checkGetSomethingAtException(int columnIndex) {
        if (columnIndex >= this.typeKeeper.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (!typeKeeper.get(columnIndex).equals(boolean.class)) {
            throw new ColumnFormatException();
        }
    }

    private List<Class<?>> convertToPrimitive(List<Class<?>> list) {
        List<Class<?>> toReturn = new LinkedList<>();
        for (Class<?> object : list) {
            toReturn.add(ObjectTableProvider.TYPES_MAP.get(object));
            continue;
        }
        return toReturn;
    }
}
*/
