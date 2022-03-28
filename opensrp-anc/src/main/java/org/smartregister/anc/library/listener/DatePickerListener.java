package org.smartregister.anc.library.listener;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.vijay.jsonwizard.utils.DatePickerUtils;

import org.smartregister.clientandeventmodel.DateUtil;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DatePickerListener implements View.OnClickListener {
    public static final String TAG = DatePickerListener.class.getCanonicalName();

    private final EditText editText;
    private boolean maxDateToday = false;
    private Context context;

    public DatePickerListener(Context context, EditText editText, boolean maxDateToday) {
        this.context = context;
        this.editText = editText;
        this.maxDateToday = maxDateToday;
    }

    @Override
    public void onClick(View view) {
        //To show current date in the datepicker
        Calendar mcurrentDate = Calendar.getInstance();

        String previouslySelectedDateString;

        if (view instanceof EditText) {
            previouslySelectedDateString = ((EditText) view).getText().toString();

            if (!("").equals(previouslySelectedDateString) && previouslySelectedDateString.length() > 2) {
                try {
                    Date previouslySelectedDate = DateUtil.yyyyMMdd.parse(previouslySelectedDateString);
                    mcurrentDate.setTime(previouslySelectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(context, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                (datepicker, selectedyear, selectedmonth, selectedday) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, selectedyear);
                    calendar.set(Calendar.MONTH, selectedmonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedday);

                    String dateString = DateUtil.yyyyMMdd.format(calendar.getTime());
                    editText.setText(dateString);

                }, mYear, mMonth, mDay);
        mDatePicker.getDatePicker().setCalendarViewShown(false);
        if (maxDateToday) {
            mDatePicker.getDatePicker().setMaxDate(new Date().getTime());
        }
        mDatePicker.show();

        try {
            DatePickerUtils.themeDatePicker(mDatePicker, new char[]{'d', 'm', 'y'});
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

}