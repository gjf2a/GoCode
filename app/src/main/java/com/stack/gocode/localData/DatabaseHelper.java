package com.stack.gocode.localData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.stack.gocode.localData.factory.FuzzyFactory;
import com.stack.gocode.localData.factory.FuzzyFlagRow;
import com.stack.gocode.localData.factory.TransitionRow;
import com.stack.gocode.localData.factory.TransitionTableFactory;
import com.stack.gocode.localData.fuzzy.FuzzyFlag;
import com.stack.gocode.localData.fuzzy.TriangleFuzzyFlag;
import com.stack.gocode.sensors.SensedValues;

import java.util.ArrayList;


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

    // Original tables
    private static final String CREATE_TABLE_MODES  = "CREATE TABLE IF NOT EXISTS " + TABLE_MODES + "(" + MODES_PROJECT + " TEXT, " + MODES_MODE + " TEXT, " + MODES_ACTION + " TEXT, " + MODES_TABLE + " TEXT" + ")";
    private static final String CREATE_TABLE_FLAGS  = "CREATE TABLE IF NOT EXISTS " + TABLE_FLAGS + "(" + FLAGS_PROJECT + " TEXT, " + FLAGS_FLAG + " TEXT, " + FLAGS_CONDITION + " TEXT, " + FLAGS_GREATER + " TEXT, " + FLAGS_SENSOR + " TEXT" + ")";
    private static final String CREATE_TABLE_TRANSITION_ROWS = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSITION_ROWS + "(" + TRANSITIONS_PROJECT + " TEXT, " + TRANSITIONS_TABLE + " TEXT, " + TRANSITIONS_ROW_NUM + " TEXT, " + TRANSITIONS_FLAG + " TEXT, " + TRANSITIONS_MODE + " TEXT, " + TRANSITIONS_ID + " INTEGER PRIMARY KEY" + ")";
    private static final String CREATE_TABLE_ACTIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTIONS + "(" + ACTION_PROJECT + " TEXT, " + ACTION_NAME + " TEXT, " + ACTION_LMP + " TEXT, " + ACTION_RMP + " TEXT, " + ACTION_RLC + " TEXT, " + ACTION_RRC + " TEXT " + ")";
    private static final String CREATE_TABLE_START_MODE = "CREATE TABLE IF NOT EXISTS " + TABLE_START_MODE + "(" + START_MODE_PROJECT + " TEXT, " + START_MODE + " TEXT " + ")";

    // Fuzzy logic table names
    private static final String TABLE_FUZZY_FLAGS = "FuzzyFlags";
    private static final String TABLE_DEFUZZIFIERS = "Defuzzifiers";
    private static final String TABLE_FUZZY_ACTIONS = "FuzzyActions";

    // Fuzzy flag columns
    private static final String FUZZY_FLAGS_TYPE = "encoding";
    private static final String FUZZY_FLAGS_ARG1 = "arg1";
    private static final String FUZZY_FLAGS_ARG2 = "arg2";
    private static final String FUZZY_FLAGS_ARG3 = "arg3";
    private static final String FUZZY_FLAGS_ARG4 = "arg4";

    // Defuzzifier columns
    private static final String DEFUZZY_PROJECT = "project";
    private static final String DEFUZZY_NAME = "name";
    private static final String DEFUZZY_SPEED_1 = "speed1";
    private static final String DEFUZZY_SPEED_2 = "speed2";

    // Fuzzy action columns
    private static final String FUZZY_ACTION_PROJECT = "project";
    private static final String FUZZY_ACTION_NAME = "name";
    private static final String FUZZY_ACTION_LEFT_FLAG = "leftFlag";
    private static final String FUZZY_ACTION_LEFT_DEFUZZIFIER = "leftDefuzzifier";
    private static final String FUZZY_ACTION_RIGHT_FLAG = "rightFlag";
    private static final String FUZZY_ACTION_RIGHT_DEFUZZIFIER = "rightDefuzzifier";

    // Fuzzy logic tables
    private static final String CREATE_TABLE_FUZZY_FLAGS  = "CREATE TABLE IF NOT EXISTS " + TABLE_FUZZY_FLAGS + "(" + FLAGS_PROJECT + " TEXT, " + FLAGS_FLAG + " TEXT, " + FUZZY_FLAGS_TYPE + " TEXT, " + FUZZY_FLAGS_ARG1 + " TEXT, " + FUZZY_FLAGS_ARG2 + " TEXT, " + FUZZY_FLAGS_ARG3 + " TEXT, " + FUZZY_FLAGS_ARG4 + " TEXT, " + FLAGS_SENSOR + " TEXT)";
    private static final String CREATE_TABLE_DEFUZZIFIERS = "CREATE TABLE IF NOT EXISTS " + TABLE_DEFUZZIFIERS + "(" + DEFUZZY_PROJECT + " TEXT, " + DEFUZZY_NAME + " TEXT, " + DEFUZZY_SPEED_1 + " TEXT, " + DEFUZZY_SPEED_2 + " TEXT)";
    private static final String CREATE_TABLE_FUZZY_ACTIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_FUZZY_ACTIONS + "(" + FUZZY_ACTION_PROJECT + " TEXT, " + FUZZY_ACTION_NAME + " TEXT, " + FUZZY_ACTION_LEFT_DEFUZZIFIER + " TEXT, " + FUZZY_ACTION_LEFT_FLAG + " TEXT, " + FUZZY_ACTION_RIGHT_DEFUZZIFIER + " TEXT, " + FUZZY_ACTION_RIGHT_FLAG + " TEXT)";

    private static FuzzyFactory fuzzyFactory = null;
    private static TransitionTableFactory transitionTableFactory = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (fuzzyFactory == null) {
            fuzzyFactory = getFuzzyItems();
        }
        if (transitionTableFactory == null) {
            transitionTableFactory = getTransitionItems();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) throws SQLException {
        Log.i(LOG, "Entering onCreate()");

        // Original tables
        db.execSQL(CREATE_TABLE_MODES);
        db.execSQL(CREATE_TABLE_FLAGS);
        db.execSQL(CREATE_TABLE_TRANSITION_ROWS);
        db.execSQL(CREATE_TABLE_ACTIONS);
        db.execSQL(CREATE_TABLE_START_MODE);

        // Fuzzy logic
        db.execSQL(CREATE_TABLE_FUZZY_FLAGS);
        db.execSQL(CREATE_TABLE_FUZZY_ACTIONS);
        db.execSQL(CREATE_TABLE_DEFUZZIFIERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLException{
        /*
        // These are not necessary unless the table formats change.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MODES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSITION_ROWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_START_MODE);
        */

        // This is necessary when incorporating new tables.
        Log.i(LOG, "Entering onUpgrade()");
        onCreate(db);
    }

    public Mode insertNewMode(String project) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        Mode mode = new Mode("Mode" + (transitionTableFactory.getNumModes() + 1), transitionTableFactory.getDefaultAction(), transitionTableFactory.getDefaultTable());
        ContentValues values = new ContentValues();
        values.put(MODES_PROJECT, project);
        values.put(MODES_MODE, mode.getName());
        values.put(MODES_ACTION, mode.getActionName());
        values.put(MODES_TABLE, mode.getTtName());
        db.insert(TABLE_MODES, null, values);
        db.close();

        transitionTableFactory.addMode(mode);
        return mode;
    }

    public ArrayList<Mode> getModeList() {
        return transitionTableFactory.getModeList();
    }

    private void getAllModes(TransitionTableFactory factory) {
        ArrayList<Action> actions = factory.getActionList();

        String query = "SELECT * FROM " + TABLE_MODES;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                factory.addMode(cursor.getString(1), cursor.getString(2), cursor.getString(3), fuzzyFactory);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public Mode getMode(String name) throws SQLException {
        return transitionTableFactory.getMode(name);
    }

    public void updateMode(Mode oldMode, Mode newMode) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MODES_PROJECT, "default");
        values.put(MODES_MODE, newMode.getName());
        values.put(MODES_ACTION, newMode.getActionName());
        values.put(MODES_TABLE, newMode.getTtName());

        String[] whereArgs = {oldMode.getName()};

        db.update(TABLE_MODES, values, MODES_MODE + " = ?", whereArgs);
        db.close();

        transitionTableFactory.replaceMode(oldMode.getName(), newMode);
    }

    public void deleteMode(String name) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = MODES_MODE + " LIKE ?";
        String[] selectionArgs = { name };

        db.delete(TABLE_MODES, selection, selectionArgs);
        db.close();

        transitionTableFactory.delMode(name);
    }

    public TransitionTable createNewTable(String project) {
        return transitionTableFactory.addTable();
    }

    public TransitionTable getTable(String name) {
        return transitionTableFactory.getTable(name);
    }

    public Row insertNewTransitionRow(String project, String transitionTable) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        TransitionTable table = transitionTableFactory.getTable(transitionTable);
        if (transitionTableFactory.getNumFlags() == 0) {
            insertNewFlag(project);
        }
        Flag flag = transitionTableFactory.getDefaultFlag();
        if (transitionTableFactory.getNumModes() == 0) {
            insertNewMode(project);
        }
        Mode mode = transitionTableFactory.getDefaultMode();
        int rowNum = table.getNumRows();

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

        transitionTableFactory.addTableRow(transitionTable, row);
        return row;
    }

    public ArrayList<TransitionTable> getTransitionTableList() throws SQLException {
        return transitionTableFactory.getTableList();
    }

    public void deleteTransitionRow(String tableName, long id) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = TRANSITIONS_TABLE + " LIKE ? AND " + TRANSITIONS_ID + " LIKE ?";
        String[] selectionArgs = { tableName, id + "" };

        db.delete(TABLE_TRANSITION_ROWS, selection, selectionArgs);
        db.close();

        transitionTableFactory.delTableRow(tableName, id);
    }

    public void renameTableRows(String oldName, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRANSITIONS_TABLE, newName);
        db.update(TABLE_TRANSITION_ROWS, values, TRANSITIONS_TABLE + " LIKE ?", new String[]{oldName});

        ContentValues modeUpdate = new ContentValues();
        modeUpdate.put(MODES_TABLE, newName);
        db.update(TABLE_MODES, modeUpdate, MODES_TABLE + " LIKE ?", new String[]{oldName});
        db.close();

        // TODO: Fix all Modes that point to oldName as well!!!

        transitionTableFactory.renameTable(oldName, newName);
    }

    public void updateTransitionRow(long id, int rowNum, String transitionTable, Flag flag, Mode mode) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRANSITIONS_PROJECT, "default");
        values.put(TRANSITIONS_TABLE, transitionTable);
        values.put(TRANSITIONS_ROW_NUM, rowNum);
        values.put(TRANSITIONS_FLAG, flag.getName());
        values.put(TRANSITIONS_MODE, mode.getName());

        String[] whereArgs = {transitionTable, id + ""};
        String whereClause = TRANSITIONS_TABLE + " LIKE ? AND " + TRANSITIONS_ID + " = ?";

        db.update(TABLE_TRANSITION_ROWS, values, whereClause, whereArgs);
        db.close();
        transitionTableFactory.replaceRow(transitionTable, rowNum, flag, mode);
    }

    public int getFlagCount() {
        return transitionTableFactory.getNumFlags();
    }

    public Flag getFlag(String name) throws SQLException {
        return transitionTableFactory.getFlag(name);
    }

    public ArrayList<Flag> getFlagList() {
        return transitionTableFactory.getFlagList();
    }

    private void getAllFlags(TransitionTableFactory factory) {
        String query = "SELECT * FROM " + TABLE_FLAGS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                factory.addFlag(cursor.getString(1), cursor.getString(4), cursor.getInt(3) == 1, cursor.getDouble(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
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

        transitionTableFactory.replaceFlag(oldFlag.getName(), newFlag);
    }

    public void deleteFlag(Flag flag) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = FLAGS_FLAG + " LIKE ?";
        String[] selectionArgs = { flag.getName() };

        db.delete(TABLE_FLAGS, selection, selectionArgs);
        db.close();
        transitionTableFactory.delFlag(flag.getName());
    }

    public Flag insertNewFlag(String project) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        Flag flag = new Flag("flag" + (getFlagCount() + 1), SensedValues.SENSOR_NAMES[0], false, 100);

        ContentValues values = new ContentValues();
        values.put(FLAGS_PROJECT, project);
        values.put(FLAGS_FLAG, flag.getName());
        values.put(FLAGS_CONDITION, flag.getTriggerValue());
        values.put(FLAGS_GREATER, flag.isGreaterThan() ? 1 : 0);
        values.put(FLAGS_SENSOR, flag.getSensor());
        db.insert(TABLE_FLAGS, null, values);
        db.close();

        transitionTableFactory.addFlag(flag);
        return flag;
    }

    public Action insertNewAction(String project) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        Action action = new Action("action" + (transitionTableFactory.getNumActions() + 1), 0, 0, false, false);
        ContentValues values = new ContentValues();
        values.put(ACTION_PROJECT, project);
        values.put(ACTION_NAME, action.getName());
        values.put(ACTION_LMP, action.getLeftMotorInput());
        values.put(ACTION_RMP, action.getRightMotorInput());
        values.put(ACTION_RLC, action.getRLCint());
        values.put(ACTION_RRC, action.getRRCint());

        db.insert(TABLE_ACTIONS, null, values);
        db.close();

        transitionTableFactory.addAction(action);

        return action;
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

        transitionTableFactory.replaceAction(oldAction.getName(), newAction);
    }

    public void deleteAction(Action action) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = ACTION_NAME + " LIKE ?";
        String[] selectionArgs = { action.getName() };

        db.delete(TABLE_ACTIONS, selection, selectionArgs);
        db.close();

        transitionTableFactory.delAction(action.getName());
    }

    public ArrayList<Action> getActionList() {
        return transitionTableFactory.getActionList();
    }

    private void getAllActions(TransitionTableFactory factory) throws SQLException { //https://stackoverflow.com/questions/31353447/how-to-list-all-the-rows-in-a-table-using-sqlite-in-android-studio-using-cursor
        String query = "SELECT * FROM " + TABLE_ACTIONS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                factory.addAction(cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4) == 1 ,cursor.getInt(5) == 1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public Action getAction(String name) throws SQLException {
        return transitionTableFactory.getAction(name);
    }

    public void insertNewStartMode(String modeName, String project) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(START_MODE_PROJECT, project);
        values.put(START_MODE, modeName);;
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

    public TransitionTable getDefaultTable() {
        return transitionTableFactory.getDefaultTable();
    }

    public ArrayList<FuzzyFlag> getFuzzyFlagList() {
        return fuzzyFactory.allGeneratedFlags();
    }

    private TransitionTableFactory getTransitionItems() {
        TransitionTableFactory factory = new TransitionTableFactory();
        getAllFlags(factory);
        getAllActions(factory);
        ArrayList<TransitionRow> rows = getTransitionRows();
        factory.addEmptyTablesFrom(rows);
        getAllModes(factory);
        factory.makeTableRowsFrom(rows);
        return factory;
    }

    private ArrayList<TransitionRow> getTransitionRows() {
        ArrayList<TransitionRow> rows = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_TRANSITION_ROWS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Duple<String, Integer> nameAndPos = new Duple( cursor.getString(1), cursor.getInt(2));
                Log.i(LOG, String.format("Row contents: %s,%s,%s,%s,%s", cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
                TransitionRow row = new TransitionRow(
                        cursor.getString(cursor.getColumnIndexOrThrow(TRANSITIONS_PROJECT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TRANSITIONS_TABLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TRANSITIONS_FLAG)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TRANSITIONS_MODE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(TRANSITIONS_ROW_NUM)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(TRANSITIONS_ID)));
                rows.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return rows;
    }

    private FuzzyFactory getFuzzyItems() throws SQLException {
        // This is a bit of a hack, in that I can't quite make onUpgrade() do what I want, but
        // it should be harmless.
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_FUZZY_FLAGS);
        db.execSQL(CREATE_TABLE_FUZZY_ACTIONS);
        db.execSQL(CREATE_TABLE_DEFUZZIFIERS);

        FuzzyFactory factory = new FuzzyFactory();
        getAllFuzzyFlags(factory);
        getAllDefuzzifiers(factory);
        getAllFuzzyActions(factory);
        return factory;
    }

    private void getAllFuzzyFlags(FuzzyFactory factory) throws SQLException {
        String query = "SELECT * FROM " + TABLE_FUZZY_FLAGS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                factory.addFlagRow(new FuzzyFlagRow(
                        cursor.getString(cursor.getColumnIndexOrThrow(FLAGS_PROJECT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FLAGS_FLAG)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_FLAGS_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_FLAGS_ARG1)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_FLAGS_ARG2)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_FLAGS_ARG3)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_FLAGS_ARG4)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FLAGS_SENSOR))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        factory.generateFuzzyFlags();
    }

    public int getFuzzyFlagCount() {
        return fuzzyFactory.numFuzzyFlags();
    }

    public FuzzyFlag insertNewFuzzyFlag(String project) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        FuzzyFlag flag = new TriangleFuzzyFlag("fuzzyFlag" + (getFuzzyFlagCount() + 1), SensedValues.SENSOR_NAMES[0], 0, 100, 200);

        ContentValues values = new ContentValues();
        values.put(FLAGS_PROJECT, project);
        values.put(FLAGS_FLAG, flag.getName());
        values.put(FUZZY_FLAGS_TYPE, flag.getClass().getSimpleName());/*
        values.put(FLAGS_CONDITION, flag.getTriggerValue());
        values.put(FLAGS_GREATER, flag.isGreaterThan() ? 1 : 0);
        values.put(FLAGS_SENSOR, flag.getSensor());
        db.insert(TABLE_FLAGS, null, values);
        db.close();

        transitionTableFactory.addFlag(flag);*/
        return flag;
    }

    private void getAllDefuzzifiers(FuzzyFactory factory) {
        String query = "SELECT * FROM " + TABLE_DEFUZZIFIERS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                factory.addDefuzzifier(
                        cursor.getString(cursor.getColumnIndexOrThrow(DEFUZZY_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DEFUZZY_SPEED_1)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DEFUZZY_SPEED_2)));
            } while (cursor.moveToNext());
        }
    }

    private void getAllFuzzyActions(FuzzyFactory factory) throws SQLException {
        String query = "SELECT * FROM " + TABLE_FUZZY_ACTIONS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                factory.addFuzzyAction(
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_ACTION_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_ACTION_LEFT_FLAG)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_ACTION_LEFT_DEFUZZIFIER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_ACTION_RIGHT_FLAG)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_ACTION_RIGHT_DEFUZZIFIER)));
            } while (cursor.moveToNext());
        }
    }
}

