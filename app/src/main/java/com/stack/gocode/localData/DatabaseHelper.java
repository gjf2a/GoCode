package com.stack.gocode.localData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;


//https://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
//https://www.youtube.com/watch?v=cp2rL3sAFmI
//https://www.youtube.com/watch?v=-xtmTrhlwgg
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "projectsInfo.db";

    //table names
    private static final String TABLE_MODES = "modes";
    private static final String TABLE_FLAGS = "flags";
    private static final String TABLE_TRANSITION_ROWS = "transitionTableRows";
    private static final String TABLE_ACTIONS = "actions";
    private static final String TABLE_START_MODE = "startModeTable";

    //modes columns
    private static final String MODES_PROJECT = "project";
    private static final String MODES_MODE    = "name";
    private static final String MODES_ACTION  = "actions";
    private static final String MODES_TABLE   = "transitionTable";

    //flags columns
    private static final String FLAGS_PROJECT   = "project";
    private static final String FLAGS_FLAG      = "flag";
    private static final String FLAGS_CONDITION = "condition";
    private static final String FLAGS_GREATER   = "greaterThan";
    private static final String FLAGS_SENSOR    = "sensor";

    //transition tables rows columns
    private static final String TRANSITIONS_PROJECT = "project";
    private static final String TRANSITIONS_TABLE   = "transitionTable";
    private static final String TRANSITIONS_ROW_NUM = "rowNum";
    private static final String TRANSITIONS_FLAG    = "flag";
    private static final String TRANSITIONS_MODE    = "mode";
    private static final String TRANSITIONS_ID      = "id";

    //actions columns
    private static final String ACTION_PROJECT  = "actionProject";
    private static final String ACTION_NAME     = "actionName";
    private static final String ACTION_LMP      = "actionLMP";
    private static final String ACTION_RMP      = "actionRMP";
    private static final String ACTION_RLC      = "actionRLC";
    private static final String ACTION_RRC      = "actionRRC";

    //start mode columns
    private static final String START_MODE_PROJECT = "project";
    private static final String START_MODE = "startingMode";


    private static final String CREATE_TABLE_MODES  = "CREATE TABLE IF NOT EXISTS " + TABLE_MODES + "(" + MODES_PROJECT + " TEXT, " + MODES_MODE + " TEXT, " + MODES_ACTION + " TEXT, " + MODES_TABLE + " TEXT" + ")";

    private static final String CREATE_TABLE_FLAGS  = "CREATE TABLE IF NOT EXISTS " + TABLE_FLAGS + "(" + FLAGS_PROJECT + " TEXT, " + FLAGS_FLAG + " TEXT, " + FLAGS_CONDITION + " TEXT, " + FLAGS_GREATER + " TEXT, " + FLAGS_SENSOR + " TEXT" + ")";

    private static final String CREATE_TABLE_TRANSITION_ROWS = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSITION_ROWS + "(" + TRANSITIONS_PROJECT + " TEXT, " + TRANSITIONS_TABLE + " TEXT, " + TRANSITIONS_ROW_NUM + " TEXT, " + TRANSITIONS_FLAG + " TEXT, " + TRANSITIONS_MODE + " TEXT, " + TRANSITIONS_ID + " INTEGER PRIMARY KEY" + ")";

    private static final String CREATE_TABLE_ACTIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTIONS + "(" + ACTION_PROJECT + " TEXT, " + ACTION_NAME + " TEXT, " + ACTION_LMP + " TEXT, " + ACTION_RMP + " TEXT, " + ACTION_RLC + " TEXT, " + ACTION_RRC + " TEXT " + ")";

    private static final String CREATE_TABLE_START_MODE = "CREATE TABLE IF NOT EXISTS " + TABLE_START_MODE + "(" + START_MODE_PROJECT + " TEXT, " + START_MODE + " TEXT " + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) throws SQLException {

        db.execSQL(CREATE_TABLE_MODES);
        db.execSQL(CREATE_TABLE_FLAGS);
        db.execSQL(CREATE_TABLE_TRANSITION_ROWS);
        db.execSQL(CREATE_TABLE_ACTIONS);
        db.execSQL(CREATE_TABLE_START_MODE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLException{

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MODES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSITION_ROWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_START_MODE);

        onCreate(db);
    }


    public void insertNewMode(Mode mode, String project) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MODES_PROJECT, project);
        values.put(MODES_MODE, mode.getName());
        values.put(MODES_ACTION, mode.getAction().getName());
        values.put(MODES_TABLE, mode.getNextLayer().getName());
        db.insert(TABLE_MODES, null, values);
        db.close();

    }

    public ArrayList<Mode> getAllModes() throws SQLException {
        ArrayList<Action> actions = getAllActions();

        String query = "SELECT * FROM " + TABLE_MODES;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Mode> modes = new ArrayList<Mode>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Mode temp = new Mode();
                temp.setName(cursor.getString(1));
                temp.setAction(getAction(cursor.getString(2), actions));
                temp.setTtName(cursor.getString(3));
                modes.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return modes;
    }

    public Mode getMode(String name, ArrayList<Mode> modes) throws SQLException {
        for (Mode m : modes) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return new Mode();
    }

    public void updateMode(Mode oldMode, Mode newMode) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MODES_PROJECT, "default");
        values.put(MODES_MODE, newMode.getName());
        values.put(MODES_ACTION, newMode.getAction().getName());
        values.put(MODES_TABLE, newMode.getTtName());

        String[] whereArgs = {oldMode.getName()};

        db.update(TABLE_MODES, values, MODES_MODE + " = ?", whereArgs);
        db.close();
    }

    public void deleteMode(String name) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = MODES_MODE + " LIKE ?";
        String[] selectionArgs = { name };

        db.delete(TABLE_MODES, selection, selectionArgs);
        db.close();
    }

    public Row insertNewTransitionRow(int rowNum, String transitionTable, Flag flag, Mode mode) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRANSITIONS_PROJECT, "default");
        values.put(TRANSITIONS_TABLE, transitionTable);
        values.put(TRANSITIONS_ROW_NUM, rowNum);
        values.put(TRANSITIONS_FLAG, flag.getName());
        values.put(TRANSITIONS_MODE, mode.getName());
        long rowId = db.insert(TABLE_TRANSITION_ROWS, null, values);
        Row row = new Row(flag, mode, rowNum, rowId);
        Log.i(LOG,String.format("Inserting into table %s transition row: %s", transitionTable, row.toString()));
        db.close();

        return row;
    }

    public ArrayList<TransitionTable> getAllTransitionTables() throws SQLException {
        Log.i(LOG, "Calling getAllTransitionTables");
        ArrayList<Flag> flags = getAllFlags();
        ArrayList<Mode> modes = getAllModes();
        String query = "SELECT * FROM " + TABLE_TRANSITION_ROWS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Duple<Duple<String, Integer>, Row>> tableRows = new ArrayList<Duple<Duple<String, Integer>, Row>>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Duple<String, Integer> nameAndPos = new Duple( cursor.getString(1), cursor.getInt(2));
                Log.i(LOG, String.format("Row contents: %s,%s,%s,%s,%s", cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
                Row row = new Row(getFlag(cursor.getString(3), flags), getMode(cursor.getString(4), modes), cursor.getInt(2), cursor.getInt(5));
                tableRows.add(new Duple<Duple<String, Integer>, Row>(nameAndPos, row));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        ArrayList<TransitionTable> tables = new ArrayList<TransitionTable>();

        HashMap<String, ArrayList<Duple<Duple<String, Integer>, Row>>> tableNames = new HashMap<String, ArrayList<Duple<Duple<String, Integer>, Row>>>();
        for (Duple<Duple<String, Integer>, Row> d : tableRows) {
            if (!tableNames.containsKey(d.getFirst().getFirst())) {
                ArrayList<Duple<Duple<String, Integer>, Row>> temp = new ArrayList<Duple<Duple<String, Integer>, Row>>();
                temp.add(d);
                tableNames.put(d.getFirst().getFirst(), temp);
            } else {
                tableNames.get(d.getFirst().getFirst()).add(d);
            }
        }

        for (String s : tableNames.keySet()) {
            TransitionTable temp = new TransitionTable();
            temp.setName(s);

            TreeMap<Integer, Row> sortedRows = new TreeMap<Integer, Row>();
            for (Duple<Duple<String, Integer>, Row> d : tableNames.get(s)) {
                sortedRows.put(d.getFirst().getSecond(), d.getSecond());
            }
            for (int i : sortedRows.keySet()) {
                temp.addRow(sortedRows.get(i));
            }
            tables.add(temp);
        }

        return tables;
    }

    public void deleteTransitionRow(String tableName, long id) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = TRANSITIONS_TABLE + " LIKE ? AND " + TRANSITIONS_ID + " LIKE ?";
        String[] selectionArgs = { tableName, id + "" };

        db.delete(TABLE_TRANSITION_ROWS, selection, selectionArgs);
        db.close();

    }

    public void updateTransitionRow(long id, String oldName, int rowNum, String transitionTable, Flag flag, Mode mode) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRANSITIONS_PROJECT, "default");
        values.put(TRANSITIONS_TABLE, transitionTable);
        values.put(TRANSITIONS_ROW_NUM, rowNum);
        values.put(TRANSITIONS_FLAG, flag.getName());
        values.put(TRANSITIONS_MODE, mode.getName());

        String[] whereArgs = {oldName, id + ""};
        String whereClause = TRANSITIONS_TABLE + " LIKE ? AND " + TRANSITIONS_ID + " = ?";

        db.update(TABLE_TRANSITION_ROWS, values, whereClause, whereArgs);
        db.close();
    }

    public Flag getFlag(String name, ArrayList<Flag> flags) throws SQLException {
        for (Flag f : flags) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return new Flag();
    }

    public ArrayList<Flag> getAllFlags() throws SQLException {
        String query = "SELECT * FROM " + TABLE_FLAGS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Flag> flags = new ArrayList<Flag>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Flag temp = new Flag();
                temp.setName(cursor.getString(1));
                temp.setTriggerValue(cursor.getDouble(2));
                temp.setGreaterThan(cursor.getInt(3) == 1);
                temp.setSensor(cursor.getString(4));
                flags.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return flags;
    }

    public void updateFlag(Flag newFlag, Flag oldFlag) throws SQLException {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FLAGS_PROJECT, "default");
        values.put(FLAGS_FLAG, newFlag.getName());
        values.put(FLAGS_CONDITION, newFlag.getTriggerValue());
        values.put(FLAGS_GREATER, newFlag.isGreaterThan() ? 1 : 0);
        values.put(FLAGS_SENSOR, newFlag.getSensor());

        String[] whereArgs = {oldFlag.getName()};

        db.update(TABLE_FLAGS, values, FLAGS_FLAG + " = ?", whereArgs);
        db.close();
    }

    public void deleteFlag(Flag flag) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = FLAGS_FLAG + " LIKE ?";
        String[] selectionArgs = { flag.getName() };

        db.delete(TABLE_FLAGS, selection, selectionArgs);
        db.close();
    }

    public void insertNewFlag(Flag flag, String project) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FLAGS_PROJECT, project);
        values.put(FLAGS_FLAG, flag.getName());
        values.put(FLAGS_CONDITION, flag.getTriggerValue());
        values.put(FLAGS_GREATER, flag.isGreaterThan() ? 1 : 0);
        values.put(FLAGS_SENSOR, flag.getSensor());
        db.insert(TABLE_FLAGS, null, values);
        db.close();
    }

    public void insertNewAction(Action action) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACTION_PROJECT, "default");
        values.put(ACTION_NAME, action.getName());
        values.put(ACTION_LMP, action.getLeftMotorInput());
        values.put(ACTION_RMP, action.getRightMotorInput());
        values.put(ACTION_RLC, action.getRLCint());
        values.put(ACTION_RRC, action.getRRCint());

        db.insert(TABLE_ACTIONS, null, values);
        db.close();
    }

    public void updateAction(Action oldAction, Action newAction) throws SQLException { //https://abhiandroid.com/database/sqlite
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACTION_PROJECT, "default");
        values.put(ACTION_NAME, newAction.getName());
        values.put(ACTION_LMP, newAction.getLeftMotorInput());
        values.put(ACTION_RMP, newAction.getRightMotorInput());
        values.put(ACTION_RLC, newAction.getRLCint());
        values.put(ACTION_RRC, newAction.getRRCint());

        String[] whereArgs = {oldAction.getName()};

        db.update(TABLE_ACTIONS, values, ACTION_NAME + " = ?", whereArgs);
        db.close();
    }

    public void deleteAction(Action action) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = ACTION_NAME + " LIKE ?";
        String[] selectionArgs = { action.getName() };

        db.delete(TABLE_ACTIONS, selection, selectionArgs);
        db.close();
    }

    public ArrayList<Action> getAllActions() throws SQLException { //https://stackoverflow.com/questions/31353447/how-to-list-all-the-rows-in-a-table-using-sqlite-in-android-studio-using-cursor
        String query = "SELECT * FROM " + TABLE_ACTIONS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Action> actions = new ArrayList<Action>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Action temp = new Action();
                temp.setName(cursor.getString(1));
                temp.setLeftMotorInput(cursor.getInt(2));
                temp.setRightMotorInput(cursor.getInt(3));
                temp.setResetLeftCount(cursor.getInt(4) == 1);
                temp.setResetRightCount(cursor.getInt(5) == 1);
                actions.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return actions;
    }

    public Action getAction(String name, ArrayList<Action> actions) throws SQLException {

        for (Action a : actions) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        Action temp = new Action();
        temp.setName("Could not get Action from Database");
        return temp;
    }

    public void insertNewStartMode(Mode mode, String project) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(START_MODE_PROJECT, project);
        values.put(START_MODE, mode.getName());;
        db.insert(TABLE_START_MODE, null, values);
        db.close();
    }

    public void updateStartMode(String newMode) throws SQLException { //in future just use project as where
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(START_MODE_PROJECT, "default");
        values.put(START_MODE, newMode);

        String[] whereArgs = {"default"};

        db.update(TABLE_START_MODE, values, START_MODE_PROJECT + " = ?", whereArgs);
        db.close();
    }

    public String getStartMode() {
        String query = "SELECT * FROM " + TABLE_START_MODE;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String startModeName = "";
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
                startModeName = cursor.getString(1);
        }
        cursor.close();
        db.close();
        return startModeName;
    }
}

