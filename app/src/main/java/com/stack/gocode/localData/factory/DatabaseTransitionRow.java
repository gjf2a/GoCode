package com.stack.gocode.localData.factory;

/**
 * Created by gabriel on 10/26/18.
 */

public class DatabaseTransitionRow {
    String project, name, flagName, modeName;
    int row;
    long id;

    public DatabaseTransitionRow(String project, String tableName, String flagName, String modeName, int row, long id) {
        this.project = project;
        this.name = tableName;
        this.flagName = flagName;
        this.modeName = modeName;
        this.row = row;
        this.id = id;
    }
}
