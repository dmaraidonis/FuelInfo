package gr.dmaraid.fuelinfo;

import android.content.Intent;

public class HistoryItem {

	private String mDate = new String();
	private String mEuro = new String();
	private String mPrice = new String();
	private String mLitres = new String();
	private boolean mVisible = false;

	public static final String ITEM_SEP = System.getProperty("line.separator");

	// Constructor that creates an item with data of an Intent
	HistoryItem(Intent intent) {
		mDate = intent.getStringExtra(FuelInfo.DATE_EXTRA);
		mEuro = intent.getStringExtra(FuelInfo.EURO_EXTRA);
		mPrice = intent.getStringExtra(FuelInfo.PRICE_EXTRA);
		mLitres = intent.getStringExtra(FuelInfo.LITRES_EXTRA);
	}

	// Constructor that creates an item with given values
	HistoryItem(String date, String euro, String price, String litres) {
		mDate = date;
		mEuro = euro;
		mPrice = price;
		mLitres = litres;
	}

	// Getters and setters
	public String getDate() {
		return mDate;
	}

	public void setDate(String mDate) {
		this.mDate = mDate;
	}

	public String getEuro() {
		return mEuro;
	}

	public void setEuro(String mEuro) {
		this.mEuro = mEuro;
	}

	public String getPrice() {
		return mPrice;
	}

	public void setPrice(String mPrice) {
		this.mPrice = mPrice;
	}

	public String getLitres() {
		return mLitres;
	}

	public void setLitres(String mLitres) {
		this.mLitres = mLitres;
	}

	public boolean isVisible() {
		return mVisible;
	}

	public void setVisible(boolean mVisible) {
		this.mVisible = mVisible;
	}

	// Useful when saving the item's values to a file
	public String toString() {
		return mDate + ITEM_SEP + mEuro + ITEM_SEP + mPrice + ITEM_SEP
				+ mLitres;
	}
}
