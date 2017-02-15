package com.muparse;

import java.util.List;

/**
 * Created by fedor on 25.11.2016.
 */

public class M3UPlaylist {

    private String playlistName;

    private String playlistParams;

    private List<M3UItem> playlistItems;

    public List<M3UItem> getPlaylistItems() {
        return playlistItems;
    }

    public void setPlaylistItems(List<M3UItem> playlistItems) {
        this.playlistItems = playlistItems;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistParams() {
        return playlistParams;
    }

    public void setPlaylistParams(String playlistParams) {
        this.playlistParams = playlistParams;
    }

    public String getSingleParameter(String paramName) {
        String[] paramsArray = this.playlistParams.split(" ");
        for (int i = 0; i < paramsArray.length; i++) {
            String parameter = paramsArray[i];
            if (parameter.contains(paramName)) {
                String paramValue = parameter.substring(parameter.indexOf(paramName) + paramName.length()).replace("=", "");
                return paramValue;
            }
        }
        return "";
    }
}
