package spellingquizz;

import javax.swing.table.AbstractTableModel;



public class TableModelAdapter extends AbstractTableModel {
	
	private SpellingStatsModel _adaptee;
	private static final String[] _columnNames = {"Word","Mastered","Faulted","Failed"};

	
	public TableModelAdapter(SpellingStatsModel spellinglistmodel) {
		_adaptee = spellinglistmodel;
	}
	
	@Override
	public String getColumnName(int index) {
	    return _columnNames[index];
	}
	
	@Override
	public int getColumnCount() {
		return _columnNames.length;
	}

	@Override
	public int getRowCount() {
		return _adaptee.getNumWords();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Word targetWord = _adaptee.getWordAt(row);
		Object result = null;

		switch(col) {
		case 0: // Word
			result = targetWord.toString();
			break;
		case 1: // Mastered
			result = targetWord.getMastered();
			break;
		case 2: // Faulted
			result = targetWord.getFaulted();
			break;
		case 3: // Failed
			result = targetWord.getFailed();
			break;
		}
		return result;
	}
	
	
	
}
