package io.github.thegeekybaniya.myapplication;

import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

/**
 * Created by muralidharan on 6/4/16.
 */
public class DateSetListener implements DatePickerDialog.OnDateSetListener {

    protected TextView dateTextView;

    DateViewClickListener clickListenerToSetMinDate;

    public DateSetListener(TextView dateTextView) {
        this.dateTextView = dateTextView;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        dateTextView.setText(getDateString(year, monthOfYear + 1, dayOfMonth));

        if (clickListenerToSetMinDate != null) {
            Calendar minDate = Calendar.getInstance();
            minDate.set(year, monthOfYear, dayOfMonth);
            clickListenerToSetMinDate.setMinDate(minDate);
        }
    }

    protected String getDateString(int year, int month, int day_of_month) {
        StringBuilder sb = new StringBuilder();
        sb.append(year);
        sb.append('-');
        if (month < 10) {
            sb.append('0');
        }
        sb.append(month);
        sb.append('-');
        if (day_of_month < 10) {
            sb.append('0');
        }
        sb.append(day_of_month);

        return sb.toString();
    }

    public void registerClickListenerToSetMinDate(DateViewClickListener clickListenerToSetMinDate) {
        this.clickListenerToSetMinDate = clickListenerToSetMinDate;
    }
}
