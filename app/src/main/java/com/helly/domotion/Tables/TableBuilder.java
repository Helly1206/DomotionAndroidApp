package com.helly.domotion.Tables;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.helly.domotion.Buttons.ButtonAdd;
import com.helly.domotion.Buttons.ButtonAnalog;
import com.helly.domotion.Buttons.ButtonBlindsUpDown;
import com.helly.domotion.Buttons.ButtonControlOnOff;
import com.helly.domotion.Buttons.ButtonDelete;
import com.helly.domotion.Buttons.ButtonDeviceOnOff;
import com.helly.domotion.Buttons.ButtonEdit;
import com.helly.domotion.Buttons.ButtonInterface;
import com.helly.domotion.Buttons.ButtonRecalc;
import com.helly.domotion.LoginDialog;
import com.helly.domotion.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

// Created by helly on 1/21/18.

public class TableBuilder implements TableInterface {
    protected WeakReference<View> view = null;
    WeakReference<Activity> activity = null;
    HashMap<TableRow, TableRow.LayoutParams> layoutParams = new HashMap<>();
    private HashMap<TableRow, Boolean> EvenRow = new HashMap<>();
    SparseArray<String> NameMap = new SparseArray<>();
    SparseArray<TableRow> RowMap = new SparseArray<>();
    private boolean ThisRowEven = true;

    TableBuilder(View view, Activity activity) {
        this.view = new WeakReference<>(view);
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public TableLayout CreateTable() {
        ThisRowEven = true;
        NameMap.clear();
        RowMap.clear();
        // Needs to be implemented by subclass
        return null;
    }

    @Override
    public TableLayout CreateNoConnTable() {
        // Needs to be implemented by subclass
        return null;
    }

    @Override
    public List<String> BuildColumns(JSONArray Info) {
        // Needs to be implemented by subclass
        return null;
    }

    @Override
    public JSONArray BuildRows(JSONArray Info) {
        return Info.optJSONArray(0).optJSONArray(1);
    }

    @Override
    public List<String> BuildRow(JSONArray Info) {
        // Needs to be implemented by subclass
        return null;
    }

    @Override
    public TableRow CreateRow() {
        TableRow tr = new TableRow(activity.get());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
        tr.setLayoutParams(lp);
        tr.setGravity(Gravity.CENTER_VERTICAL);
        layoutParams.put(tr, lp);
        EvenRow.put(tr, ThisRowEven);
        ThisRowEven ^= true;
        return tr;
    }

    @Override
    public void CreateTopText(TableRow tr, String text) {
        if (tr != null) {
            TextView tv = new TextView(activity.get());
            tv.setText(text);
            tv.setTextColor(activity.get().getResources().getColor(R.color.tableWhite));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (activity.get().getResources().getDimension(R.dimen.icon_size)*0.75));
            tv.setHeight((int) activity.get().getResources().getDimension(R.dimen.icon_size));
            tv.setTypeface(null, Typeface.BOLD);
            tv.setLayoutParams(layoutParams.get(tr));
            tv.setBackgroundResource(R.drawable.table_head);
            tr.addView(tv);
        }
    }

    @Override
    public TableRow CreateBottomRow() {
        // Needs to be implemented by subclass
        return null;
    }

    @Override
    public void CreateText(TableRow tr, String text) {
        if (tr != null) {
            TextView tv = new TextView(activity.get());
            tv.setText(text);
            tv.setLayoutParams(layoutParams.get(tr));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (activity.get().getResources().getDimension(R.dimen.icon_size)*0.75));
            tv.setHeight((int) activity.get().getResources().getDimension(R.dimen.icon_size));
            if (EvenRow.get(tr)) {
                tv.setBackgroundResource(R.drawable.table_even);
            } else {
                tv.setBackgroundResource(R.drawable.table_odd);
            }
            tr.addView(tv);
        }
    }

    @Override
    public void CreateButtons(TableRow tr, JSONArray RowData) {
        // Needs to be implemented by subclass
    }

    @Override
    public ButtonInterface CreateButton(TableRow tr, ButtonInterface.ButtonType buttonType) {
        View ReturnObject = null;

        switch (buttonType) {
            case BLINDS:
                ReturnObject = new ButtonBlindsUpDown(activity.get());
                break;
            case SWITCH:
                ReturnObject = new ButtonDeviceOnOff(activity.get());
                break;
            case CONTROL:
                ReturnObject = new ButtonControlOnOff(activity.get());
                break;
            case ANALOG:
                ReturnObject = new ButtonAnalog(activity.get());
                break;
            case EDIT:
                ReturnObject = new ButtonEdit(activity.get());
                break;
            case DELETE:
                ReturnObject = new ButtonDelete(activity.get());
                break;
            case RECALC:
                ReturnObject = new ButtonRecalc(activity.get());
                break;
            case ADD:
                ReturnObject = new ButtonAdd(activity.get());
                break;
            case VOID:
                break;
        }

        if (ReturnObject != null) {
            ReturnObject.setLayoutParams(layoutParams.get(tr));
            if (EvenRow.get(tr)) {
                ReturnObject.setBackgroundResource(R.drawable.table_even);
            } else {
                ReturnObject.setBackgroundResource(R.drawable.table_odd);
            }
            tr.addView(ReturnObject);

            ReturnObject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view instanceof ButtonInterface) {
                            int idIndex = RowMap.indexOfValue((TableRow)(view).getParent());
                            if (idIndex >= 0) { // otherwise not found
                                HandleButton((ButtonInterface)view, RowMap.keyAt(idIndex));
                            } else {
                                HandleButton((ButtonInterface)view);
                            }
                        }
                    }
                }
            );
        }

        return (ButtonInterface) ReturnObject;
    }

    @Override
    public void HandleButton(ButtonInterface button, int id) {
        switch (button.GetType()) {
            case SWITCH:
            case ANALOG:
            case BLINDS:
            case CONTROL:
                Toast.makeText(activity.get(), R.string.button_set, Toast.LENGTH_SHORT).show();
                TableFragment fragment = getFragment();
                if (fragment != null) {
                    fragment.Update(NameMap.get(id), button.Get());
                }
                break;
            case EDIT:
                // popup --> override
                Toast.makeText(activity.get(), R.string.button_edit, Toast.LENGTH_SHORT).show();
                break;
            case DELETE:
                // popup --> override
                Toast.makeText(activity.get(), R.string.button_delete, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void HandleButton(ButtonInterface button) {
        switch (button.GetType()) {
            case RECALC:
                // execute recalc --> override
                Toast.makeText(activity.get(), R.string.button_recalc, Toast.LENGTH_SHORT).show();
                break;
            case ADD:
                // popup --> override
                Toast.makeText(activity.get(), R.string.button_add, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void AddRow(TableLayout tl, TableRow tr) {
        if ((tl != null) && (tr != null)) {
            tl.addView(tr, layoutParams.get(tr));
        }
    }

    @Override
    public JSONObject GetValues(JSONArray Data) {
        // Needs to be implemented by subclass
        return null;
    }

    @Override
    public void UpdateStatus(JSONArray Data) {
        // Needs to be implemented by subclass
    }

    @Override
    public void SetRowValue(int id, String Value) {
        // Needs to be implemented by subclass
    }

    @Override
    public void SetButtonValue(ButtonInterface Button, String Value, ButtonInterface.ButtonType buttonType) {
        if (buttonType != ButtonInterface.ButtonType.VOID) {
            Button.Set(Value);
        }
    }

    @Override
    public int GetSize() {
        return NameMap.size();
    }

    @Override
    public boolean NoConnection(TableLayout tl, int Reason) {
        boolean RetryButton = true;
        if (tl != null) {
            TableRow tr = new TableRow(activity.get());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
            tr.setLayoutParams(lp);
            TextView tv = new TextView(activity.get());
            tv.setTextSize(18);
            switch (Reason) {
                case TableInterface.CONNECTION_ERROR_REQUEST:
                    tv.setText(activity.get().getString(R.string.error_request));
                    break;
                case TableInterface.CONNECTION_ERROR_RESPONSE:
                    tv.setText(activity.get().getString(R.string.error_response));
                    break;
                case TableInterface.CONNECTION_ERROR_SSL:
                    tv.setText(activity.get().getString(R.string.error_ssl));
                    break;
                case TableInterface.CONNECTION_ERROR_AUTH:
                    tv.setText(activity.get().getString(R.string.error_auth));
                    RetryButton = false;
                    break;
                default: //TableInterface.CONNECTION_ERROR_NO_CONNECTION
                    tv.setText(activity.get().getString(R.string.error_no_connection));
                    break;
            }
            tv.setPadding(20, 20, 20, 20);
            tv.setLayoutParams(lp);
            tr.addView(tv);
            tl.addView(tr, lp);
            tr = new TableRow(activity.get());
            tr.setLayoutParams(lp);
            Button bt = new Button(activity.get());
            if (RetryButton) {
                bt.setText(R.string.button_retry);
                bt.setPadding(20, 20, 20, 20);
                bt.setLayoutParams(lp);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TableFragment fragment = getFragment();
                        if (fragment != null) {
                            fragment.onChange();
                            }
                        }
                });
            } else {
                bt.setText(R.string.button_login);
                bt.setPadding(20, 20, 20, 20);
                bt.setLayoutParams(lp);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoginDialog loginDialog = new LoginDialog();
                        loginDialog.showDialog(((AppCompatActivity)activity.get()).getSupportFragmentManager());
                    }
                });
            }
            tr.addView(bt);
            tl.addView(tr, lp);
        }
        return RetryButton;
    }

    @Nullable
    TableFragment getFragment() {
        List<Fragment> fragments = ((AppCompatActivity)activity.get()).getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment: fragments) {
                if (fragment != null && fragment.isVisible()) {
                    return (TableFragment)fragment;
                }
            }
        }
        return null;
    }
}
