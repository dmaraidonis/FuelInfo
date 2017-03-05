package gr.dmaraid.fuelinfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormatSymbols;
import java.util.Calendar;

public class History extends AppCompatActivity {

    private static final String EXPORT_DIR = "/FuelInfo";
    private static final String EXPORT_TXT_FILE = "FuelInfo_export.txt";
    private static final String FILE_NAME = "HistoryData.txt";
    // private static final int EDIT_ITEM = 0;
    HistoryAdapter mAdapter;

    Spinner spinnerYear;
    Spinner spinnerMonth;
    Calendar c;
    private int currentYearSelection = -1;
    private int currentMonthSelection = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the adapter
        mAdapter = new HistoryAdapter(getApplicationContext());

        setContentView(R.layout.layout_listview_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the ListView
        ListView listView = (ListView) findViewById(R.id.listview_history);

        listView.setHeaderDividersEnabled(true);

        // Inflate the ListView's header from layout
        LinearLayout headerLayout = (LinearLayout) LayoutInflater.from(
                getApplicationContext()).inflate(R.layout.layout_list_header,
                null);

        // Add the header
        listView.addHeaderView(headerLayout, null, false);

        // Load the saved history items of the ListView
        loadItems();

        Intent fromFuelInfo = getIntent();
        // If the activity was started for saving and not viewing, add an item
        // to the adapter that is handling the ListView
        if (fromFuelInfo.hasExtra(FuelInfo.DATE_EXTRA)) {
            mAdapter.add(new HistoryItem(fromFuelInfo));
        }

        // Set the adapter to the ListView
        listView.setAdapter(mAdapter);

        // On long press of an item of the ListView a context menu will appear
        registerForContextMenu(listView);

        // Create the spinner for year and fill it with data
        spinnerYear = (Spinner) findViewById(R.id.spinnerYear);
        ArrayAdapter<CharSequence> adapterYear = ArrayAdapter
                .createFromResource(this, R.array.years, R.layout.spinner_item);
        spinnerYear.setAdapter(adapterYear);

        spinnerYear.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String selectedYear = parent.getItemAtPosition(pos).toString();
                String yearOfItem;

                mAdapter.clearFilteredItems();
                mAdapter.setFilteringListEmpty(false);
                boolean noMatch = false;
                for (int idx = 0; idx < mAdapter.getCount(); idx++) {
                    yearOfItem = ((HistoryItem) mAdapter.getItem(idx))
                            .toString().substring(6, 10);
                    if (pos == 0) {
                        History.this.mAdapter.getFilter().filter("/");
                        noMatch = false;
                        Log.i("spinnerYear", "All years (Year spinner 1)");
                        break;
                    }
                    if (selectedYear.equals(yearOfItem)) {
                        History.this.mAdapter.getFilter().filter(yearOfItem);
                        noMatch = false;
                        Log.i("spinnerYear", "Year match (Year spinner 2)");
                        break;
                    }
                    if (!selectedYear.equals(yearOfItem) && pos != 0) {
                        noMatch = true;
                        Log.i("spinnerYear", "Year mismatch (Year spinner 3)");
                    }
                }
                if ((noMatch || mAdapter.getCount() == 0) && pos != 0) {
                    mAdapter.setFilteringListEmpty(true);
                    spinnerMonth.setSelection(0);
                    spinnerMonth.setEnabled(false);
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.nothing_to_display_for_the_year)
                                    + selectedYear, Toast.LENGTH_SHORT).show();
                } else {
                    spinnerMonth.setEnabled(true);
                    spinnerMonth.setSelection(0, true);
                    spinnerMonth.setSelection(c.get(Calendar.MONTH) + 1, true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        c = Calendar.getInstance();

        // Create the spinner for months and fill it with data
        spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        ArrayAdapter<CharSequence> adapterMonth = ArrayAdapter
                .createFromResource(this, R.array.months, R.layout.spinner_item);
        spinnerMonth.setAdapter(adapterMonth);

        spinnerMonth.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String selectedMonth = parent.getItemAtPosition(pos).toString();

                mAdapter.clearFilteredItems();
                mAdapter.setFilteringListEmpty(false);
                String yearOfItem;
                boolean noMatch = false;
                for (int idx = 0; idx < mAdapter.getCount(); idx++) {
                    String monthOfItem = ((HistoryItem) mAdapter.getItem(idx))
                            .toString().substring(3, 5);
                    String month_name = new DateFormatSymbols().getMonths()[Integer
                            .parseInt(monthOfItem) - 1];
                    String yearSelection = spinnerYear.getItemAtPosition(
                            spinnerYear.getSelectedItemPosition()).toString();
                    yearOfItem = ((HistoryItem) mAdapter.getItem(idx))
                            .toString().substring(6, 10);

                    if (pos == 0 && yearSelection.equals(yearOfItem)) {
                        History.this.mAdapter.getFilter().filter(
                                "/" + yearSelection);
                        noMatch = false;
                        Log.i("spinnerMonth", "All months and year match (1)");
                        break;
                    }
                    if (pos == 0 && !yearSelection.equals(yearOfItem)
                            && spinnerYear.getSelectedItemPosition() != 0) {
                        History.this.mAdapter.getFilter().filter(
                                "/" + yearSelection);
                        noMatch = true;
                        Log.i("spinnerMonth", "All months and year mismatch (2)");
                    }
                    if (selectedMonth.equals(month_name)
                            && yearSelection.equals(yearOfItem)
                            && spinnerYear.getSelectedItemPosition() != 0) {
                        History.this.mAdapter.getFilter().filter(
                                "/" + monthOfItem + "/" + yearSelection);
                        noMatch = false;
                        Log.i("spinnerMonth", "Month and year match, year is "
                                + yearSelection + " and month is " + monthOfItem + ". (3)");
                        break;
                    }
                    if (!selectedMonth.equals(month_name)
                            && yearSelection.equals(yearOfItem)
                            && spinnerYear.getSelectedItemPosition() != 0) {
                        Log.i("spinnerMonth",
                                "Month mismatch and year match, year is "
                                        + yearSelection + ". (4)");
                        noMatch = true;
                    }
                    if (selectedMonth.equals(month_name)
                            && spinnerYear.getSelectedItemPosition() == 0) {
                        History.this.mAdapter.getFilter().filter(
                                "/" + monthOfItem + "/");
                        noMatch = false;
                        Log.i("spinnerMonth", "Month match for all years. (5)");
                        break;
                    }
                    if (!selectedMonth.equals(month_name)
                            && spinnerYear.getSelectedItemPosition() == 0
                            && pos != 0) {
                        noMatch = true;
                        Log.i("spinnerMonth", "Month mismatch for all years. (6)");
                    }
                    if (pos == 0 && spinnerYear.getSelectedItemPosition() == 0) {
                        History.this.mAdapter.getFilter().filter("/");
                        noMatch = false;
                        Log.i("spinnerMonth", "All months and all years (7)");
                        break;
                    }

                }
                if (noMatch)
                    mAdapter.setFilteringListEmpty(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // Set the spinner selection to the current year
        for (int i = 0; i < spinnerYear.getCount(); i++) {
            if (spinnerYear.getItemAtPosition(i).toString()
                    .equals(Integer.toString(c.get(Calendar.YEAR)))) {
                spinnerYear.setSelection(i, true);
            }
        }

//        Log.i("Before manual month spinner selection", Integer.toString(spinnerMonth.getSelectedItemPosition()));
//        spinnerMonth.setSelection(c.get(Calendar.MONTH) + 1, true);
//        Log.i("After manual month spinner selection", Integer.toString(spinnerMonth.getSelectedItemPosition()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_action_export) {
            saveToSDcard();
        }
        if (item.getItemId() == R.id.menu_action_import) {
            loadFromSDcard();
            spinnerYear.setSelection(0);
            spinnerMonth.setSelection(0);
            mAdapter.notifyDataSetChanged();
        }
        if (item.getItemId() == R.id.menu_action_delete) {
            showDialogFragmentToDeleteAll();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Gets the extra information linked to this menu item
        AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        // Get the item's position
        int pos = info.position;
        if (item.getTitle().equals("Delete")) {
            mAdapter.removeItem(pos - 1);
        }
        // if (item.getTitle().equals("Duplicate")) {
        // mAdapter.setAddedDuplicate(true);
        // ((HistoryItem) mAdapter.getItem(pos - 1)).getDate();
        // mAdapter.add(new HistoryItem(((HistoryItem) mAdapter
        // .getItem(pos - 1)).getDate(), ((HistoryItem) mAdapter
        // .getItem(pos - 1)).getEuro(), ((HistoryItem) mAdapter
        // .getItem(pos - 1)).getPrice(), ((HistoryItem) mAdapter
        // .getItem(pos - 1)).getLitres()));
        // }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        // Add a context menu item
        menu.add(Menu.NONE, 1, Menu.NONE, "Delete");
        // menu.add(Menu.NONE, 2, Menu.NONE, "Duplicate");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void onResume() {
        Log.i("myTag", "Entered onResume at History.java");
        if (currentYearSelection != -1) {
            spinnerYear.setSelection(0, true);
            spinnerMonth.setSelection(0, true);
            spinnerYear.setSelection(currentYearSelection, true);
            spinnerMonth.setSelection(currentMonthSelection, true);
        }
        super.onResume();
    }

    @Override
    protected void onRestart() {
        Log.i("myTag", "Entered onRestart at History.java");

        super.onRestart();
    }

    @Override
    protected void onPause() {
        Log.i("myTag", "Entered onPause at History.java");
        // Save the history items of the ListView
        saveItems();

        currentYearSelection = spinnerYear.getSelectedItemPosition();
        currentMonthSelection = spinnerMonth.getSelectedItemPosition();

        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("myTag", "Entered onStop at History.java");

        super.onStop();
    }

    private void saveItems() {
        PrintWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            mAdapter.clearFilteredItems();
            mAdapter.setFilteringListEmpty(false);
            for (int idx = 0; idx < mAdapter.getCount(); idx++) {
                // The toString() method of the HistoryItem class returns the
                // values of the item with a line separator (\n). These lines
                // are written to the file.
                writer.println(mAdapter.getItem(idx));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

    private void saveToSDcard() {
        String state = Environment.getExternalStorageState();
        PrintWriter writer = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + EXPORT_DIR);
            dir.mkdirs();

            File file = new File(dir, EXPORT_TXT_FILE);

            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                writer = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(fos)));

                mAdapter.clearFilteredItems();
                mAdapter.setFilteringListEmpty(false);
                for (int idx = 0; idx < mAdapter.getCount(); idx++) {
                    writer.println(mAdapter.getItem(idx));
                }
                Toast.makeText(
                        getApplicationContext(),
                        "Data exported to " + EXPORT_DIR + "/"
                                + EXPORT_TXT_FILE, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e("myTag", "Error at saving file:", e);
                return; // Do not continue
            } finally {
                if (null != writer) {
                    writer.close();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "SD card is not mounted",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void loadItems() {
        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String date;
            String euro;
            String price;
            String litres;

            // Read the file to get the saved values of each item
            while (null != (date = reader.readLine())) {
                euro = reader.readLine();
                price = reader.readLine();
                litres = reader.readLine();
                mAdapter.add(new HistoryItem(date, euro, price, litres));
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadFromSDcard() {
        BufferedReader reader = null;
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + EXPORT_DIR);

            File file = new File(dir, EXPORT_TXT_FILE);

            reader = new BufferedReader(new FileReader(file));

            String date;
            String euro;
            String price;
            String litres;

            // Read the file to get the saved values of each item
            while ((null != (date = reader.readLine()))) {
                euro = reader.readLine();
                price = reader.readLine();
                litres = reader.readLine();
                mAdapter.add(new HistoryItem(date, euro, price, litres));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "The file " + EXPORT_TXT_FILE + " was not found.",
                    Toast.LENGTH_LONG).show();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteAllItems() {
        mAdapter.clearAllItems();
        spinnerYear.setSelection(0, true);
        spinnerMonth.setSelection(0, true);
    }

    void showDialogFragmentToDeleteAll() {
        AlertDialogFragment mDialog = AlertDialogFragment.newInstance();
        mDialog.show(getSupportFragmentManager(), "Alert");

    }

    public static class AlertDialogFragment extends DialogFragment {

        public static AlertDialogFragment newInstance() {
            return new AlertDialogFragment();
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage("Do you really want delete everything?")
                    .setCancelable(false)
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dismiss();
                                }

                            })
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    ((History) getActivity()).deleteAllItems();

                                }
                            }).create();
        }
    }
}
