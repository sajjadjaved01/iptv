package com.muparse;

import java.util.List;

class M3UPlaylist {

    private String playlistName;

    private String playlistParams;

    private List<M3UItem> playlistItems;

    List<M3UItem> getPlaylistItems() {
        return playlistItems;
    }

    void setPlaylistItems(List<M3UItem> playlistItems) {
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
        for (String parameter : paramsArray) {
            if (parameter.contains(paramName)) {
                return parameter.substring(parameter.indexOf(paramName) + paramName.length()).replace("=", "");
            }
        }
        return "";
    }
}
