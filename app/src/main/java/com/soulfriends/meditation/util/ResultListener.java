package com.soulfriends.meditation.util;

public interface ResultListener {

    public void onSuccess(Integer id, String message);
    public void onFailure(Integer id, String message);
}
