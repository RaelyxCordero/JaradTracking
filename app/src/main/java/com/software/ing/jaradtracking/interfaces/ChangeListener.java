package com.software.ing.jaradtracking.interfaces;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

public interface ChangeListener {

    void onChange(DropboxAPI<AndroidAuthSession> mDBAPIHasChanged);

}
