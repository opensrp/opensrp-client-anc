package org.smartregister.anc.library.listener;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.vijay.jsonwizard.utils.DatePickerUtils;

import org.smartregister.clientandeventmodel.DateUtil;

import java.util.Calendar;
import java.util.Date;

public class DatePickerListener implements View.OnClickListener {
    public static final String TAG = DatePickerListener.class.getCanonicalName();

    private final EditText editText;
    private final boolean hasMaxMinDates;
    private final boolean isEdd;
    private Context context;

    public DatePickerListener(Context context, EditText editText, boolean maxDateToday, boolean edd) {
        this.context = context;
        this.editText = editText;
        this.hasMaxMinDates = maxDateToday;
        this.isEdd = edd;
    }

    @Override
    public void onClick(View view) {
        try {
            //To show current date in the datepicker
            Calendar mcurrentDate = Calendar.getInstance();
            String previouslySelectedDateString;
            if (view instanceof EditText) {
                previouslySelectedDateString = ((EditText) view).getText().toString();

                if (!("").equals(previouslySelectedDateString) && previouslySelectedDateString.length() > 2) {
                    Date previouslySelectedDate = DateUtil.yyyyMMdd.parse(previouslySelectedDateString);
                    mcurrentDate.setTime(previouslySelectedDate);
                }
            }

            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog mDatePicker = new DatePickerDialog(context, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                        String dateString = DateUtil.yyyyMMdd.format(calendar.getTime());
                        editText.setText(dateString);

                    }, mYear, mMonth, mDay);
            mDatePicker.getDatePicker().setCalendarViewShown(false);
            if (hasMaxMinDates) {
                if (isEdd) {
                    mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis() + 3600000L * 24 * 7 * 50);
                    mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
                } else {
                    mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 3600000L * 24 * 30 * 12 * 10);
                    mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 3600000L * 24 * 30 * 12 * 50);
                }
            }
            mDatePicker.show();
            DatePickerUtils.themeDatePicker(mDatePicker, new char[]{'d', 'm', 'y'});
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

}