package com.apkupdater.updater;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import com.apkupdater.model.Update;

interface UpdaterCallback
{
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	void onStart();
	void onUpdate(Update update);
	void onError(Throwable e);
	void onFinish(String m);

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////