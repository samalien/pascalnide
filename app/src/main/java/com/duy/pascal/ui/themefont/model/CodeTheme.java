/*
 *  Copyright (c) 2017 Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.pascal.ui.themefont.model;

import android.graphics.Color;

import com.duy.pascal.ui.editor.highlight.IEditorColorScheme;
import com.duy.pascal.ui.utils.DLog;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import static com.duy.pascal.ui.themefont.themes.database.CodeThemeContract.CodeThemeEntry.BACKGROUND;
import static com.duy.pascal.ui.themefont.themes.database.CodeThemeContract.CodeThemeEntry.BOOLEAN;
import static com.duy.pascal.ui.themefont.themes.database.CodeThemeContract.CodeThemeEntry.COMMENT;
import static com.duy.pascal.ui.themefont.themes.database.CodeThemeContract.CodeThemeEntry.ERROR;
import static com.duy.pascal.ui.themefont.themes.database.CodeThemeContract.CodeThemeEntry.KEY_WORD;
import static com.duy.pascal.ui.themefont.themes.database.CodeThemeContract.CodeThemeEntry.NORMAL;
import static com.duy.pascal.ui.themefont.themes.database.CodeThemeContract.CodeThemeEntry.NUMBER;
import static com.duy.pascal.ui.themefont.themes.database.CodeThemeContract.CodeThemeEntry.OPERATOR;
import static com.duy.pascal.ui.themefont.themes.database.CodeThemeContract.CodeThemeEntry.STRING;

public class CodeTheme implements Serializable, IEditorColorScheme {

    private static final String TAG = "CodeTheme";
    private final boolean builtin;
    private boolean premium = false;
    private HashMap<String, Integer> colors = new HashMap<>();
    private String name;

    public CodeTheme(boolean builtin) {
        this.builtin = builtin;
    }

    public CodeTheme(boolean premium, boolean builtin) {
        this.premium = premium;
        this.builtin = builtin;
    }

    @Override
    public String toString() {
        return "CodeTheme{" +
                "builtin=" + builtin +
                ", premium=" + premium +
                ", colors=" + colors +
                ", name='" + name + '\'' +
                '}';
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Integer> getColors() {
        return colors;
    }

    public int getBackgroundColor() {
        return getColor(BACKGROUND);
    }

    public int getTextColor() {
        return getColor(NORMAL);
    }

    public void setTextColor(int integer) {
        putColor(NORMAL, integer);
    }


    public boolean isBuiltin() {
        return this.builtin;
    }

    public void putColor(String name, Integer color) {
        this.colors.put(name, color);
    }

    public Integer getColor(String name) {
        return this.colors.get(name);
    }

    public void setKeyWordColor(int integer) {
        putColor(KEY_WORD, integer);
    }

    public void setBooleanColor(int integer) {
        putColor(BOOLEAN, integer);
    }

    public int getErrorColor() {
        return getColor(ERROR);
    }

    public void setErrorColor(int integer) {
        putColor(ERROR, integer);
    }

    public int getNumberColor() {
        return getColor(NUMBER);
    }

    public void setNumberColor(int integer) {
        putColor(NUMBER, integer);
    }

    public int getKeywordColor() {
        return getColor(KEY_WORD);
    }

    public int getOptColor() {
        return getColor(OPERATOR);
    }

    public void setOptColor(int integer) {
        putColor(OPERATOR, integer);
    }

    public int getCommentColor() {
        return getColor(COMMENT);
    }

    public void setCommentColor(int integer) {
        putColor(COMMENT, integer);
    }

    public int getStringColor() {
        return getColor(STRING);
    }

    public void setStringColor(int integer) {
        putColor(STRING, integer);
    }

    public int getDebugColor() {
        int background = getBackgroundColor();
        float[] hsv = new float[3];
        Color.colorToHSV(background, hsv);
        hsv[2] = Math.min(1, Math.max(0.1f, hsv[2]) * 1.25f);//brightness color
        DLog.d(TAG, "getDebugColor: " + Arrays.toString(hsv));
        return Color.HSVToColor(hsv);
    }

    public void setBackgroundColor(int integer) {
        putColor("background_color", integer);
    }

    public int getBracketColor() {
        return getKeywordColor();
    }

}
