package gr.dmaraid.fuelinfo;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FuelInfo extends ActionBarActivity {

	private EditText etMoney;
	private EditText etPrice;
	private TextView tvLitres;
	private Button btnCalc;
	private double litres;
	private int money;
	private double price;

	// Constant variables to use with Intents
	public static final String DATE_EXTRA = "gr.dmaraid.fuelinfo.DATE";
	public static final String EURO_EXTRA = "gr.dmaraid.fuelinfo.EURO";
	public static final String PRICE_EXTRA = "gr.dmaraid.fuelinfo.PRICE";
	public static final String LITRES_EXTRA = "gr.dmaraid.fuelinfo.LITRES";

	SharedPreferences sharedPrefs;
	private Date mDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fuel);

		sharedPrefs = getPreferences(Context.MODE_PRIVATE);

		// Initialize the Views
		etMoney = (EditText) findViewById(R.id.editMoney);
		etPrice = (EditText) findViewById(R.id.editPrice);
		tvLitres = (TextView) findViewById(R.id.tvCalculatedLitres);
		btnCalc = (Button) findViewById(R.id.btnCalc);
		tvLitres.setTextColor(Color.BLUE);

		// Restore the previously saved values of the variables
		litres = sharedPrefs.getFloat("litres", 0);
		money = sharedPrefs.getInt("money", 0);
		price = sharedPrefs.getFloat("price", 0);

		// If the restored values are not zero, put them in the EditTexts
		if (money != 0) {
			etMoney.setText(Integer.toString(money));
		}
		if (price != 0) {
			etPrice.setText(Double.toString(price));
		}
		if (litres != 0) {
			tvLitres.setText(new DecimalFormat("0.00").format(litres)
					+ getString(R.string.string_volUnit));
		}

		// If the user writes in the EditText and hits Enter, then the litres
		// are calculated
		etPrice.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					calculateLitres();
					return true;
				}
				return false;
			}
		});

		etMoney.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					calculateLitres();
					return true;
				}
				return false;
			}
		});

		btnCalc.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				calculateLitres();
			}
		});
	}

	@Override
	protected void onPause() {
		// Save the values of the variables to restore them when the app
		// will be restarted later
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putFloat("litres", (float) litres);
		editor.putInt("money", money);
		editor.putFloat("price", (float) price);
		editor.commit();

		super.onPause();
	}

	@Override
	protected void onResume() {
		// Give the focus to the layout to avoid the popping up of the soft
		// keyboard because of the EditText's initial focus
		RelativeLayout layoutMain = (RelativeLayout) findViewById(R.id.layoutMainFuel);
		layoutMain.setFocusable(true);
		layoutMain.setFocusableInTouchMode(true);
		layoutMain.requestFocus();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_fuel, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Clear the values of the variables and the EditTexts if the menu
		// button Clear is selected
		if (item.getItemId() == R.id.action_clear) {
			etMoney.setText("");
			etPrice.setText("");
			tvLitres.setText("");
			money = 0;
			price = 0;
			litres = 0;
			Toast.makeText(this, getString(R.string.toastInfoCleared),
					Toast.LENGTH_SHORT).show();
		}
		// Send the values to the History activity via the Intent
		if (item.getItemId() == R.id.action_save_data) {
			if (calculateLitres()) {
				Intent i = new Intent(getApplicationContext(), History.class);
				i.putExtra(DATE_EXTRA, getDateAndTime());
				i.putExtra(EURO_EXTRA, Integer.toString(money));
				i.putExtra(PRICE_EXTRA,
						new DecimalFormat("0.000").format(price));
				i.putExtra(LITRES_EXTRA,
						new DecimalFormat("0.00").format(litres));
				startActivity(i);
			}
		}
		// Start the History activity without sending values, just for viewing
		if (item.getItemId() == R.id.action_show_history) {
			startActivity(new Intent(getApplicationContext(), History.class));
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Start the Consumption activity if all the fields have values
	public void goToConsumption(View v) {
		if (litres == 0) {
			Toast.makeText(this, getString(R.string.toastEmptyLitres),
					Toast.LENGTH_LONG).show();
		} else {
			Intent i = new Intent(this, Consumption.class);
			i.putExtra(LITRES_EXTRA, litres);
			startActivity(i);
		}
	}
	
	// The following methods are used to increment the EditTexts' values (money and price) 
	public void plusOneMilliCent(View v) {
		price = (etPrice.getText().toString().equals("")) ? 0.001 : Double
				.parseDouble(etPrice.getText().toString()) + 0.001;
		etPrice.setText(new DecimalFormat("0.000").format(price));
	}

	public void plusOneCent(View v) {
		price = (etPrice.getText().toString().equals("")) ? 0.01 : Double
				.parseDouble(etPrice.getText().toString()) + 0.01;
		etPrice.setText(new DecimalFormat("0.000").format(price));
	}

	public void plusTenCents(View v) {
		price = (etPrice.getText().toString().equals("")) ? 0.1 : Double
				.parseDouble(etPrice.getText().toString()) + 0.1;
		etPrice.setText(new DecimalFormat("0.000").format(price));
	}

	public void plusOneEuro(View v) {
		price = (etPrice.getText().toString().equals("")) ? 1 : Double
				.parseDouble(etPrice.getText().toString()) + 1;
		etPrice.setText(new DecimalFormat("0.000").format(price));
	}

	public void plusTwoEuro(View v) {
		money = (etMoney.getText().toString().equals("")) ? 2 : Integer
				.parseInt(etMoney.getText().toString()) + 2;
		etMoney.setText(Integer.toString(money));
	}

	public void plusFiveEuro(View v) {
		money = (etMoney.getText().toString().equals("")) ? 5 : Integer
				.parseInt(etMoney.getText().toString()) + 5;
		etMoney.setText(Integer.toString(money));
	}

	public void plusTenEuro(View v) {
		money = (etMoney.getText().toString().equals("")) ? 10 : Integer
				.parseInt(etMoney.getText().toString()) + 10;
		etMoney.setText(Integer.toString(money));
	}

	public void plusTwentyEuro(View v) {
		money = (etMoney.getText().toString().equals("")) ? 20 : Integer
				.parseInt(etMoney.getText().toString()) + 20;
		etMoney.setText(Integer.toString(money));
	}
	
	// Calculate the litres if all the fields have values and are not zero
	private boolean calculateLitres() {
		if (!etMoney.getText().toString().equals("")
				&& !etPrice.getText().toString().equals("")) {
			money = Integer.parseInt(etMoney.getText().toString());
			price = Double.parseDouble(etPrice.getText().toString());
			// Check if the values are zero
			if (money == 0 || price == 0) {
				Toast.makeText(this, getString(R.string.toastZero),
						Toast.LENGTH_LONG).show();
				return false;
			}
			litres = money / price;
			tvLitres.setText(new DecimalFormat("0.00").format(litres)
					+ getString(R.string.string_volUnit));
			return true;
		} else {
			if (etMoney.getText().toString().equals("")) {
				Toast.makeText(this, getString(R.string.toastEmptyMoney),
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, getString(R.string.toastEmptyPrice),
						Toast.LENGTH_LONG).show();
			}
			return false;
		}

	}
	
	// Gat the current date and time in a String format (DD/MM/YYYY - HH:MM)
	private String getDateAndTime() {

		mDate = new Date();
		mDate = new Date(mDate.getTime());

		Calendar c = Calendar.getInstance();

		String dateString = getDateString(c.get(Calendar.YEAR),
				c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
		String timeString = getTimeString(c.get(Calendar.HOUR_OF_DAY),
				c.get(Calendar.MINUTE));

		return dateString + " " + timeString;

	}

	private static String getDateString(int year, int monthOfYear,
			int dayOfMonth) {

		String mon = "" + monthOfYear;
		String day = "" + dayOfMonth;

		if (monthOfYear < 10)
			mon = "0" + monthOfYear;
		if (dayOfMonth < 10)
			day = "0" + dayOfMonth;

		return day + "/" + mon + "/" + year;
	}

	private static String getTimeString(int hourOfDay, int minute) {
		String hour = "" + hourOfDay;
		String min = "" + minute;

		if (hourOfDay < 10)
			hour = "0" + hourOfDay;
		if (minute < 10)
			min = "0" + minute;

		return hour + ":" + min;
	}
}