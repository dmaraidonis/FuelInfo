package gr.dmaraid.fuelinfo;

import java.text.DecimalFormat;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Consumption extends ActionBarActivity {

	private double litres;
	private double distance;
	private double consumption;
	private EditText etConsumption;
	private TextView tvConsumption;

	SharedPreferences sharedPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consumption);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		sharedPrefs = getPreferences(Context.MODE_PRIVATE);

		Intent i = getIntent();
		litres = i.getDoubleExtra(FuelInfo.LITRES_EXTRA, 0);

		etConsumption = (EditText) findViewById(R.id.editConsumption);
		tvConsumption = (TextView) findViewById(R.id.tvConsumptionResult);
		tvConsumption.setTextColor(Color.BLUE);

		consumption = sharedPrefs.getFloat("consumption", 0);

		if (consumption != 0) {
			etConsumption
					.setText(new DecimalFormat("0.00").format(consumption));
			calculateConsumption();
		}

		etConsumption.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					calculateConsumption();
					return true;
				}
				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		RelativeLayout layoutMainConsumption = (RelativeLayout) findViewById(R.id.layoutMainConsumption);
		layoutMainConsumption.setFocusable(true);
		layoutMainConsumption.setFocusableInTouchMode(true);
		layoutMainConsumption.requestFocus();
		super.onResume();
	}

	@Override
	protected void onPause() {
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putFloat("consumption", (float) consumption);
		editor.commit();

		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_consumption, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_clear) {
			etConsumption.setText("");
			tvConsumption.setText("");
			consumption = 0;
		}
		return super.onOptionsItemSelected(item);
	}

	public void calculateConsumption() {
		if (!etConsumption.getText().toString().equals("")) {
			consumption = Double
					.parseDouble(etConsumption.getText().toString());
			distance = (litres * 100) / consumption;
			tvConsumption.setText(getString(R.string.your_vehicle_can_travel)
					+ new DecimalFormat("0.00").format(distance)
					+ getString(R.string.distanceUnit_with)
					+ new DecimalFormat("0.00").format(litres)
					+ getString(R.string.volUnit_of_fuel));
		} else {
			Toast.makeText(this, getString(R.string.toastEmptyConsumption),
					Toast.LENGTH_LONG).show();
		}

	}

	public void calcConsumption(View v) {
		calculateConsumption();
	}

}
