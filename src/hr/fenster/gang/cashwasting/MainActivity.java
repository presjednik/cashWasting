package hr.fenster.gang.cashwasting;

import hr.fenster.gang.cashwasting.type.Cash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private final CashDataSource datasource = new CashDataSource(this);
	private boolean show = true;
	private double sumCash;
	private double monthSumCash;
	private TextView monthWasted;
	private TextView wasted;
	private LinearLayout date_time;
	private LinearLayout month_date_time;
	private List<String> descriptions = new ArrayList<String>();
	private String[] options = { "Delete", "Edit" };

	public Context c = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		datasource.open();

		LinearLayout tabLayout = (LinearLayout) findViewById(R.id.tab_layout);
		monthWasted = (TextView) tabLayout.findViewById(R.id.month_text_wasted);
		// monthWasted = (TextView) findViewById(R.id.text_wasted_month);
		wasted = (TextView) tabLayout.findViewById(R.id.text_wasted);

		ScrollView sv = (ScrollView) findViewById(R.id.scroll_cashs_week);
		ScrollView month_sv = (ScrollView) findViewById(R.id.scroll_cashs_month);
		date_time = (LinearLayout) sv.findViewById(R.id.linear_dates_time_week);
		month_date_time = (LinearLayout) month_sv
				.findViewById(R.id.linear_dates_time_month);

		Button buttonAdd = (Button) findViewById(R.id.button_add);

		buttonAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addEntryAlertDialog("New entry", null);
			}
		});

		addCashToLayout(date_time);
		addMonthCashToLayout(month_date_time);

		date_time.setVisibility(View.GONE);
		month_date_time.setVisibility(View.GONE);

		wasted.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				show = true;
				setMainScrollView();
			}
		});

		monthWasted.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				show = false;
				setMainScrollView();
			}
		});

		setMainScrollView();

		setMonthWastedMoneyText(monthWasted);
		setWastedMoneyText(wasted);
	}

	private void setMainScrollView() {
		if (show) {
			date_time.setVisibility(View.VISIBLE);
			month_date_time.setVisibility(View.GONE);
			wasted.setTextColor(Color.WHITE);
			monthWasted.setTextColor(Color.GRAY);
		} else {
			date_time.setVisibility(View.GONE);
			month_date_time.setVisibility(View.VISIBLE);
			wasted.setTextColor(Color.GRAY);
			monthWasted.setTextColor(Color.WHITE);
		}

	}

	private void addEntryAlertDialog(String title, final Cash c) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				MainActivity.this);
		alert.setTitle(title);

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout l = (LinearLayout) inflater.inflate(R.layout.dialog, null);

		fillDescriptions();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				MainActivity.this, android.R.layout.simple_list_item_1,
				descriptions);

		final EditText inputCash = (EditText) l.findViewById(R.id.cash_input);

		final EditText inputDate = (EditText) l.findViewById(R.id.date_input);

		final EditText inputTime = (EditText) l.findViewById(R.id.time_input);

		final AutoCompleteTextView inputDesc = (AutoCompleteTextView) l
				.findViewById(R.id.desc_input);

		if (c != null) {
			inputCash.setText(String.valueOf(c.getCash()));
			inputDate.setText(c.getDate());
			inputTime.setText(c.getTime());
			inputDesc.setText(c.getDesc());
		}

		inputDesc.setThreshold(0);
		inputDesc.setAdapter(adapter);
		inputDesc.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				inputDesc.showDropDown();
				return false;
			}
		});

		if (c == null) {
			java.util.Calendar cal = java.util.Calendar.getInstance();
			java.util.Date today = cal.getTime();
			long time = System.currentTimeMillis();
			java.sql.Date date = new java.sql.Date(time);
			inputTime.setText(new Time(today.getTime()).toString());
			inputDate.setText(date.toString());
		}

		alert.setView(l);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

				if (c != null) {
					datasource.deleteCash(c);
				}

				String dobule_cash = inputCash.getText().toString();

				if (!dobule_cash.equals("")) {
					double d = Double.parseDouble(dobule_cash);
					datasource.createDate(d, inputDesc.getText().toString(),
							Date.valueOf(inputDate.getText().toString()),
							Time.valueOf(inputTime.getText().toString()));

					addCashToLayout(date_time);
					addMonthCashToLayout(month_date_time);
					setWastedMoneyText(wasted);
					setMonthWastedMoneyText(monthWasted);
				} else
					Toast.makeText(getApplicationContext(),
							"Please enter amount of cash", Toast.LENGTH_LONG)
							.show();
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
	}

	private void fillDescriptions() {

		List<Cash> cashes = new ArrayList<Cash>();
		cashes.addAll(datasource.getAllCashs());

		descriptions.clear();
		for (Cash c : cashes) {
			descriptions.add(c.getDesc());
		}
	}

	private void addCashToLayout(final LinearLayout layout) {
		List<Cash> list = datasource.getWeekCashs();

		layout.removeAllViews();
		for (final Cash c : list) {
			final LinearLayout l = setDateTimeCash(c);
			l.setMinimumHeight(30);

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			layoutParams.setMargins(0, 0, 0, 5);

			layout.addView(l, layoutParams);

			l.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setTitle("Options");
					builder.setItems(options,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										deleteEntryAlertDialog(layout, c, l);
										break;
									case 1:
										addEntryAlertDialog("Edit entry", c);
										break;
									default:
										break;
									}
								}
							});
					builder.show();
				}
			});
		}
	}

	private void addMonthCashToLayout(final LinearLayout layout) {
		List<Cash> list = datasource.getThisMonthCash();

		layout.removeAllViews();
		for (final Cash c : list) {
			final LinearLayout l = setDateTimeCash(c);

			l.setMinimumHeight(30);

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			layoutParams.setMargins(0, 0, 0, 5);
			layout.addView(l);

			l.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setTitle("Options");
					builder.setItems(options,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										deleteEntryAlertDialog(layout, c, l);
										break;
									case 1:
										addEntryAlertDialog("Edit entry", c);
										break;
									default:
										break;
									}
								}
							});
					builder.show();
				}
			});
		}
	}

	private LinearLayout deleteEntryAlertDialog(final LinearLayout layout,
			final Cash c, final LinearLayout l) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				MainActivity.this);
		TextView text = new TextView(getApplicationContext());
		text.setText("Do you want to delete this entry?");
		alert.setView(text);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				datasource.deleteCash(c);
				layout.removeView(l);
				addCashToLayout(date_time);
				addMonthCashToLayout(month_date_time);
				setWastedMoneyText(wasted);
				setMonthWastedMoneyText(monthWasted);
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
		return layout;

	}

	public void getSumCash() {
		List<Cash> list = datasource.getWeekCashs();
		sumCash = 0.0;

		for (Cash c : list) {
			sumCash += c.getCash();
		}
	}

	public void getMonthSumCash() {
		List<Cash> list = datasource.getThisMonthCash();
		monthSumCash = 0.0;

		for (Cash c : list) {
			monthSumCash += c.getCash();
		}
	}

	private void setWastedMoneyText(TextView wasted) {
		getSumCash();
		wasted.setText("W: " + String.valueOf(sumCash) + " kn");
	}

	private void setMonthWastedMoneyText(TextView monthWasted) {
		getMonthSumCash();
		monthWasted.setText("M: " + String.valueOf(monthSumCash) + " kn");
	}

	@SuppressLint("SimpleDateFormat")
	private LinearLayout setDateTimeCash(Cash c) {
		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.layout_dates_time, null);

		LinearLayout entryLayout = (LinearLayout) v
				.findViewById(R.id.entry_layout);
		LinearLayout allExcept = (LinearLayout) entryLayout
				.findViewById(R.id.all_except_money);

		LinearLayout timeLayout = (LinearLayout) allExcept
				.findViewById(R.id.time_layout);
		LinearLayout descLayout = (LinearLayout) allExcept
				.findViewById(R.id.desc_layout);

		TextView t = (TextView) timeLayout.findViewById(R.id.textView_date);
		t.setTextSize(Float.valueOf(15));
		String newDate = null;
		java.util.Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(c.getDate());
			newDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		t.setText(getDayOfWeek(dayOfWeek) + " " + newDate);

		TextView t2 = (TextView) timeLayout.findViewById(R.id.textView_time);
		t2.setTextSize(Float.valueOf(15));
		String s = c.getTime().substring(0, Math.min(c.getTime().length(), 8));
		t2.setText(s);

		TextView t3 = (TextView) entryLayout.findViewById(R.id.textView_cash);
		t3.setTextSize(Float.valueOf(26));
		t3.setText(String.valueOf(c.getCash()) + " kn");

		TextView t4 = (TextView) descLayout.findViewById(R.id.textView_desc);
		t4.setTextSize(Float.valueOf(20));
		t4.setText(c.getDesc());

		return (LinearLayout) v;
	}

	private String getDayOfWeek(int id) {
		String s = new String();
		switch (id) {
		case 2:
			s = "Mon";
			break;
		case 3:
			s = "Tue";
			break;
		case 4:
			s = "Wed";
			break;
		case 5:
			s = "Thu";
			break;
		case 6:
			s = "Fri";
			break;
		case 7:
			s = "Sat";
			break;
		case 1:
			s = "Sun";
			break;

		default:

		}
		return s;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.see_history:
			Intent i = new Intent(c, HistoryActivity.class);
			startActivity(i);
			break;
		case R.id.export_db:
			/*Intent i = new Intent(c, HistoryActivity.class);
			startActivity(i);*/
			// data/data/hr.fenster.gang.cashwasting/databases/cash.db

			try {
				// File sd = Environment.getExternalStorageDirectory();
				File sd = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				File data = Environment.getDataDirectory();

				if (sd.canWrite()) {
					String currentDBPath = "//data//hr.fenster.gang.cashwasting//databases//cash.db";
					String backupDBPath = "cash_old.db";
					File currentDB = new File(data, currentDBPath);
					File backupDB = new File(sd, backupDBPath);

					if (currentDB.exists()) {
						@SuppressWarnings("resource")
						FileChannel src = new FileInputStream(currentDB)
								.getChannel();
						@SuppressWarnings("resource")
						FileChannel dst = new FileOutputStream(backupDB)
								.getChannel();
						dst.transferFrom(src, 0, src.size());
						src.close();
						dst.close();
						Toast.makeText(getBaseContext(),
								backupDB.toString() + " PROSLO",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getBaseContext(),
								backupDB.toString() + " nekaj",
								Toast.LENGTH_LONG).show();
					}
				}
			} catch (Exception e) {
			}
			break;
		default:
			break;
		}

		return true;
	}

}
