package com.stack.gocode.localData;

public class Row {
    private Flag flag;
    private Mode mode;
    private int rowNum;
    private long rowId;

    public Row(Flag flag, Mode mode, int rowNum, long rowId) {
        this.flag = flag;
        this.mode = mode;
        this.rowNum = rowNum;
        this.rowId = rowId;
    }

    public boolean isUsable() {
        return flag.isUsable() && mode.isUsable();
    }

    public Row(Row src) {
        this(src.getFlag(), src.getMode(), src.getRowNum(), src.getRowId());
    }

    public Flag getFlag() {
        return flag;
    }

    public Mode getMode() {
        return mode;
    }

    public long getRowId() {
        return rowId;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setMode(Mode mode) {this.mode = mode;}

    public void setFlag(Flag flag) {this.flag = flag;}

    @Override
    public String toString() {
        return String.format("Row:%s;%s;%d;id:%d", mode, flag, rowNum, rowId);
    }

    @Override
    public boolean equals(Object other) {
        return this.toString().equals(other.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
