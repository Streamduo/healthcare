package com.sxy.healthcare.me.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import com.sxy.healthcare.common.event.RxBus;
import com.sxy.healthcare.me.event.BirthEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment  implements DatePickerDialog.OnDateSetListener{

    private String TAG = DatePickerFragment.class.getCanonicalName();

    private String birth;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        Log.e(TAG, "year:"+year+";monthOfYear:"+monthOfYear+";dayOfMonth:"+dayOfMonth);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(year+"-");
        stringBuffer.append(monthOfYear+1+"-");
        stringBuffer.append(dayOfMonth+"");
        birth = stringBuffer.toString();

        BirthEvent birthEvent = new BirthEvent();
        birthEvent.setBirth(birth);

      //  RxBus.get().post(birthEvent);
        EventBus.getDefault().post(birthEvent);
    }

    public String getBirth() {
        return birth;
    }
}
