package com.helly.domotion;

import com.helly.domotion.Connection.ConnectionThread;
import com.helly.domotion.Tables.TableFragment;

public class ControlsFragment extends TableFragment {

    public ControlsFragment() {
        super.setArguments(ConnectionThread.SENSORS, R.layout.fragment_controls);
    }

}
