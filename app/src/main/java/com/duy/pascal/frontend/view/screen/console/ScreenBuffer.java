package com.duy.pascal.frontend.view.screen.console;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Duy on 26-Mar-17.
 */

public class ScreenBuffer {
    public int firstIndex;
    public char[] screenBuffer;
    public ByteQueue inputBuffer = new ByteQueue();

    private FileInputStream fileInputStream;
    private FileOutputStream fileOutputStream;

    /**
     * save current data to file
     */
    public void store() {
// TODO: 26-Mar-17
    }

    /**
     * read data from then file
     */
    public void restore() {
// TODO: 26-Mar-17
    }

    /**
     * clear data
     */
    public void clearBuffer() {
    }

    public int getFirstIndex() {
        return firstIndex;
    }


}
