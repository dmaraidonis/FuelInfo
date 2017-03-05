package gr.dmaraid.fuelinfo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends BaseAdapter implements Filterable {

	private List<HistoryItem> mAllItems = new ArrayList<HistoryItem>();
	private List<HistoryItem> mFilteredItems = new ArrayList<HistoryItem>();
	private List<HistoryItem> mEmptyList = new ArrayList<HistoryItem>();
	private boolean isFilteringListEmpty;
	private boolean addedDuplicate = false;

	private final Context mContext;

	// Constructor
	public HistoryAdapter(Context context) {
		mContext = context;
	}

	// Add a HistoryItem to the adapter
	// Notify observers that the data set has changed
	public void add(HistoryItem item) {
		mAllItems.add(item);
		if (addedDuplicate) {
			mFilteredItems.add(item);
		}
		notifyDataSetChanged();
	}

	// Retrieve the number of HistoryItems
	@Override
	public int getCount() {
		if (mFilteredItems.size() > 0)
			return mFilteredItems.size();
		if (isFilteringListEmpty)
			return 0;

		return mAllItems.size();

	}

	// Get the item in the "pos" position
	@Override
	public Object getItem(int pos) {
		if (mFilteredItems.size() > 0)
			return mFilteredItems.get(pos);
		if (isFilteringListEmpty) {
			return mEmptyList;
		}
		return mAllItems.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	// Remove the item
	public void removeItem(int pos) {
		if (mFilteredItems.size() > 0) {
			for (int i = 0; i < mAllItems.size(); i++) {
				if (mAllItems.get(i) == mFilteredItems.get(pos)) {
					mAllItems.remove(i);
				}
			}
			mFilteredItems.remove(pos);
		}
		notifyDataSetChanged();
	}

	public void clearFilteredItems() {
		mFilteredItems.clear();
		notifyDataSetChanged();
	}

	public void clearAllItems() {
		clearFilteredItems();
		mAllItems.clear();
		notifyDataSetChanged();
	}

	public boolean isFilteringListEmpty() {
		return isFilteringListEmpty;
	}

	public void setFilteringListEmpty(boolean isFilteringListEmpty) {
		this.isFilteringListEmpty = isFilteringListEmpty;
		if (isFilteringListEmpty())
			mEmptyList.clear();
	}

	public boolean isAddedDuplicate() {
		return addedDuplicate;
	}

	public void setAddedDuplicate(boolean addedDuplicate) {
		this.addedDuplicate = addedDuplicate;
	}

	// Get the View of the history item
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Get the item
		HistoryItem historyItem = (HistoryItem) getItem(position);

		LinearLayout itemLayout = (LinearLayout) convertView;

		if (itemLayout == null) {
			// Inflate the layout that will be used for the item
			itemLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(
					R.layout.layout_history_item, null);
		}

		itemLayout.setVisibility(View.VISIBLE);
		// Set some attributes of each TextView of the layout
		TextView tvDate = (TextView) itemLayout
				.findViewById(R.id.tvHistoryDate);
		tvDate.setTextColor(Color.BLUE);
		tvDate.setText(historyItem.getDate());

		TextView tvEuro = (TextView) itemLayout
				.findViewById(R.id.tvHistoryEuro);
		tvEuro.setTextColor(Color.BLACK);
		tvEuro.setText(historyItem.getEuro()
				+ mContext.getString(R.string.string_currency));

		TextView tvPrice = (TextView) itemLayout
				.findViewById(R.id.tvHistoryPrice);
		tvPrice.setTextColor(Color.BLACK);
		tvPrice.setText(historyItem.getPrice()
				+ mContext.getString(R.string.string_euro_per_l));

		TextView tvLitres = (TextView) itemLayout
				.findViewById(R.id.tvHistoryLitres);
		tvLitres.setTextColor(Color.BLACK);
		tvLitres.setText(historyItem.getLitres());

		return itemLayout;
	}

	@Override
	public Filter getFilter() {

		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				mFilteredItems = (List<HistoryItem>) results.values;
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				if (constraint != null && constraint.length() > 0) {
					ArrayList<HistoryItem> filterList = new ArrayList<HistoryItem>();
					for (int i = 0; i < mAllItems.size(); i++) {
						if ((mAllItems.get(i).getDate().toUpperCase(Locale.US))
								.contains(constraint.toString().toUpperCase(
										Locale.US))) {
							filterList.add(mAllItems.get(i));
						}
					}
					results.count = filterList.size();
					results.values = filterList;
				}
				return results;
			}
		};

		return filter;
	}

}
