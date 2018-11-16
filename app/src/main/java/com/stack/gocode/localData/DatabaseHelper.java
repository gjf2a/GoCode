package com.stack.gocode.localData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.stack.gocode.localData.factory.FuzzyFactory;
import com.stack.gocode.localData.factory.FuzzyFlagFinder;
import com.stack.gocode.localData.factory.FuzzyFlagRow;
import com.stack.gocode.localData.factory.ImageFactory;
import com.stack.gocode.localData.factory.NeuralNetFactory;
import com.stack.gocode.localData.factory.DatabaseTransitionRow;
import com.stack.gocode.localData.factory.TransitionTableFactory;
import com.stack.gocode.localData.factory.WrappedLabel;
import com.stack.gocode.localData.flagtypes.SimpleSensorFlag;
import com.stack.gocode.localData.fuzzy.Defuzzifier;
import com.stack.gocode.localData.fuzzy.FuzzyAction;
import com.stack.gocode.localData.fuzzy.FuzzyFlag;
import com.stack.gocode.sensors.SensedValues;
import com.stack.gocode.sensors.Symbol;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.ml.ANN_MLP;

import java.util.ArrayList;
import java.util.TreeMap;


//https://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
//https://www.youtube.com/watch?v=cp2rL3sAFmI
//https://www.youtube.com/watch?v=-xtmTrhlwgg
public class DatabaseHelper extends SQLiteOpenHelper implements FuzzyFlagFinder {
    public static final String TAG = "DatabaseHelper";

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "projectsInfo.db";

    //table names
    public static final String TABLE_MODES = "modes";
    public static final String TABLE_FLAGS = "flags";
    public static final String TABLE_TRANSITION_ROWS = "transitionTableRows";
    public static final String TABLE_ACTIONS = "actions";
    public static final String TABLE_START_MODE = "startModeTable";

    //modes columns
    public static final String MODES_PROJECT = "project";
    public static final String MODES_MODE    = "name";
    public static final String MODES_ACTION  = "actions";
    public static final String MODES_TABLE   = "transitionTable";

    //flags columns
    public static final String FLAGS_PROJECT   = "project";
    public static final String FLAGS_FLAG      = "flag";
    public static final String FLAGS_CONDITION = "condition";
    public static final String FLAGS_GREATER   = "greaterThan";
    public static final String FLAGS_SENSOR    = "sensor";

    //transition tables rows columns
    public static final String TRANSITIONS_PROJECT = "project";
    public static final String TRANSITIONS_TABLE_NAME = "transitionTable";
    public static final String TRANSITIONS_ROW_NUM = "rowNum";
    public static final String TRANSITIONS_FLAG    = "flag";
    public static final String TRANSITIONS_MODE    = "mode";
    public static final String TRANSITIONS_ID      = "id";

    //actions columns
    public static final String ACTION_PROJECT  = "actionProject";
    public static final String ACTION_NAME     = "actionName";
    public static final String ACTION_LMP      = "actionLMP";
    public static final String ACTION_RMP      = "actionRMP";
    public static final String ACTION_RLC      = "actionRLC";
    public static final String ACTION_RRC      = "actionRRC";

    //start mode columns
    public static final String START_MODE_PROJECT = "project";
    public static final String START_MODE = "startingMode";

    public static String createTableStr(String tableName, String... columns) {
        StringBuilder stmt = new StringBuilder();
        stmt.append("CREATE TABLE IF NOT EXISTS " + tableName + "(");
        for (String column: columns) {
            stmt.append(column + " TEXT,");
        }
        stmt.replace(stmt.length() - 1, stmt.length(), ")");
        Log.i(TAG, stmt.toString());
        return stmt.toString();
    }

    public static String createKeyedTableStr(String tableName, String intKeyColumn, String... columns) {
        String creator = createTableStr(tableName, columns);
        creator = creator.substring(0, creator.length() - 1) + ", " + intKeyColumn + " INTEGER PRIMARY KEY)";
        Log.i(TAG, creator);
        return creator;
    }

    // Original tables
    //public static final String CREATE_TABLE_MODES  = "CREATE TABLE IF NOT EXISTS " + TABLE_MODES + "(" + MODES_PROJECT + " TEXT, " + MODES_MODE + " TEXT, " + MODES_ACTION + " TEXT, " + MODES_TABLE + " TEXT" + ")";
    //public static final String CREATE_TABLE_FLAGS  = "CREATE TABLE IF NOT EXISTS " + TABLE_FLAGS + "(" + FLAGS_PROJECT + " TEXT, " + FLAGS_FLAG + " TEXT, " + FLAGS_CONDITION + " TEXT, " + FLAGS_GREATER + " TEXT, " + FLAGS_SENSOR + " TEXT" + ")";
    //public static final String CREATE_TABLE_TRANSITION_ROWS = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSITION_ROWS + "(" + TRANSITIONS_PROJECT + " TEXT, " + TRANSITIONS_TABLE_NAME + " TEXT, " + TRANSITIONS_ROW_NUM + " TEXT, " + TRANSITIONS_FLAG + " TEXT, " + TRANSITIONS_MODE + " TEXT, " + TRANSITIONS_ID + " INTEGER PRIMARY KEY" + ")";
    //public static final String CREATE_TABLE_ACTIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTIONS + "(" + ACTION_PROJECT + " TEXT, " + ACTION_NAME + " TEXT, " + ACTION_LMP + " TEXT, " + ACTION_RMP + " TEXT, " + ACTION_RLC + " TEXT, " + ACTION_RRC + " TEXT " + ")";
    //public static final String CREATE_TABLE_START_MODE = "CREATE TABLE IF NOT EXISTS " + TABLE_START_MODE + "(" + START_MODE_PROJECT + " TEXT, " + START_MODE + " TEXT " + ")";

    public static final String CREATE_TABLE_MODES  = createTableStr(TABLE_MODES, MODES_PROJECT, MODES_MODE, MODES_ACTION, MODES_TABLE);
    public static final String CREATE_TABLE_FLAGS  = createTableStr(TABLE_FLAGS, FLAGS_PROJECT, FLAGS_FLAG, FLAGS_CONDITION, FLAGS_GREATER, FLAGS_SENSOR);
    public static final String CREATE_TABLE_TRANSITION_ROWS = createKeyedTableStr(TABLE_TRANSITION_ROWS, TRANSITIONS_ID, TRANSITIONS_PROJECT, TRANSITIONS_TABLE_NAME, TRANSITIONS_ROW_NUM, TRANSITIONS_FLAG, TRANSITIONS_MODE);
    public static final String CREATE_TABLE_ACTIONS = createTableStr(TABLE_ACTIONS, ACTION_PROJECT, ACTION_NAME, ACTION_LMP, ACTION_RMP, ACTION_RLC, ACTION_RRC);
    public static final String CREATE_TABLE_START_MODE = createTableStr(TABLE_START_MODE, START_MODE_PROJECT, START_MODE);

    // Auxiliary table names
    public static final String TABLE_DIFFERENCES = "Differences";

    // Difference columns
    public static final String DIFFERENCES_PROJECT = "project";
    public static final String DIFFERENCES_NAME = "name";
    public static final String DIFFERENCES_TERM_1 = "term1";
    public static final String DIFFERENCES_TERM_2 = "term2";
    public static final String DIFFERENCES_ABS = "abs";

    // Auxiliary table creation
    public static final String CREATE_TABLE_DIFFERENCES = createTableStr(TABLE_DIFFERENCES, DIFFERENCES_PROJECT, DIFFERENCES_NAME, DIFFERENCES_TERM_1, DIFFERENCES_TERM_2, DIFFERENCES_ABS);

    // Fuzzy logic table names
    public static final String TABLE_FUZZY_FLAGS = "FuzzyFlags";
    public static final String TABLE_DEFUZZIFIERS = "Defuzzifiers";
    public static final String TABLE_FUZZY_ACTIONS = "FuzzyActions";

    // Fuzzy flag columns
    public static final String FUZZY_FLAGS_TYPE = "encoding";
    public static final String FUZZY_FLAGS_ARG1 = "arg1";
    public static final String FUZZY_FLAGS_ARG2 = "arg2";
    public static final String FUZZY_FLAGS_ARG3 = "arg3";
    public static final String FUZZY_FLAGS_ARG4 = "arg4";

    // Defuzzifier columns
    public static final String DEFUZZY_PROJECT = "project";
    public static final String DEFUZZY_NAME = "name";
    public static final String DEFUZZY_SPEED_1 = "speed1";
    public static final String DEFUZZY_SPEED_2 = "speed2";

    // Fuzzy action columns
    public static final String FUZZY_ACTION_PROJECT = "project";
    public static final String FUZZY_ACTION_NAME = "name";
    public static final String FUZZY_ACTION_LEFT_FLAG = "leftFlag";
    public static final String FUZZY_ACTION_LEFT_DEFUZZIFIER = "leftDefuzzifier";
    public static final String FUZZY_ACTION_RIGHT_FLAG = "rightFlag";
    public static final String FUZZY_ACTION_RIGHT_DEFUZZIFIER = "rightDefuzzifier";

    // Fuzzy logic tables
    public static final String CREATE_TABLE_FUZZY_FLAGS  = createTableStr(TABLE_FUZZY_FLAGS, FLAGS_PROJECT, FLAGS_FLAG, FUZZY_FLAGS_TYPE, FUZZY_FLAGS_ARG1, FUZZY_FLAGS_ARG2, FUZZY_FLAGS_ARG3, FUZZY_FLAGS_ARG4, FLAGS_SENSOR);
    public static final String CREATE_TABLE_DEFUZZIFIERS = createTableStr(TABLE_DEFUZZIFIERS, DEFUZZY_PROJECT, DEFUZZY_NAME, DEFUZZY_SPEED_1, DEFUZZY_SPEED_2);
    public static final String CREATE_TABLE_FUZZY_ACTIONS = createTableStr(TABLE_FUZZY_ACTIONS, FUZZY_ACTION_PROJECT, FUZZY_ACTION_NAME, FUZZY_ACTION_LEFT_DEFUZZIFIER, FUZZY_ACTION_LEFT_FLAG, FUZZY_ACTION_RIGHT_DEFUZZIFIER, FUZZY_ACTION_RIGHT_FLAG);

    // Image/learning table names
    public static final String TABLE_IMAGE_LABELS = "labels";
    public static final String TABLE_IMAGES = "images";

    // Image columns
    public static final String IMAGE_LABEL = "label";
    public static final String IMAGE_CONTENTS = "image";
    public static final String IMAGE_PROJECT = "project";

    // Image/learning tables
    public static final String CREATE_TABLE_LABELS = createTableStr(TABLE_IMAGE_LABELS, IMAGE_PROJECT, IMAGE_LABEL);
    public static final String CREATE_TABLE_IMAGES = "CREATE TABLE IF NOT EXISTS " + TABLE_IMAGES + "(" + IMAGE_PROJECT + " TEXT, " + IMAGE_LABEL + " TEXT, " + IMAGE_CONTENTS + " BLOB)";

    private static ImageFactory imageData = null;
    private static TreeMap<String,Symbol> symbols = null;
    private static FuzzyFactory fuzzyFactory = null;
    private static TransitionTableFactory transitionTableFactory = null;
    private static NeuralNetFactory nets = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (imagesReady()) {
            if (symbols == null) {
                symbols = getSymbols();
            }
            if (fuzzyFactory == null) {
                fuzzyFactory = getFuzzyItems();
            }
            if (transitionTableFactory == null) {
                transitionTableFactory = getTransitionItems();
            }
        }
    }

    private TreeMap<String,Symbol> getSymbols() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_DIFFERENCES);

        TreeMap<String,Symbol> inputs = new TreeMap<>();
        String query = "SELECT * FROM " + TABLE_DIFFERENCES;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DIFFERENCES_NAME));
                inputs.put(name, new Symbol(name,
                        cursor.getString(cursor.getColumnIndexOrThrow(DIFFERENCES_TERM_1)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DIFFERENCES_TERM_2)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DIFFERENCES_ABS)).equals("true")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return inputs;
    }

    public boolean imagesReady() {
        return imageData != null;
    }

    public void setupImages(Context context) {
        if (!imagesReady()) {
            try {
                imageData = getImageData();
                nets = getNeuralNetworks(context);
                Log.i(TAG, "Images ready");
            } catch (Exception exc) {
                Log.i(TAG, "Problem: " + exc);
                throw exc;
            }
        }
    }

    private ImageFactory getImageData() {
        ImageFactory images = new ImageFactory();
        getAllLabels(images);
        getAllImages(images);
        return images;
    }

    private NeuralNetFactory getNeuralNetworks(Context context) {
        return NeuralNetFactory.loadAll(context);
    }

    public void addNeuralNetwork(ANN_MLP network, String targetLabel, int numHidden, Context context) {
        nets.addNeuralNet(network, targetLabel, numHidden, context);
    }

    public void getAllLabels(ImageFactory images) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_LABELS);
        String labelQuery = "SELECT * FROM " + TABLE_IMAGE_LABELS;
        Cursor cursor = db.rawQuery(labelQuery, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String label = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_LABEL));
                images.addLabel(label);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public void getAllImages(ImageFactory images) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_IMAGES);
        String query = "SELECT * FROM " + TABLE_IMAGES;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String label = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_LABEL));
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(IMAGE_CONTENTS));
                // From https://stackoverflow.com/questions/21113190/how-to-get-the-mat-object-from-the-byte-in-opencv-android
                Mat mat = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
                images.addImage(label, mat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public void addImage(String label, Mat image) {
        imageData.addImage(label, image);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_LABEL, label);
        values.put(IMAGE_PROJECT, "default");

        // From http://answers.opencv.org/question/2847/convert-mat-to-matofbyte-in-android/
        MatOfByte matOfByte = new MatOfByte();
        // encoding to png, so that your image does not lose information like with jpeg.
        Imgcodecs.imencode(".png", image, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        values.put(IMAGE_CONTENTS, byteArray);
        db.insert(TABLE_IMAGES, null, values);
        db.close();
    }

    public ArrayList<Symbol> getSymbolList() {
        return new ArrayList<>(symbols.values());
    }

    public ArrayList<String> getSensorAndSymbolNames() {
        ArrayList<String> names = new ArrayList<>();
        for (String sensor: SensedValues.SENSOR_NAMES) {
            names.add(sensor);
        }
        names.addAll(symbols.keySet());
        return names;
    }

    public ContentValues getSymbolValues(String project, Symbol symbol) {
        ContentValues values = new ContentValues();
        values.put(DIFFERENCES_PROJECT, project);
        values.put(DIFFERENCES_NAME, symbol.getName());
        values.put(DIFFERENCES_TERM_1, symbol.getSensorOne());
        values.put(DIFFERENCES_TERM_2, symbol.getSensorTwo());
        values.put(DIFFERENCES_ABS, Boolean.toString(symbol.absoluteValue()));
        return values;
    }

    public Symbol insertNewSymbol(String project) {
        SQLiteDatabase db = this.getWritableDatabase();
        Symbol toInsert = new Symbol("difference" + (symbols.size() + 1));
        db.insert(TABLE_DIFFERENCES, null, getSymbolValues(project, toInsert));
        db.close();
        return toInsert;
    }

    public void updateSymbol(Symbol newSymbol, String oldName) {
        Log.i(TAG, "updateSymbol: newSymbol: " + newSymbol);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getSymbolValues("default", newSymbol);
        db.update(TABLE_DIFFERENCES, values, DIFFERENCES_NAME + " = ?", new String[]{oldName});
        db.close();
        symbols.remove(oldName);
        symbols.put(newSymbol.getName(), newSymbol);

        logEntireTable(TABLE_DIFFERENCES);
    }

    public void deleteSymbol(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getSymbolValues("default", symbols.get(name));
        String selection = DIFFERENCES_NAME + " LIKE ?";
        String[] selectionArgs = { name };
        db.delete(TABLE_DIFFERENCES, selection, selectionArgs);
        db.close();
        symbols.remove(name);
    }

    @Override
    public void onCreate(SQLiteDatabase db) throws SQLException {
        Log.i(TAG, "Entering onCreate()");

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

        // Others
        db.execSQL(CREATE_TABLE_DIFFERENCES);
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
        Log.i(TAG, "Entering onUpgrade()");
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
                try {
                    factory.addMode(cursor.getString(1), cursor.getString(2), cursor.getString(3), fuzzyFactory);
                } catch (IllegalArgumentException exc) {
                    Log.i(TAG, "Can't create mode; " + exc.getMessage());
                }
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
        values.put(TRANSITIONS_TABLE_NAME, transitionTable);
        values.put(TRANSITIONS_ROW_NUM, rowNum);
        values.put(TRANSITIONS_FLAG, flag.getName());
        values.put(TRANSITIONS_MODE, mode.getName());
        long rowId = db.insert(TABLE_TRANSITION_ROWS, null, values);
        Row row = new Row(flag, mode, rowNum, rowId);
        Log.i(TAG,String.format("Inserting into table %s transition row: %s", transitionTable, row.toString()));
        db.close();

        transitionTableFactory.addTableRow(transitionTable, row);
        return row;
    }

    public ArrayList<TransitionTable> getTransitionTableList() throws SQLException {
        return transitionTableFactory.getTableList();
    }

    public void deleteTransitionRow(String tableName, long id) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = TRANSITIONS_TABLE_NAME + " LIKE ? AND " + TRANSITIONS_ID + " LIKE ?";
        String[] selectionArgs = { tableName, id + "" };

        db.delete(TABLE_TRANSITION_ROWS, selection, selectionArgs);
        db.close();

        transitionTableFactory.delTableRow(tableName, id);
    }

    public void renameTableRows(String oldName, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRANSITIONS_TABLE_NAME, newName);
        db.update(TABLE_TRANSITION_ROWS, values, TRANSITIONS_TABLE_NAME + " LIKE ?", new String[]{oldName});

        ContentValues modeUpdate = new ContentValues();
        modeUpdate.put(MODES_TABLE, newName);
        db.update(TABLE_MODES, modeUpdate, MODES_TABLE + " LIKE ?", new String[]{oldName});
        db.close();

        transitionTableFactory.renameTable(oldName, newName);
    }

    public void updateTransitionRow(long id, int rowNum, String transitionTable, Flag flag, Mode mode) throws SQLException {
        logEntireTable(TABLE_TRANSITION_ROWS);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRANSITIONS_PROJECT, "default");
        values.put(TRANSITIONS_TABLE_NAME, transitionTable);
        values.put(TRANSITIONS_ROW_NUM, rowNum);
        values.put(TRANSITIONS_FLAG, flag.getName());
        values.put(TRANSITIONS_MODE, mode.getName());

        String[] whereArgs = {transitionTable, id + ""};
        String whereClause = TRANSITIONS_TABLE_NAME + " LIKE ? AND " + TRANSITIONS_ID + " = ?";

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

    public ArrayList<SimpleSensorFlag> getSimpleSensorFlagList() {
        return transitionTableFactory.getSimpleSensorFlagList();
    }

    public ArrayList<Flag> getFlagList() {
        ArrayList<Flag> allFlags = new ArrayList<>();
        allFlags.addAll(getSimpleSensorFlagList());
        if (nets != null) {
            allFlags.addAll(nets.getAllNeuralNets());
        }
        return allFlags;
    }

    private void getAllFlags(TransitionTableFactory factory) {
        String query = "SELECT * FROM " + TABLE_FLAGS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                factory.addSimpleSensorFlag(cursor.getString(1), cursor.getString(4), cursor.getInt(3) == 1, cursor.getDouble(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public ContentValues getFlagValues(String project, SimpleSensorFlag flag) {
        ContentValues values = new ContentValues();
        values.put(FLAGS_PROJECT, project);
        values.put(FLAGS_FLAG, flag.getName());
        values.put(FLAGS_CONDITION, flag.getTriggerValue());
        values.put(FLAGS_GREATER, flag.isGreaterThan() ? 1 : 0);
        values.put(FLAGS_SENSOR, flag.getSensor());
        return values;
    }

    public void updateFlag(SimpleSensorFlag newFlag, SimpleSensorFlag oldFlag) throws SQLException {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getFlagValues("default", newFlag);
        String[] whereArgs = {oldFlag.getName()};

        db.update(TABLE_FLAGS, values, FLAGS_FLAG + " = ?", whereArgs);
        db.close();

        transitionTableFactory.replaceFlag(oldFlag.getName(), newFlag);
    }

    public SimpleSensorFlag insertNewFlag(String project) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        SimpleSensorFlag flag = new SimpleSensorFlag("flag" + (getFlagCount() + 1), SensedValues.SENSOR_NAMES[0], false, 100);
        db.insert(TABLE_FLAGS, null, getFlagValues(project, flag));
        db.close();

        transitionTableFactory.addSimpleSensorFlag(flag);
        return flag;
    }

    public void deleteFlag(SimpleSensorFlag flag) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = FLAGS_FLAG + " LIKE ?";
        String[] selectionArgs = { flag.getName() };

        db.delete(TABLE_FLAGS, selection, selectionArgs);
        db.close();
        transitionTableFactory.delFlag(flag.getName());
    }

    public ContentValues getActionValues(String project, Action action) {
        ContentValues values = new ContentValues();
        values.put(ACTION_PROJECT, project);
        values.put(ACTION_NAME, action.getName());
        values.put(ACTION_LMP, action.getLeftMotorInput());
        values.put(ACTION_RMP, action.getRightMotorInput());
        values.put(ACTION_RLC, action.getRLCint());
        values.put(ACTION_RRC, action.getRRCint());
        return values;
    }

    public Action insertNewAction(String project) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        Action action = transitionTableFactory.makeNewAction();
        db.insert(TABLE_ACTIONS, null, getActionValues(project, action));
        db.close();

        return action;
    }

    public void updateAction(Action oldAction, Action newAction) throws SQLException { //https://abhiandroid.com/database/sqlite
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getActionValues("default", newAction);
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

    public boolean hasAction(String name) {
        return transitionTableFactory.hasAction(name);
    }

    public Action getAction(String name) {
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
        return fuzzyFactory.getFuzzyFlagList();
    }

    private TransitionTableFactory getTransitionItems() {
        Log.i(TAG,"getTransitionItems(): Look into the database");
        logEntireTable(TABLE_TRANSITION_ROWS);
        Log.i(TAG, "Finished database peek.");
        TransitionTableFactory factory = new TransitionTableFactory();
        getAllFlags(factory);
        getAllActions(factory);
        ArrayList<DatabaseTransitionRow> rows = getTransitionRows();
        factory.addEmptyTablesFrom(rows);
        getAllModes(factory);
        factory.makeTableRowsFrom(rows);
        return factory;
    }

    private ArrayList<DatabaseTransitionRow> getTransitionRows() {
        logEntireTable(TABLE_TRANSITION_ROWS);
        ArrayList<DatabaseTransitionRow> rows = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_TRANSITION_ROWS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Duple<String, Integer> nameAndPos = new Duple( cursor.getString(1), cursor.getInt(2));
                Log.i(TAG, String.format("Row contents: %s,%s,%s,%s,%s", cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
                DatabaseTransitionRow row = new DatabaseTransitionRow(
                        cursor.getString(cursor.getColumnIndexOrThrow(TRANSITIONS_PROJECT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TRANSITIONS_TABLE_NAME)),
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

    public void logColumns(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor dbCursor = db.query(table, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        StringBuilder sb = new StringBuilder();
        for (String column: columnNames) {
            sb.append(column);
            sb.append(",");
        }
        Log.i(TAG, "Columns for " + table + ": " + sb.toString());
    }

    public void logEntireTable(String table) {
        logColumns(table);
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + table;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            Log.i(TAG, "Table " + table + " has data");
            cursor.moveToFirst();
            do {
                StringBuilder row = new StringBuilder();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    row.append(cursor.getString(i));
                    row.append(',');
                }
                Log.i(TAG, row.toString());
            }while (cursor.moveToNext());
            Log.i(TAG, "Finished with table " + table);
        } else {
            Log.i(TAG, "Table has no data");
        }
        cursor.close();
        db.close();
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
        logEntireTable(TABLE_FUZZY_FLAGS);
        Log.i(TAG,"Look up to see the table");

        String query = "SELECT * FROM " + TABLE_FUZZY_FLAGS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                FuzzyFlagRow row = new FuzzyFlagRow(
                        cursor.getString(cursor.getColumnIndexOrThrow(FLAGS_PROJECT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FLAGS_FLAG)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_FLAGS_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_FLAGS_ARG1)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_FLAGS_ARG2)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_FLAGS_ARG3)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_FLAGS_ARG4)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FLAGS_SENSOR)));
                Log.i(TAG, "Row from Fuzzy flag table:" + row);
                factory.addFlagRow(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        factory.generateFuzzyFlags();
    }

    public int getFuzzyFlagCount() {
        return fuzzyFactory.numFuzzyFlags();
    }

    public boolean fuzzyFlagExists(String name) {
        return fuzzyFactory.fuzzyFlagExists(name);
    }

    public FuzzyFlag getFuzzyFlag(String name) {
        return fuzzyFactory.getFuzzyFlag(name);
    }

    public ContentValues fuzzyFlagValues(String project, FuzzyFlag flag) {
        ContentValues values = new ContentValues();
        values.put(FLAGS_PROJECT, project);
        values.put(FLAGS_FLAG, flag.getName());
        values.put(FLAGS_SENSOR, flag.getSensor());
        values.put(FUZZY_FLAGS_ARG1, flag.getArg(0));
        values.put(FUZZY_FLAGS_ARG2, flag.getArg(1));
        values.put(FUZZY_FLAGS_ARG3, flag.getArg(2));
        values.put(FUZZY_FLAGS_ARG4, flag.getArg(3));
        values.put(FUZZY_FLAGS_TYPE, flag.getType().name());
        return values;
    }

    public ContentValues defuzzifierValues(String project, Defuzzifier defuzz) {
        ContentValues values = new ContentValues();
        values.put(DEFUZZY_PROJECT, project);
        values.put(DEFUZZY_NAME, defuzz.getName());
        values.put(DEFUZZY_SPEED_1, defuzz.getSpeed1());
        values.put(DEFUZZY_SPEED_2, defuzz.getSpeed2());
        return values;
    }

    public ContentValues fuzzyActionValues(String project, FuzzyAction action) {
        ContentValues values = new ContentValues();
        values.put(FUZZY_ACTION_PROJECT, project);
        values.put(FUZZY_ACTION_NAME, action.getName());
        values.put(FUZZY_ACTION_LEFT_FLAG, action.getLeft().getFlag().getName());
        values.put(FUZZY_ACTION_LEFT_DEFUZZIFIER, action.getLeft().getDefuzzifier().getName());
        values.put(FUZZY_ACTION_RIGHT_FLAG, action.getRight().getFlag().getName());
        values.put(FUZZY_ACTION_RIGHT_DEFUZZIFIER, action.getRight().getDefuzzifier().getName());
        return values;
    }

    public FuzzyFlag insertNewFuzzyFlag(String project) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        FuzzyFlag flag = fuzzyFactory.generateDefaultFlag(project);
        db.insert(TABLE_FUZZY_FLAGS, null, fuzzyFlagValues(project, flag));
        db.close();
        return flag;
    }

    public Defuzzifier getDefuzzifier(String name) {
        return fuzzyFactory.getDefuzzifier(name);
    }

    public Defuzzifier insertNewDefuzzifier(String project) {
        SQLiteDatabase db = this.getWritableDatabase();
        Defuzzifier defuzz = fuzzyFactory.generateDefaultDefuzzifier(project);
        db.insert(TABLE_DEFUZZIFIERS, null, defuzzifierValues(project, defuzz));
        db.close();
        return defuzz;
    }

    public void updateFuzzyFlag(FuzzyFlag newFlag, String oldFlagName) throws SQLException {

        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = {oldFlagName};

        db.update(TABLE_FUZZY_FLAGS, fuzzyFlagValues("default", newFlag), FLAGS_FLAG + " = ?", whereArgs);
        db.close();

        fuzzyFactory.updateFuzzyFlag(newFlag, oldFlagName);
    }

    public void updateDefuzzifier(Defuzzifier updated, String oldName) throws SQLException {

        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = {oldName};

        db.update(TABLE_DEFUZZIFIERS, defuzzifierValues("default", updated), DEFUZZY_NAME + " = ?", whereArgs);
        db.close();

        fuzzyFactory.updateDefuzzifier(updated, oldName);
    }

    public void deleteFuzzyFlag(FuzzyFlag flag) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = FLAGS_FLAG + " LIKE ?";
        String[] selectionArgs = { flag.getName() };

        db.delete(TABLE_FUZZY_FLAGS, selection, selectionArgs);
        db.close();
        fuzzyFactory.delFuzzyFlag(flag.getName());

        logEntireTable(TABLE_FUZZY_FLAGS);
    }

    public void deleteDefuzzifier(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = DEFUZZY_NAME + " LIKE ?";
        String[] selectionArgs = { name };

        db.delete(TABLE_DEFUZZIFIERS, selection, selectionArgs);
        db.close();
        fuzzyFactory.delDefuzzifier(name);
    }

    public ArrayList<Defuzzifier> getDefuzzifierList() {
        return fuzzyFactory.allDefuzzifiers();
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
        cursor.close();
        db.close();
    }

    private void getAllFuzzyActions(FuzzyFactory factory) throws SQLException {
        String query = "SELECT * FROM " + TABLE_FUZZY_ACTIONS;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                try {
                    factory.addFuzzyAction(
                            cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_ACTION_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_ACTION_LEFT_FLAG)),
                            cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_ACTION_LEFT_DEFUZZIFIER)),
                            cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_ACTION_RIGHT_FLAG)),
                            cursor.getString(cursor.getColumnIndexOrThrow(FUZZY_ACTION_RIGHT_DEFUZZIFIER)));
                } catch (IllegalStateException exc) {
                    logEntireTable(TABLE_FLAGS);
                    logEntireTable(TABLE_FUZZY_ACTIONS);
                    logEntireTable(TABLE_FUZZY_FLAGS);
                    logEntireTable(TABLE_DEFUZZIFIERS);
                    throw exc;
                }
            } while (cursor.moveToNext());
        }
    }

    public ArrayList<FuzzyAction> getFuzzyActionList() {
        return fuzzyFactory.allFuzzyActions();
    }

    public ArrayList<InstructionCreator> getInstructionCreatorList() {
        ArrayList<InstructionCreator> result = new ArrayList<>();
        result.addAll(getActionList());
        result.addAll(getFuzzyActionList());
        return result;
    }

    public InstructionCreator getInstructionCreator(String name) {
        if (hasAction(name)) {
            return getAction(name);
        } else if (hasFuzzyAction(name)) {
            return getFuzzyAction(name);
        } else {
            throw new IllegalArgumentException("'" + name + "' is not an action");
        }
    }

    private InstructionCreator getFuzzyAction(String name) {
        return fuzzyFactory.getFuzzyAction(name);
    }

    private boolean hasFuzzyAction(String name) {
        return fuzzyFactory.hasFuzzyAction(name);
    }

    public FuzzyAction insertNewFuzzyAction(String project) {
        SQLiteDatabase db = this.getWritableDatabase();
        FuzzyAction action = fuzzyFactory.generateDefaultFuzzyAction(project);
        db.insert(TABLE_FUZZY_ACTIONS, null, fuzzyActionValues(project, action));
        db.close();
        return action;
    }

    public void deleteFuzzyAction(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = FUZZY_ACTION_NAME + " LIKE ?";
        String[] selectionArgs = { name };

        db.delete(TABLE_FUZZY_ACTIONS, selection, selectionArgs);
        db.close();
        fuzzyFactory.delFuzzyAction(name);
    }

    public void updateFuzzyAction(FuzzyAction updated, String oldName) throws SQLException {

        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = {oldName};

        db.update(TABLE_FUZZY_ACTIONS, fuzzyActionValues("default", updated), DEFUZZY_NAME + " = ?", whereArgs);
        db.close();

        fuzzyFactory.updateFuzzyAction(updated, oldName);
    }

    public ArrayList<String> getAllLabels() {
        return imageData.getAllLabelNames();
    }

    public String createNewLabel() {
        String label = imageData.generateLabel();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_LABEL, label);
        values.put(IMAGE_PROJECT, "default");
        db.insert(TABLE_IMAGE_LABELS, null, values);
        db.close();
        return label;
    }

    public void updateLabel(String original, String updated) {
        imageData.renameLabel(original, updated);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_LABEL, updated);
        db.update(TABLE_IMAGES, values, IMAGE_LABEL + " = ?", new String[]{original});
        db.update(TABLE_IMAGE_LABELS, values, IMAGE_LABEL + " = ?", new String[]{original});
        db.close();
    }

    public int getNumStoredImages() {
        return imagesReady() ? imageData.numImages() : 0;
    }

    public Mat getImage(int i) {
        return imageData.getImage(i);
    }

    public String getImageLabel(int i) {
        return imageData.getLabel(i);
    }

    public int getImageWidths() {
        return imageData.imageWidths();
    }

    public int getImageHeights() {
        return imageData.imageHeights();
    }

    public NeuralNetTrainingData makeTrainingTestingSets(String targetLabel, double proportionToTrain) {
        int numInTraining = (int)(getNumStoredImages() * proportionToTrain);
        ArrayList<Duple<WrappedLabel,Mat>> trainingImages = imageData.getShuffledImageList();
        ArrayList<Duple<WrappedLabel,Mat>> testImages = new ArrayList<>();
        while (trainingImages.size() > numInTraining) {
            testImages.add(trainingImages.remove(trainingImages.size() - 1));
        }
        return new NeuralNetTrainingData(trainingImages, testImages, targetLabel);
    }


}

