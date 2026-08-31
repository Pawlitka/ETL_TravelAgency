package OfferLayer;

import javax.swing.table.AbstractTableModel;

public class OfferModel extends AbstractTableModel {

    private final Object[][] data;

    public OfferModel(Object[][] data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return data.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    public Object[] getRow(int rowIndex) {
        return data[rowIndex];
    }
}
