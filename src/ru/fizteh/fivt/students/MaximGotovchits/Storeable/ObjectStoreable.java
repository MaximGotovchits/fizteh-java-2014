package ru.fizteh.fivt.students.MaximGotovchits.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ObjectStoreable extends CommandsTools implements Storeable {
    public List<Object> subValueList = new LinkedList<Object>();
    public String serialisedValue = new String();
    public List<Class<?>> typeKeeper = new LinkedList<Class<?>>();
    public ObjectStoreable() {}
    public ObjectStoreable(List<?> values) {
        int ind = 0;
        serialisedValue = "[";
        for (Object val : values) {
            subValueList.add(val);
            serialisedValue += val + ", ";
        }
        serialisedValue = serialisedValue.substring(0, serialisedValue.length() - 2); // Откусить ", ".
        serialisedValue += "]";
    }
    public ObjectStoreable(Storeable value) {
        subValueList = ((ObjectStoreable) value).subValueList;
        serialisedValue = ((ObjectStoreable) value).serialisedValue;
        typeKeeper = ((ObjectStoreable) value).typeKeeper;
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
    public boolean equals(Object obj) { // Возможно тут нужно было как-то использовать hashCode,
        // но я не знаю как, особенно если учесть, что он возваращает Objects, а не Object.
        ObjectStoreable storeableObj = (ObjectStoreable) obj;
        if (this.serialisedValue.equals(storeableObj.serialisedValue)
                && this.typeKeeper.equals(storeableObj.typeKeeper)
                && this.subValueList.equals(storeableObj.subValueList)) {
            return true;
        }
        return false;
    }
    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        try {
            if (columnIndex >= this.subValueList.size()) {
                throw new IndexOutOfBoundsException();
            }
            List<Class<?>> toConvert = new LinkedList<Class<?>>();
            toConvert.add(value.getClass());
            toConvert = convertToPrimitive(toConvert);
            if (!typeKeeper.get(columnIndex).equals(toConvert.get(0))) {
                throw new ColumnFormatException();
            }
        } catch (IndexOutOfBoundsException s) {
            System.err.println(s);
            return;
        } catch (ColumnFormatException s) {
            System.err.println(s);
            return;
        }
        this.subValueList.set(columnIndex, value);
        this.serialisedValue = "";
        serialisedValue = "[";
        for (Object val : this.subValueList) {
            serialisedValue += val + ", ";
        }
        serialisedValue = serialisedValue.substring(0, serialisedValue.length() - 2); // Откусить ", ".
        serialisedValue += "]";
        return;
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
        if (columnIndex >= subValueList.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (!this.typeKeeper.get(columnIndex).equals(int.class)) {
            throw new ColumnFormatException();
        }
        return (Integer) subValueList.get(columnIndex);
    }
    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        try {
            if (columnIndex >= this.typeKeeper.size()) {
                throw new IndexOutOfBoundsException();
            }
            if (!typeKeeper.get(columnIndex).equals(long.class)) {
                throw new ColumnFormatException();
            }
        } catch (IndexOutOfBoundsException s) {
            System.err.println(s);
        } catch (ColumnFormatException s) {
            System.err.println(s);
        }
        return (Long) subValueList.get(columnIndex);
    }
    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        try {
            if (columnIndex >= this.typeKeeper.size()) {
                throw new IndexOutOfBoundsException();
            }
            if (!typeKeeper.get(columnIndex).equals(byte.class)) {
                throw new ColumnFormatException();
            }
        } catch (IndexOutOfBoundsException s) {
            System.err.println(s);
        } catch (ColumnFormatException s) {
            System.err.println(s);
        }
        return (Byte) subValueList.get(columnIndex);
    }
    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        try {
            if (columnIndex >= this.typeKeeper.size()) {
                throw new IndexOutOfBoundsException();
            }
            if (!typeKeeper.get(columnIndex).equals(float.class)) {
                throw new ColumnFormatException();
            }
        } catch (IndexOutOfBoundsException s) {
            System.err.println(s);
        } catch (ColumnFormatException s) {
            System.err.println(s);
        }
        return (Float) subValueList.get(columnIndex);
    }
    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        try {
            if (columnIndex >= this.typeKeeper.size()) {
                throw new IndexOutOfBoundsException();
            }
            if (!typeKeeper.get(columnIndex).equals(double.class)) {
                throw new ColumnFormatException();
            }
        } catch (IndexOutOfBoundsException s) {
            System.err.println(s);
        } catch (ColumnFormatException s) {
            System.err.println(s);
        }
        return (Double) subValueList.get(columnIndex);
    }
    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        try {
            if (columnIndex >= this.typeKeeper.size()) {
                throw new IndexOutOfBoundsException();
            }
            if (!typeKeeper.get(columnIndex).equals(boolean.class)) {
                throw new ColumnFormatException();
            }
        } catch (IndexOutOfBoundsException s) {
            System.err.println(s);
        } catch (ColumnFormatException s) {
            System.err.println(s);
        }
        return (Boolean) subValueList.get(columnIndex);
    }
    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        try {
            if (columnIndex >= this.typeKeeper.size()) {
                throw new IndexOutOfBoundsException();
            }
            if (!typeKeeper.get(columnIndex).equals(String.class)) {
                throw new ColumnFormatException();
            }
        } catch (IndexOutOfBoundsException s) {
            System.err.println(s);
        } catch (ColumnFormatException s) {
            System.err.println(s);
        }
        return subValueList.get(columnIndex).toString();
    }
    private List<Class<?>> convertToPrimitive(List<Class<?>> list) { // Этим методом пользуюсь только я.
        List<Class<?>> toReturn = new LinkedList<Class<?>>();
        for (Class<?> object : list) {
            if (object.equals(Integer.class)) {
                toReturn.add(int.class);
                continue;
            }
            if (object.equals(Long.class)) {
                toReturn.add(long.class);
                continue;
            }
            if (object.equals(Boolean.class)) {
                toReturn.add(boolean.class);
                continue;
            }
            if (object.equals(String.class)) {
                toReturn.add(String.class);
                continue;
            }
            if (object.equals(Byte.class)) {
                toReturn.add(byte.class);
                continue;
            }
            if (object.equals(Double.class)) {
                toReturn.add(double.class);
                continue;
            }
            if (object.equals(Float.class)) {
                toReturn.add(float.class);
                continue;
            }
        }
        return toReturn;
    }
}
