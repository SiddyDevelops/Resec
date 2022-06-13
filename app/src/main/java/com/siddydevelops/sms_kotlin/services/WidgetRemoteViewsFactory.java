package com.siddydevelops.sms_kotlin.services;

import static android.provider.BaseColumns._ID;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.lifecycle.LiveData;

import com.siddydevelops.sms_kotlin.R;
import com.siddydevelops.sms_kotlin.data.db.SettingsDAO;
import com.siddydevelops.sms_kotlin.data.db.SettingsDatabase;
import com.siddydevelops.sms_kotlin.data.db.SettingsRepository;
import com.siddydevelops.sms_kotlin.data.db.entity.SettingsItem;
import com.siddydevelops.sms_kotlin.ui.SettingsViewModel;

import java.util.List;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private Cursor mCursor;

    private SettingsRepository settingsRepository;
    private LiveData<List<SettingsItem>> allSettings;
    private SettingsDAO dao;

    public WidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        dao = SettingsDatabase.Companion.getDatabase(applicationContext).getSettingDao();
        settingsRepository = new SettingsRepository(dao);
    }

    @Override
    public void onCreate() {
        Log.d("Widget","Init");
    }

    @Override
    public void onDataSetChanged() {
        allSettings = settingsRepository.getAllSettings();
        if (mCursor != null) {
            mCursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        Uri uri = Uri.parse("content://" + "com.siddydevelops.sms_kotlin").buildUpon().appendPath("resec").build();
        mCursor = mContext.getContentResolver().query(uri,
                null,
                null,
                null,
                _ID + " DESC");

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        //return mCursor == null ? 0 : mCursor.getCount();
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_layout);
        rv.setTextViewText(R.id.soundProfileTV, mCursor.getString(1));

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return mCursor.moveToPosition(position) ? mCursor.getLong(0) : position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
