package com.example.empty;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PlannerItemFirebase {

    private PlannerItemModel plannerItemModel;

    // default = 0; success = 1; fail = 2; miss = 3
    private int status;

    private DateStr dateStr;

    public PlannerItemFirebase(PlannerItemModel plannerItemModel, String dateStr, int status) {
      this.plannerItemModel = plannerItemModel;
      this.status = status;
      this.dateStr = new DateStr(dateStr);
    }

}
