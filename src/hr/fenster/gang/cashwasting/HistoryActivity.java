package hr.fenster.gang.cashwasting;

import hr.fenster.gang.cashwasting.type.Cash;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class HistoryActivity extends Activity {

	private final CashDataSource datasource = new CashDataSource(this);

	private double sumCash;
	private TextView wasted_history;
	private ScrollView sv;
	private LinearLayout date_time_history;
	private EditText start_date;
	private EditText end_date;
	private Button changeStartDate;
	private Button changeEndDate;
	private int year;
	private int month;
	private int day;

	static final int DATE_DIALOG_ID_START = 998;
	static final int DATE_DIALOG_ID_END = 999;

	private boolean startOrEnd = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_layout);

		datasource.open();

		wasted_history = (TextView) findViewById(R.id.text_wasted_history);
		wasted_history.setVisibility(View.INVISIBLE);

		sv = (ScrollView) findViewById(R.id.scroll_cashs_history);
		sv.setVisibility(View.INVISIBLE);
		date_time_history = (LinearLayout) sv
				.findViewById(R.id.linear_dates_time_history);

		start_date = (EditText) findViewById(R.id.edit_start);
		end_date = (EditText) findViewById(R.id.edit_end);

		changeStartDate = (Button) findViewById(R.id.change_start_date);
		changeEndDate = (Button) findViewById(R.id.change_end_date);

		Button buttonSearch = (Button) findViewById(R.id.history_button);

		buttonSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addCashToLayout(date_time_history);
				setWastedMoneyText(wasted_history);
				wasted_history.setVisibility(View.VISIBLE);
				sv.setVisibility(View.VISIBLE);
			}
		});

		changeStartDate.setOnClickListener(new OnClickListener() {

			//@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID_START);
				startOrEnd = false;
			}
		});

		changeEndDate.setOnClickListener(new OnClickListener() {

			//@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID_END);
				startOrEnd = true;
			}
		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		switch (id) {
		case DATE_DIALOG_ID_START:
			return new DatePickerDialog(this, datePickerListener, year, month,
					day);
		case DATE_DIALOG_ID_END:
			return new DatePickerDialog(this, datePickerListener, year, month,
					day);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			// set selected date into textview
			StringBuilder builder;
			if (month + 1 < 10 && day >= 10) {
				builder = new StringBuilder().append(year).append("-0")
						.append(month + 1).append("-").append(day).append(" ");
			}else if(month + 1 >= 10 && day >= 10){
				builder = new StringBuilder().append(year).append("-")
						.append(month + 1).append("-").append(day).append(" ");
			}else if(month + 1 < 10 && day < 10){
				builder = new StringBuilder().append(year).append("-0")
						.append(month + 1).append("-0").append(day).append(" ");
			}else {
				builder = new StringBuilder().append(year).append("-")
						.append(month + 1).append("-0").append(day).append(" ");
			}
			
			if (startOrEnd) {
				start_date.setText(builder);
			} else {
				end_date.setText(builder);
			}

		}
	};

	private void addCashToLayout(final LinearLayout layout) {
		System.out.println("Start: " + start_date.getText() + " end: "
				+ end_date.getText());
		List<Cash> list = datasource.getWastedCashStartEndDate(start_date.getText().toString(), end_date.getText().toString());

		layout.removeAllViews();
		for (final Cash c : list) {
			final LinearLayout l = setDateTimeCash(c);
			l.setMinimumHeight(30);

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);

			layoutParams.setMargins(0, 0, 0, 5);

			layout.addView(l, layoutParams);

			l.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					final AlertDialog.Builder alert = new AlertDialog.Builder(
							HistoryActivity.this);
					TextView text = new TextView(getApplicationContext());
					text.setText("Do you want to delete this entry?");
					alert.setView(text);
					alert.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									datasource.deleteCash(c);
									layout.removeView(l);
									setWastedMoneyText(wasted_history);
								}
							});

					alert.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
								}
							});
					alert.show();
				}
			});
		}
	}

	public void getSumCash() {
		List<Cash> list = datasource.getWastedCashStartEndDate(start_date.getText().toString(), end_date.getText().toString());
		sumCash = 0.0;

		for (Cash c : list) {
			sumCash += c.getCash();
		}
	}

	private void setWastedMoneyText(TextView wasted) {
		getSumCash();
		wasted.setText("Wasted: " + String.valueOf(sumCash) + " kn");
	}

	private LinearLayout setDateTimeCash(Cash c) {
		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.layout_dates_time, null);

		TextView t = (TextView) v.findViewById(R.id.textView_date);
		t.setTextSize(Float.valueOf(20));
		t.setText(c.getDate());

		TextView t2 = (TextView) v.findViewById(R.id.textView_time);
		t2.setTextSize(Float.valueOf(20));
		t2.setText(c.getTime());

		TextView t3 = (TextView) v.findViewById(R.id.textView_cash);
		t3.setTextSize(Float.valueOf(20));
		t3.setText(String.valueOf(c.getCash()) + "$");

		TextView t4 = (TextView) v.findViewById(R.id.textView_desc);
		t4.setTextSize(Float.valueOf(20));
		t4.setText(c.getDesc());

		return (LinearLayout) v;
	}

}
