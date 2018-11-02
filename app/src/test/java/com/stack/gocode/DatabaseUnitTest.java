package com.stack.gocode;

import com.stack.gocode.localData.DatabaseHelper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DatabaseUnitTest {
    @Test
    public void tableCreateTest() {
        assertEquals("CREATE TABLE IF NOT EXISTS TestTable(col1 TEXT,col2 TEXT,col3 TEXT)", DatabaseHelper.createTableStr("TestTable", "col1", "col2", "col3"));
    }
}