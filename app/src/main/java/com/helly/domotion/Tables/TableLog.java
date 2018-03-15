package com.helly.domotion.Tables;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.helly.domotion.R;
import com.helly.domotion.ZoomLayout.ZoomLayout;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

// Created by helly on 1/21/18.

public class TableLog extends TableBuilder {
    private final int MaxLines = 100;
    private SharedPreferences sharedPref = null;
    private boolean firstScroll = true;

    TableLog(View view, Activity activity) {
        super(view, activity);
        sharedPref = activity.getSharedPreferences(activity.getString(R.string.shared_pref), Context.MODE_PRIVATE);
        firstScroll = true;
    }

    @Override
    public TableLayout CreateTable() {
        super.CreateTable();
        TableLayout tl = view.get().findViewById(R.id.LogNoConnTable);
        tl.removeAllViews();
        tl = view.get().findViewById(R.id.LogTable);
        tl.removeAllViews();
        return tl;
    }

    @Override
    public TableLayout CreateNoConnTable() {
        super.CreateNoConnTable();
        TableLayout tl = view.get().findViewById(R.id.LogTable);
        tl.removeAllViews();
        tl = view.get().findViewById(R.id.LogNoConnTable);
        tl.removeAllViews();
        return tl;
    }

    @Override
    public JSONArray BuildRows(JSONArray Info) {
        JSONArray SingleRowsArray = new JSONArray();

        int start = 0;
        int len =Info.optJSONArray(0).length();
        if (len > MaxLines) {
            start = len - MaxLines;
        }
        for (int i=start; i<len; i++) {
            JSONArray InnerArray = new JSONArray();
            InnerArray.put(Info.optJSONArray(0).optString(i));
            SingleRowsArray.put(InnerArray);
        }

        return SingleRowsArray;
    }

    @Override
    public List<String> BuildRow(JSONArray Info) {
        List<String> Row = new ArrayList<>();

        int len = Info.length();
        if (len > 0) {
            Row.add(Info.optString(0).trim().replace("\n", ""));
        }
        return Row;
    }

    @Override
    public void CreateText(TableRow tr, String text) {
        if (tr != null) {
            TextView tv = new TextView(activity.get());
            tv.setText(text);
            tv.setLayoutParams(layoutParams.get(tr));
            tr.addView(tv);
        }
    }

    @Override
    public void UpdateStatus(JSONArray Data) {
        TableLayout tl = view.get().findViewById(R.id.LogTable);
        int len = Data.optJSONArray(0).length();
        for (int i=0; i < len; i++) {
            String StrData = Data.optJSONArray(0).optString(i).trim().replace("\n", "");

            if (tl.getChildCount() >= MaxLines) {
                tl.removeViewAt(0);
            }

            TableRow tr = CreateRow();
            CreateText(tr, StrData);
            AddRow(tl, tr);
        }

        if (((firstScroll) || (len > 0)) &&
                (sharedPref.getBoolean(activity.get().getString(R.string.saved_log_scroll), true))) {
            ZoomLayout zl = (ZoomLayout) tl.getParent();
            zl.ScrollDown();
            firstScroll = false;
        }
    }
}
