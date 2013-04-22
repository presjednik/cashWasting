package hr.fenster.gang.cashwasting;

import hr.fenster.gang.cashwasting.type.Cash;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class HistoryActivity extends Activity {

	private final CashDataSource datasource = new CashDataSource(this);

	// private boolean show = true;
	private double sumCash;
	private TextView wasted_history;
	private LinearLayout date_time_history;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_layout);

		datasource.open();

		wasted_history = (TextView) findViewById(R.id.text_wasted_history);

		ScrollView sv = (ScrollView) findViewById(R.id.scroll_cashs_history);
		date_time_history = (LinearLayout) sv
				.findViewById(R.id.linear_dates_time_history);

		Button buttonSearch = (Button) findViewById(R.id.history_button);

		buttonSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addCashToLayout(date_time_history);
				setWastedMoneyText(wasted_history);
			}
		});

	}

	private void addCashToLayout(final LinearLayout layout) {
		List<Cash> list = datasource.getAllCashs();

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
		List<Cash> list = datasource.getAllCashs();
		sumCash = 0.0;

		for (Cash c : list) {
			sumCash += c.getCash();
		}
	}

	private void setWastedMoneyText(TextView wasted) {
		getSumCash();
		wasted.setText("Week: " + String.valueOf(sumCash) + " kn");
	}

	private LinearLayout setDateTimeCash(Cash c) {
		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.layout_dates_time, null);

		TextView t = (TextView) v.findViewById(R.id.textView_date);
		t.setText(c.getDate());

		TextView t2 = (TextView) v.findViewById(R.id.textView_time);
		t2.setText(c.getTime());

		TextView t3 = (TextView) v.findViewById(R.id.textView_cash);
		t3.setText(String.valueOf(c.getCash()) + "$");

		TextView t4 = (TextView) v.findViewById(R.id.textView_desc);
		t4.setText(c.getDesc());

		return (LinearLayout) v;
	}

}
