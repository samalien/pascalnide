/*
 *  Copyright 2017 Tran Le Duy
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

package com.googlecode.sl4a.facade;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.provider.Contacts.People;

import com.duy.pascal.backend.lib.android.AndroidUtilsLib;
import com.duy.pascal.backend.lib.android.utils.FacadeManager;
import com.googlecode.sl4a.jsonrpc.RpcReceiver;
import com.googlecode.sl4a.rpc.PascalMethod;
import com.googlecode.sl4a.rpc.RpcOptional;
import com.googlecode.sl4a.rpc.RpcParameter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * A selection of commonly used intents. <br>
 * <br>
 * These can be used to trigger some common tasks.
 */
public class CommonIntentsFacade extends RpcReceiver {

    private final AndroidUtilsLib mAndroidFacade;

    public CommonIntentsFacade(FacadeManager manager) {
        super(manager);
        mAndroidFacade = manager.getReceiver(AndroidUtilsLib.class);
    }

    @Override
    public void shutdown() {
    }

    @PascalMethod(description = "Display content to be picked by URI (e.g. contacts)", returns = "A map of result values.")
    public Intent pick(@RpcParameter(name = "uri") String uri) throws JSONException {
        return mAndroidFacade.startActivityForResult(Intent.ACTION_PICK, uri, null, null, null, null);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Starts the barcode scanner.", returns = "A Map representation of the result Intent.")
    public Intent scanBarcode() throws JSONException {
        return mAndroidFacade.startActivityForResult("com.google.zxing.client.android.SCAN", null,
                null, null, null, null);
    }

    private void view(Uri uri, String type) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, type);
        mAndroidFacade.startActivity(intent);
    }

    @PascalMethod(description = "Start activity with view action by URI (i.e. browser, contacts, etc.).")
    public void view(
            @RpcParameter(name = "uri") String uri,
            @RpcParameter(name = "type", description = "MIME type/subtype of the URI") @RpcOptional String type,
            @RpcParameter(name = "extras", description = "a Map of extras to add to the Intent") @RpcOptional JSONObject extras)
            throws Exception {
        mAndroidFacade.startActivity(Intent.ACTION_VIEW, uri, type, extras, true, null, null);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Opens a map search for query (e.g. pizza, 123 My Street).")
    public void viewMap(@RpcParameter(name = "query, e.g. pizza, 123 My Street") String query)
            throws Exception {
        view("geo:0,0?q=" + query, null, null);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Opens the list of contacts.")
    public void viewContacts() throws JSONException {
        view(People.CONTENT_URI, null);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Opens the browser to display a local HTML file.")
    public void viewHtml(
            @RpcParameter(name = "path", description = "the path to the HTML file") String path)
            throws JSONException {
        File file = new File(path);
        view(Uri.fromFile(file), "text/html");
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Starts a search for the given query.")
    public void search(@RpcParameter(name = "query") String query) {
        Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        mAndroidFacade.startActivity(intent);
    }
}
