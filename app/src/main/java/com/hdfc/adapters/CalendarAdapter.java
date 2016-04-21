package com.hdfc.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.models.ActivityModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends BaseAdapter {
    public final static SimpleDateFormat writeFormatDate = new SimpleDateFormat("dd", Locale.US);
    public final static SimpleDateFormat writeFormatMonth = new SimpleDateFormat("MMMM", Locale.US);
    public final static SimpleDateFormat writeFormatYear = new SimpleDateFormat("yyyy", Locale.US);
    public final static SimpleDateFormat readFormat = new SimpleDateFormat("kk:mm aa dd MMM yyyy",
            Locale.US);
    private static final int DAY_OFFSET = 1;
    private final Context _context;
    private final List<String> list;
    public List<ActivityModel> activityModels = new ArrayList<>();
    private int currentDayOfMonth;
    private int currentWeekDay;

    // Days in Current Month
    public CalendarAdapter(Context context, int month, int year, List<ActivityModel> _activityModels) {
        super();
        this._context = context;
        this.list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
        activityModels = _activityModels;

        // Print Month
        printMonth(month, year);

        //todo proper color scheme, default date selection,

        // Find Number of Events
       // eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
    }

    private String getMonthAsString(int i) {
        return Config.months[i];
    }

    private int getNumberOfDaysOfMonth(int i) {
        return Config.daysOfMonth[i];
    }

    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    private void printMonth(int mm, int yy) {
        int trailingSpaces = 0;
        int daysInPrevMonth = 0;
        int prevMonth = 0;
        int prevYear = 0;
        int nextMonth = 0;
        int nextYear = 0;

        int currentMonth = mm - 1;
        int daysInMonth = getNumberOfDaysOfMonth(currentMonth);

        // Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
        GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

        if (currentMonth == 11) {
            prevMonth = currentMonth - 1;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 0;
            prevYear = yy;
            nextYear = yy + 1;
        } else if (currentMonth == 0) {
            prevMonth = 11;
            prevYear = yy - 1;
            nextYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 1;
        } else {
            prevMonth = currentMonth - 1;
            nextMonth = currentMonth + 1;
            nextYear = yy;
            prevYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
        }

        trailingSpaces = cal.get(Calendar.DAY_OF_WEEK) - 1;

        if (cal.isLeapYear(cal.get(Calendar.YEAR)) && currentMonth == 1) {
            ++daysInMonth;
        }

        // weeks
        for (int i = 0; i < Config.weekNames.length; i++) {
            list.add(Config.weekNames[i] + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" +
                    yy);
        }

        // Trailing Month days
        for (int i = 0; i < trailingSpaces; i++) {
            list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" +
                    "-" + getMonthAsString(prevMonth) + "-" + prevYear);
        }

        // Current Month Days
        for (int i = 1; i <= daysInMonth; i++) {
            if (i == getCurrentDayOfMonth())
                list.add(String.valueOf(i) + "-GREEN" + "-" + getMonthAsString(currentMonth) + "-" +
                        yy);
            else
                list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" +
                        yy);
        }

        // Leading Month days
        for (int i = 0; i < list.size() % 7; i++) {
            list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" +
                    nextYear);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.calendar_cell_view, parent, false);
        }

        // Get a reference to the Day gridcell
        Button gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
        String[] day_color = list.get(position).split("-");

        String theday = day_color[0];
        String themonth = day_color[2];
        String theyear = day_color[3];

        // Set the Day GridCell
        gridcell.setText(theday);
        gridcell.setTag(theday + "-" + themonth + "-" + theyear);

        if (day_color[1].equals("GREY")) {
            gridcell.setTextColor(Color.LTGRAY);
        }

        if (day_color[1].equals("WHITE")) {
            gridcell.setTextColor(Color.BLACK);
        }

        if (day_color[1].equals("BLUE")) {
            gridcell.setTextColor(Color.BLUE);
        }

        if (day_color[1].equals("GREEN")) {
            gridcell.setTextColor(Color.RED);
            //gridcell.setTextColor(_context.getResources().getColor(R.color.colorPrimary));
        }

        if(position>6) {

            int iDay = Integer.parseInt(day_color[0]);
            int iYear = Integer.parseInt(day_color[3]);

            try {
                for (ActivityModel activityModel : activityModels) {

                    Date date = readFormat.parse(activityModel.getStrActivityDate());

                    String strActivityMonth = writeFormatMonth.format(date);
                    int iActivityYear = Integer.parseInt(writeFormatYear.format(date));
                    int iActivityDate = Integer.parseInt(writeFormatDate.format(date));

                    if (iActivityYear == iYear &&
                            strActivityMonth.trim().equalsIgnoreCase(themonth) &&
                            iActivityDate == iDay) {

                        gridcell.setBackground(_context.getResources().
                                getDrawable(R.drawable.bottom_border_green));

                        //gridcell.setBackground(_context.getResources().getDrawable(R.drawable.bottom_border_green));
                        gridcell.setTextColor(Color.BLACK);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return row;
    }

    public int getCurrentDayOfMonth() {
        return currentDayOfMonth;
    }

    private void setCurrentDayOfMonth(int currentDayOfMonth) {
        this.currentDayOfMonth = currentDayOfMonth;
    }

    public int getCurrentWeekDay() {
        return currentWeekDay;
    }

    public void setCurrentWeekDay(int currentWeekDay) {
        this.currentWeekDay = currentWeekDay;
    }
}