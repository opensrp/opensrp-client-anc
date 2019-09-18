package org.smartregister.anc.library.cursor;

import java.util.Date;

public class AdvancedMatrixCursor extends net.sqlcipher.MatrixCursor {
    public AdvancedMatrixCursor(String[] columnNames) {
        super(columnNames);
    }

    @Override
    public long getLong(int column) {
        try {
            return super.getLong(column);
        } catch (NumberFormatException e) {
            return (new Date()).getTime();
        }
    }

}