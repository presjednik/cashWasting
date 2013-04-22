package hr.fenster.gang.cashwasting;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLite extends SQLiteOpenHelper {

	public static final String TABLE_CASH = "cashwasting";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_CASH = "cash";
	public static final String COLUMN_DESCRIPTION = "desc";

	private static final String DATABASE_NAME = "cash.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + TABLE_CASH
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_DATE + " datetime default current_date, " + COLUMN_TIME
			+ " datetime default current_time, " + COLUMN_CASH + " double, "
			+ COLUMN_DESCRIPTION + " text );";

	public MySQLite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLite.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CASH);
		onCreate(db);
	}

}
