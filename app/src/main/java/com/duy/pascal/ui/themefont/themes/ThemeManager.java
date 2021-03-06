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

package com.duy.pascal.ui.themefont.themes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.duy.pascal.ui.R;
import com.duy.pascal.ui.themefont.model.CodeTheme;
import com.duy.pascal.ui.themefont.themes.database.ThemeDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Duy on 12-Jul-17.
 */

public class ThemeManager {
    /**
     * save theme in preferences
     * "theme.name.attr"
     */
    private static final String THEME_FILE = "THEME_FILE";

    /**
     * user define theme
     */
    private static HashMap<String, CodeTheme> customThemes;

    /**
     * builtin theme, include theme from asset and theme from xml file
     */
    private static HashMap<String, CodeTheme> builtinThemes;


    public static HashMap<String, CodeTheme> getAll(Context context) {
        loadAll(context); //ensure load all theme
        HashMap<String, CodeTheme> result = new HashMap<>();
        String[] names = context.getResources().getStringArray(R.array.code_themes);
        for (String name : names) {
            CodeTheme codeTheme = new CodeTheme(false, true);
            loadFromXml(name, codeTheme, context);
            result.put(name, codeTheme);
        }

        result.putAll(builtinThemes);
        result.putAll(customThemes);
        return result;
    }

    private static void loadFromXml(String name, @NonNull CodeTheme codeTheme, Context context) {
        int style = getStyleFromName(context, name);
        TypedArray typedArray = context.obtainStyledAttributes(style, R.styleable.CodeTheme);
        typedArray.getInteger(R.styleable.CodeTheme_background_color, R.color.color_background_color);

        codeTheme.setTextColor(typedArray.getInteger(R.styleable.CodeTheme_normal_text_color,
                R.color.color_normal_text_color));
        codeTheme.setBackgroundColor(typedArray.getInteger(R.styleable.CodeTheme_background_color,
                R.color.color_background_color));
        codeTheme.setErrorColor(typedArray.getInteger(R.styleable.CodeTheme_error_color,
                R.color.color_error_color));
        codeTheme.setNumberColor(typedArray.getInteger(R.styleable.CodeTheme_number_color,
                R.color.color_number_color));
        codeTheme.setKeyWordColor(typedArray.getInteger(R.styleable.CodeTheme_key_word_color,
                R.color.color_key_word_color));
        codeTheme.setCommentColor(typedArray.getInteger(R.styleable.CodeTheme_comment_color,
                R.color.color_comment_color));
        codeTheme.setStringColor(typedArray.getInteger(R.styleable.CodeTheme_string_color,
                R.color.color_string_color));
        codeTheme.setBooleanColor(typedArray.getInteger(R.styleable.CodeTheme_boolean_color,
                R.color.color_boolean_color));
        codeTheme.setOptColor(typedArray.getInteger(R.styleable.CodeTheme_opt_color,
                R.color.color_opt_color));
        codeTheme.setName(name);
        typedArray.recycle();
    }

    public static int getStyleFromName(Context context, String name) {
        if (name.equals(context.getString(R.string.default_theme))) {
            return R.style.CodeTheme;
        } else if (name.equals(context.getString(R.string.BrightYellow))) {
            return R.style.CodeTheme_BrightYellow;
        } else if (name.equals(context.getString(R.string.DarkGray))) {
            return R.style.CodeTheme_DarkGray;
        } else if (name.equals(context.getString(R.string.EspressoLibre))) {
            return R.style.CodeTheme_EspressoLibre;
        } else if (name.equals(context.getString(R.string.Idel))) {
            return R.style.CodeTheme_Idel;
        } else if (name.equals(context.getString(R.string.KFT2))) {
            return R.style.CodeTheme_KFT2;
        } else if (name.equals(context.getString(R.string.Modnokai_Coffee))) {
            return R.style.CodeTheme_ModnokaiCoffee;
        } else {
            return R.style.CodeTheme;
        }
    }

    private static void loadFromFile(Context context) {
        builtinThemes = new HashMap<>();

        //load from asset
        try {
            InputStream is = context.getAssets().open("themes/themes.properties");
            Properties properties = new Properties();
            properties.load(is);
            int id = 1;
            while (true) {
                try {
                    CodeTheme codeTheme = new CodeTheme(true, true);
                    codeTheme.putColor("background_color", loadColor(properties, id, "background_color"));
                    codeTheme.putColor("normal_text_color", loadColor(properties, id, "normal_text_color"));
                    codeTheme.putColor("number_color", loadColor(properties, id, "number_color"));
                    codeTheme.putColor("key_word_color", loadColor(properties, id, "key_word_color"));
                    codeTheme.putColor("string_color", loadColor(properties, id, "string_color"));
                    codeTheme.putColor("comment_color", loadColor(properties, id, "comment_color"));
                    codeTheme.putColor("error_color", loadColor(properties, id, "error_color"));
                    codeTheme.putColor("opt_color", loadColor(properties, id, "opt_color"));
                    codeTheme.setName(id + "");
                    builtinThemes.put(Integer.toString(id), codeTheme);
                    id++;
                } catch (Exception e) {
//                    e.printStackTrace();
                    return;
                }
            }
        } catch (IOException ignored) {
        }

        //load from xml
        String[] names = context.getResources().getStringArray(R.array.code_themes);
        for (String name : names) {
            CodeTheme codeTheme = new CodeTheme(true);
            loadFromXml(name, codeTheme, context);
            builtinThemes.put(name, codeTheme);
        }

    }

    private static Integer loadColor(Properties properties, int id, String name)
            throws Exception {
        String color = properties.getProperty("theme." + id + "." + name);
        if (color == null) throw new RuntimeException("Can not find properties " + name);
        return Color.parseColor(color.trim());
    }


    public static CodeTheme getTheme(String name, Context context) {
        loadAll(context);
        if (builtinThemes.containsKey(name)) return builtinThemes.get(name);
        if (customThemes.containsKey(name)) return customThemes.get(name);

        //default theme
        CodeTheme codeTheme = new CodeTheme(true);
        loadFromXml(name, codeTheme, context);
        return codeTheme;
    }

    private static void loadCustomThemes(Context context) {
        customThemes = new HashMap<>();
        ThemeDatabase themeDatabase = new ThemeDatabase(context);
        ArrayList<CodeTheme> all = themeDatabase.getAll();
        for (CodeTheme codeTheme : all) {
            codeTheme.setPremium(true);
            customThemes.put(codeTheme.getName(), codeTheme);
        }
    }

    public static CodeTheme getDefault(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(THEME_FILE, Context.MODE_PRIVATE);
        String name = preferences.getString(context.getString(R.string.key_code_theme), "");
        return getTheme(name, context);
    }

    public synchronized static void loadAll(Context context) {
        if (builtinThemes == null) {
            loadFromFile(context);
        }
        if (customThemes == null) {
            loadCustomThemes(context);
        }
    }

    public synchronized static void reload(Context context) {
        builtinThemes = null;
        customThemes = null;
        loadAll(context);
    }
}
