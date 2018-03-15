package com.helly.domotion;

import com.helly.domotion.Connection.ConnectionThread;
import com.helly.domotion.Tables.TableFragment;

public class TimersFragment extends TableFragment {

    public TimersFragment() {
        super.setArguments(ConnectionThread.TIMERS, R.layout.fragment_timers);
    }
}
