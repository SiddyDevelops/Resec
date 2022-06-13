package com.siddydevelops.sms_kotlin.services;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class AppWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new AppWidgetRemoteViewsFactory(this.getApplicationContext());
    }
}
