package ru.fizteh.fivt.students.MaximGotovchits.Storeable.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.*;
import ru.fizteh.fivt.students.MaximGotovchits.Storeable.ObjectStoreable;
import ru.fizteh.fivt.students.MaximGotovchits.Storeable.ObjectTable;
import ru.fizteh.fivt.students.MaximGotovchits.Storeable.ObjectTableProvider;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class ObjectTableProviderTest extends ObjectTableProvider {
    public String name;
    public List<Class<?>> columnTypes = new LinkedList<Class<?>>();
    public ObjectTable tableToCompare = new ObjectTable();
    public ObjectStoreable deserializedValue = new ObjectStoreable();
    public ObjectTable table;
    public String valueToDeserialize;
    @Before
    public void initialization() throws IOException {
        name = "TestTable";
        columnTypes.add(int.class);
        columnTypes.add(long.class);
        columnTypes.add(double.class);
        columnTypes.add(float.class);
        columnTypes.add(byte.class);
        columnTypes.add(boolean.class);
        columnTypes.add(String.class);
        tableToCompare.typeKeeper = columnTypes;
        tableToCompare.tableName = name;
        deserializedValue.subValueList.add(100500);
        deserializedValue.subValueList.add((long) 10000000);
        deserializedValue.subValueList.add(123.456);
        deserializedValue.subValueList.add((float) 12.45);
        deserializedValue.subValueList.add((byte) 100);
        deserializedValue.subValueList.add(true);
        deserializedValue.subValueList.add("\"ValueToTest\"");
        deserializedValue.typeKeeper = columnTypes;
        deserializedValue.serialisedValue = "[100500, 10000000, 123.456, 12.45, 100, true, \"ValueToTest\"]";
        table = (ObjectTable) createTable(name, columnTypes);
        valueToDeserialize = "[100500, 10000000, 123.456, 12.45, 100, true, \"ValueToTest\"]";
    }
    @Test
    public void createAndGetTableTest() throws IOException {
        removeTable(name);
        name = "TestTable";
        assertEquals(tableToCompare, createTable(name, columnTypes));
        assertEquals(tableToCompare, getTable(name));
    }
    @Test
    public void deserializeTest() throws ParseException, IOException {
        assertEquals(deserializedValue, (ObjectStoreable) deserialize(table, valueToDeserialize));
    }
    /*@Test(expected = IllegalArgumentException.class) // Это была попытка сделать тест на исключения.
        //В следующей задаче я постараюсь это сделать. Просто нужно изменять кучу кода.
    public void createNullTableTest() throws IOException {
        createTable(null, columnTypes);
    }*/
    @Test
    public void serializeTest() {
        assertEquals(valueToDeserialize, serialize(table, deserializedValue));
    }
    @Test
    public void createForTest() {
        ObjectStoreable tempStoreable = new ObjectStoreable();
        tempStoreable.typeKeeper = table.typeKeeper;
        assertEquals(tempStoreable, createFor(table));
        tempStoreable.subValueList = deserializedValue.subValueList;
        tempStoreable.serialisedValue = deserializedValue.serialisedValue;
        ObjectStoreable qq = (ObjectStoreable) createFor(table, deserializedValue.subValueList);
        assertEquals(tempStoreable, (ObjectStoreable) createFor(table, deserializedValue.subValueList));
    }
    @Test
    public void getTableNamesTest() throws IOException { // Конечно же, стоит удостовериться,
    // что в рабочей директории на момент тестирования нет таблиц.
        List<String> toCompare = new LinkedList<String>();
        toCompare.add(name);
        for (Integer ind = 0; ind < 1000; ++ind) { // Generating tables.
            toCompare.add(ind.toString());
            createTable(ind.toString(), columnTypes);
        }
        assertTrue(toCompare.containsAll(getTableNames()));
        for (Integer ind = 0; ind < 1000; ++ind) {
            removeTable(ind.toString());
        }
    }
    @After
    public void cleanUp() { // Поясню. Теста на removeTable нет. Если бы метод не работал, то было бы
        // сообщение "table_name exists". Его нет.
        removeTable(name);
    }
}
