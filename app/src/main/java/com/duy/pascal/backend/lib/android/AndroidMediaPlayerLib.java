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

package com.duy.pascal.backend.lib.android;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.duy.pascal.backend.lib.android.utils.AndroidLibraryManager;
import com.googlecode.sl4a.facade.EventFacade;
import com.googlecode.sl4a.jsonrpc.AndroidLibrary;
import com.duy.pascal.backend.lib.annotations.PascalMethod;
import com.googlecode.sl4a.rpc.RpcDefault;
import com.googlecode.sl4a.rpc.PascalParameter;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This facade exposes basic mediaPlayer functionality.
 * <p>
 * <br>
 * <br>
 * <b>Usage Notes:</b><br>
 * mediaPlayerFacade maintains a list of media streams, identified by a user supplied tag. If the
 * tag is null or blank, this tag defaults to "default"<br>
 * Basic operation is: mediaPlayOpen("file:///sdcard/MP3/sample.mp3","mytag",true)<br>
 * This will look for a media file at /sdcard/MP3/sample.mp3. Other urls should work. If the file
 * exists and is playable, this will return a true otherwise it will return a false.
 * <p>
 * <br>
 * If play=true, then the media file will play immediately, otherwise it will wait for a
 * {@link #mediaPlayStart mediaPlayerStart} command.
 * <p>
 * <br>
 * When done with the resource, use {@link #mediaPlayClose mediaPlayClose}
 * <p>
 * <br>
 * You can get information about the loaded media with {@link #mediaPlayInfo mediaPlayInfo} This
 * returns a map with the following elements:
 * <ul>
 * <li>"tag" - user supplied tag identifying this mediaPlayer.
 * <li>"loaded" - true if loaded, false if not. If false, no other elements are returned.
 * <li>"duration" - length of the media in milliseconds.
 * <li>"position" - current position of playback in milliseconds. Controlled by
 * {@link #mediaPlaySeek mediaPlaySeek}
 * <li>"isplaying" - shows whether media is playing. Controlled by {@link #mediaPlayPause
 * mediaPlayPause} and {@link #mediaPlayStart mediaPlayStart}
 * <li>"url" - the url used to open this media.
 * <li>"looping" - whether media will loop. Controlled by {@link #mediaPlaySetLooping
 * mediaPlaySetLooping}
 * </ul>
 * <br>
 * You can use {@link #mediaPlayList mediaPlayList} to get a list of the loaded tags. <br>
 * {@link #mediaIsPlaying mediaIsPlaying} will return true if the media is playing.<br>
 * <p>
 * <b>Events:</b><br>
 * A playing media will throw a <b>"media"</b> event on completion.
 * <p>
 * NB: In remote mode, a media file will continue playing after the script has finished unless an
 * explicit {@link #mediaPlayClose mediaPlayClose} event is called.
 *
 * @author Robbie Matthews (rjmatthews62@gmail.com)
 */

public class AndroidMediaPlayerLib extends AndroidLibrary implements MediaPlayer.OnCompletionListener {

    private final Map<String, MediaPlayer> mPlayers = new Hashtable<>();
    private final Map<String, String> mUrls = new Hashtable<>();
    private final Context mContext;
    private final EventFacade mEventFacade;

    public AndroidMediaPlayerLib(AndroidLibraryManager manager) {
        super(manager);
        mContext = manager.getContext();
        mEventFacade = manager.getReceiver(EventFacade.class);
    }

    private String getDefault(String tag) {
        return (tag == null || tag.equals("")) ? "default" : tag;
    }

    private MediaPlayer getPlayer(String tag) {
        tag = getDefault(tag);
        return mPlayers.get(tag);
    }

    private String getUrl(String tag) {
        tag = getDefault(tag);
        return mUrls.get(tag);
    }

    private void putMp(String tag, MediaPlayer player, String url) {
        tag = getDefault(tag);
        mPlayers.put(tag, player);
        mUrls.put(tag, url);
    }

    private void removeMp(String tag) {
        tag = getDefault(tag);
        MediaPlayer player = mPlayers.get(tag);
        if (player != null) {
            player.stop();
            player.release();
        }
        mPlayers.remove(tag);
        mUrls.remove(tag);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Open a media file", returns = "true if play successful")
    public synchronized boolean mediaPlay(
            @PascalParameter(name = "url", description = "url of media resource") String url,
            @PascalParameter(name = "tag", description = "string identifying resource") @RpcDefault(value = "default") String tag,
            @PascalParameter(name = "play", description = "start playing immediately") @RpcDefault(value = "true") Boolean play) {
        removeMp(tag);
        MediaPlayer player = getPlayer(tag);
        player = MediaPlayer.create(mContext, Uri.parse(url));
        if (player != null) {
            putMp(tag, player, url);
            player.setOnCompletionListener(this);
            if (play) {
                player.start();
            }
        }
        return player != null;
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "pause playing media file", returns = "true if successful")
    public synchronized boolean mediaPlayPause(
            @PascalParameter(name = "tag", description = "string identifying resource")
            @RpcDefault(value = "default") String tag) {
        MediaPlayer player = getPlayer(tag);
        if (player == null) {
            return false;
        }
        player.pause();
        return true;
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "start playing media file", returns = "true if successful")
    public synchronized boolean mediaPlayStart(
            @PascalParameter(name = "tag", description = "string identifying resource") @RpcDefault(value = "default") String tag) {
        MediaPlayer player = getPlayer(tag);
        if (player == null) {
            return false;
        }
        player.start();
        return mediaIsPlaying(tag);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Close media file", returns = "true if successful")
    public synchronized boolean mediaPlayClose(
            @PascalParameter(name = "tag", description = "string identifying resource") @RpcDefault(value = "default") String tag) {
        removeMp(tag);
        return true;
    }

    @PascalMethod(description = "Checks if media file is playing.", returns = "true if playing")
    public synchronized boolean mediaIsPlaying(
            @PascalParameter(name = "tag", description = "string identifying resource") @RpcDefault(value = "default") String tag) {
        MediaPlayer player = getPlayer(tag);
        return (player == null) ? false : player.isPlaying();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Information on current media", returns = "Media Information")
    public synchronized Map<String, Object> mediaPlayInfo(
            @PascalParameter(name = "tag", description = "string identifying resource") @RpcDefault(value = "default") String tag) {
        Map<String, Object> result = new HashMap<>();
        MediaPlayer player = getPlayer(tag);
        result.put("tag", getDefault(tag));
        if (player == null) {
            result.put("loaded", false);
        } else {
            result.put("loaded", true);
            result.put("duration", player.getDuration());
            result.put("position", player.getCurrentPosition());
            result.put("isplaying", player.isPlaying());
            result.put("url", getUrl(tag));
            result.put("looping", player.isLooping());
        }
        return result;
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Lists currently loaded media", returns = "List of Media Tags")
    public Set<String> mediaPlayList() {
        return mPlayers.keySet();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Set Looping", returns = "True if successful")
    public synchronized boolean mediaPlaySetLooping(
            @PascalParameter(name = "enabled") @RpcDefault(value = "true") Boolean enabled,
            @PascalParameter(name = "tag", description = "string identifying resource") @RpcDefault(value = "default") String tag) {
        MediaPlayer player = getPlayer(tag);
        if (player == null) {
            return false;
        }
        player.setLooping(enabled);
        return true;
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Seek To Position", returns = "New Position (in ms)")
    public synchronized int mediaPlaySeek(
            @PascalParameter(name = "msec", description = "Position in millseconds") Integer msec,
            @PascalParameter(name = "tag", description = "string identifying resource") @RpcDefault(value = "default") String tag) {
        MediaPlayer player = getPlayer(tag);
        if (player == null) {
            return 0;
        }
        player.seekTo(msec);
        return player.getCurrentPosition();
    }

    @Override
    public synchronized void shutdown() {
        for (String key : mPlayers.keySet()) {
            MediaPlayer player = mPlayers.get(key);
            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }
        }
        mPlayers.clear();
        mUrls.clear();
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        String tag = getTag(player);
        if (tag != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("action", "complete");
            data.put("tag", tag);
            mEventFacade.postEvent("media", data);
        }
    }

    private String getTag(MediaPlayer player) {
        for (Entry<String, MediaPlayer> m : mPlayers.entrySet()) {
            if (m.getValue() == player) {
                return m.getKey();
            }
        }
        return null;
    }
}
