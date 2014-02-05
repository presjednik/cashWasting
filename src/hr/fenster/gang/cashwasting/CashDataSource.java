package hr.fenster.gang.cashwasting;

import hr.fenster.gang.cashwasting.type.Cash;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CashDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLite dbHelper;
	private String[] allColumns = { MySQLite.COLUMN_ID, MySQLite.COLUMN_DATE,
			MySQLite.COLUMN_TIME, MySQLite.COLUMN_CASH,
			MySQLite.COLUMN_DESCRIPTION };

	public CashDataSource(Context context) {
		dbHelper = new MySQLite(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long createCash(double cash, String desc) {
		open();

		ContentValues values = new ContentValues();
		values.put(MySQLite.COLUMN_CASH, cash);
		values.put(MySQLite.COLUMN_DESCRIPTION, desc);
		long insertId = database.insert(MySQLite.TABLE_CASH, null, values);

		return insertId;
	}

	public long createDate(double cash, String desc, Date date, Time time) {
		open();

		ContentValues values = new ContentValues();
		values.put(MySQLite.COLUMN_DATE, date.toString());
		values.put(MySQLite.COLUMN_TIME, time.toString());
		values.put(MySQLite.COLUMN_CASH, cash);
		values.put(MySQLite.COLUMN_DESCRIPTION, desc);

		long insertId = database.insert(MySQLite.TABLE_CASH, null, values);
		return insertId;

	}

	public List<Cash> getThisMonthCash() {

		List<Cash> cashs = new ArrayList<Cash>();
		open();

		Cursor c = database.rawQuery("SELECT * FROM " + MySQLite.TABLE_CASH
				+ " WHERE strftime('%Y-%m-%d'," + MySQLite.COLUMN_DATE
				+ ") >= date('now','start of month') AND strftime('%Y-%m-%d',"
				+ MySQLite.COLUMN_DATE + ") <= date('now') ORDER BY "
				+ MySQLite.COLUMN_DATE + " DESC, " + MySQLite.COLUMN_TIME
				+ " DESC", null);

		c.moveToFirst();
		while (!c.isAfterLast()) {
			Cash cash = cursorToCash(c);
			cashs.add(cash);
			c.moveToNext();
		}
		c.close();
		return cashs;
	}

	public void deleteCash(Cash cash) {
		long id = cash.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(MySQLite.TABLE_CASH, MySQLite.COLUMN_ID + " = " + id,
				null);
	}

	public List<Cash> getAllCashs() {
		List<Cash> cashs = new ArrayList<Cash>();

		Cursor cursor = database.query(true, MySQLite.TABLE_CASH, allColumns, null,
				null, MySQLite.COLUMN_DESCRIPTION, null, "COUNT(*) DESC", "4");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Cash cash = cursorToCash(cursor);
			cashs.add(cash);
			cursor.moveToNext();
		}
		cursor.close();
		return cashs;
	}
	
	public List<Cash> getWastedCashStartEndDate(String start, String end) {
		List<Cash> cashs = new ArrayList<Cash>();

		Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLite.TABLE_CASH
				+ " WHERE " + MySQLite.COLUMN_DATE
				+ " BETWEEN " + "'" + start + "'" + " AND " + "'" + end + "'" + " ORDER BY "
				+ MySQLite.COLUMN_DATE + " DESC, " + MySQLite.COLUMN_TIME
				+ " DESC", null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Cash cash = cursorToCash(cursor);
			cashs.add(cash);
			cursor.moveToNext();
		}
		cursor.close();
		return cashs;
	}

	public List<Cash> getWeekCashs() {
		List<Cash> cashs = new ArrayList<Cash>();

		Calendar calendar = Calendar.getInstance();

		Cursor cursor = null;

		int day = calendar.get(Calendar.DAY_OF_WEEK);

		switch (day) {
		case 1:
			cursor = database.rawQuery("SELECT * FROM " + MySQLite.TABLE_CASH
					+ " WHERE strftime('%Y-%m-%d'," + MySQLite.COLUMN_DATE
					+ ") >= date('now','-6 days') AND strftime('%Y-%m-%d',"
					+ MySQLite.COLUMN_DATE + ") <= date('now') ORDER BY "
					+ MySQLite.COLUMN_DATE + " DESC, " + MySQLite.COLUMN_TIME
					+ " DESC", null);
			break;

		case 2:
			cursor = database.rawQuery("SELECT * FROM " + MySQLite.TABLE_CASH
					+ " WHERE strftime('%Y-%m-%d'," + MySQLite.COLUMN_DATE
					+ ") >= date('now', '-0 days') AND strftime('%Y-%m-%d',"
					+ MySQLite.COLUMN_DATE + ") <= date('now') ORDER BY "
					+ MySQLite.COLUMN_DATE + " DESC, " + MySQLite.COLUMN_TIME
					+ " DESC", null);
			break;

		case 3:
			cursor = database.rawQuery("SELECT * FROM " + MySQLite.TABLE_CASH
					+ " WHERE strftime('%Y-%m-%d'," + MySQLite.COLUMN_DATE
					+ ") >= date('now','-1 days') AND strftime('%Y-%m-%d',"
					+ MySQLite.COLUMN_DATE + ") <= date('now') ORDER BY "
					+ MySQLite.COLUMN_DATE + " DESC, " + MySQLite.COLUMN_TIME
					+ " DESC", null);
			break;

		case 4:
			cursor = database.rawQuery("SELECT * FROM " + MySQLite.TABLE_CASH
					+ " WHERE strftime('%Y-%m-%d'," + MySQLite.COLUMN_DATE
					+ ") >= date('now','-2 days') AND strftime('%Y-%m-%d',"
					+ MySQLite.COLUMN_DATE + ") <= date('now') ORDER BY "
					+ MySQLite.COLUMN_DATE + " DESC, " + MySQLite.COLUMN_TIME
					+ " DESC", null);
			break;

		case 5:
			cursor = database.rawQuery("SELECT * FROM " + MySQLite.TABLE_CASH
					+ " WHERE strftime('%Y-%m-%d'," + MySQLite.COLUMN_DATE
					+ ") >= date('now','-3 days') AND strftime('%Y-%m-%d',"
					+ MySQLite.COLUMN_DATE + ") <= date('now') ORDER BY "
					+ MySQLite.COLUMN_DATE + " DESC, " + MySQLite.COLUMN_TIME
					+ " DESC", null);
			break;

		case 6:
			cursor = database.rawQuery("SELECT * FROM " + MySQLite.TABLE_CASH
					+ " WHERE strftime('%Y-%m-%d'," + MySQLite.COLUMN_DATE
					+ ") >= date('now','-4 days') AND strftime('%Y-%m-%d',"
					+ MySQLite.COLUMN_DATE + ") <= date('now') ORDER BY "
					+ MySQLite.COLUMN_DATE + " DESC, " + MySQLite.COLUMN_TIME
					+ " DESC", null);
			break;

		case 7:
			cursor = database.rawQuery("SELECT * FROM " + MySQLite.TABLE_CASH
					+ " WHERE strftime('%Y-%m-%d'," + MySQLite.COLUMN_DATE
					+ ") >= date('now','-5 days') AND strftime('%Y-%m-%d',"
					+ MySQLite.COLUMN_DATE + ") <= date('now') ORDER BY "
					+ MySQLite.COLUMN_DATE + " DESC, " + MySQLite.COLUMN_TIME
					+ " DESC", null);
			break;
		}

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Cash cash = cursorToCash(cursor);
			cashs.add(cash);
			cursor.moveToNext();
		}
		cursor.close();
		return cashs;
	}

	private Cash cursorToCash(Cursor cursor) {
		Cash cash = new Cash();
		cash.setId(cursor.getLong(0));
		cash.setDate(cursor.getString(1));
		cash.setTime(cursor.getString(2));
		cash.setCash(cursor.getDouble(3));
		cash.setDesc(cursor.getString(4));
		return cash;
	}
}
