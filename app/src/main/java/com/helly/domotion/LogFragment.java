package com.helly.domotion;

import com.helly.domotion.Connection.ConnectionThread;
import com.helly.domotion.Tables.TableFragment;

public class LogFragment extends TableFragment {
    public LogFragment() {
        super.setArguments(ConnectionThread.LOG, R.layout.fragment_log);
    }
}
