package com.helly.domotion;

import com.helly.domotion.Connection.ConnectionThread;
import com.helly.domotion.Tables.TableFragment;

public class HolidaysFragment extends TableFragment {

    public HolidaysFragment() {
        super.setArguments(ConnectionThread.HOLIDAYS, R.layout.fragment_holidays);
    }

}
