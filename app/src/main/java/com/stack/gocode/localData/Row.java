package com.stack.gocode.localData;

public class Row {
    private Duple<Flag, Mode> row;
    int rowNum;
    int rowId;

    public Row(Flag flag, Mode mode) {
        row = new Duple<Flag, Mode>(flag, mode);
    }

    public Flag getFirst() {
        return row.getFirst();
    }

    public Mode getSecond() {
        return row.getSecond();
    }

    public void setFirst(Flag flag) {row.setFirst(flag);}

    public void setSecond(Mode mode) {row.setSecond(mode);}

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getRowId() {
        return rowId;
    }

    public int getRowNum() {
        return rowNum;
    }
}
