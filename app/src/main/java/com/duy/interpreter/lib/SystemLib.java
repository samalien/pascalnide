package com.duy.interpreter.lib;

import com.duy.pascal.compiler.activities.ExecuteActivity;

import java.util.Map;

/**
 * Created by Duy on 07-Mar-17.
 */

public class SystemLib implements PascalLibrary {


    private ExecuteActivity activity;

    public SystemLib(ExecuteActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean instantiate(Map<String, Object> pluginargs) {
        return false;
    }


    public boolean keyPressed() {
        if (activity != null) activity.getConsoleView().keyPressed();
        return false;
    }
}
