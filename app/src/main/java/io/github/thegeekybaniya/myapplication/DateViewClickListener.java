package io.github.thegeekybaniya.myapplication;

import android.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

/**
 * Created by muralidharan on 6/4/16.
 */
public class DateViewClickListener implements View.OnClickListener {

    private static final String LOG_TAG = DateViewClickListener.class.getSimpleName();

    TextView textView;
    DateSetListener dateSetListener;
    FragmentManager fragmentManager;
    String tag;
    Calendar minDate;

    public DateViewClickListener(
            TextView textView,
            DateSetListener dateSetListener,
            FragmentManager fragmentManager,
            String tag) {
        this.textView = textView;
        this.dateSetListener = dateSetListener;
        this.fragmentManager = fragmentManager;
        this.tag = tag;
    }

    @Override
    public void onClick(View v) {
        String dateStr = textView.getText().toString();
        Calendar date = Calendar.getInstance();
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            date = getDate(textView.getText().toString());
        }
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                dateSetListener,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
        );
        if (minDate != null)
            dpd.setMinDate(minDate);

        // Airlines do not allow ticketing more than a year in advance.
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 1);
        dpd.setMaxDate(maxDate);

        dpd.show(fragmentManager, tag);
    }
    /*
     * Return a calendar instance representing a string.
     * Assume that the string is of the form "YYYY-MM-DD". If its malformed, return today's date.
     */
    protected Calendar getDate(String str) {
        Calendar cal = Calendar.getInstance();

        try {
            int year = Integer.parseInt(str.substring(0, 4));
            int month = Integer.parseInt(str.substring(5, 7));
            int day = Integer.parseInt(str.substring(8, 10));
            cal.set(year, month, day);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not parse date string: " + str, e);
        }
        return cal;
    }

    public Calendar getMinDate() {
        return minDate;
    }

    public void setMinDate(Calendar minDate) {
        this.minDate = minDate;
    }
}
