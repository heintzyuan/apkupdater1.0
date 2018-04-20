package com.apkupdater.updater;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import android.content.Context;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class UpdaterAPKPure
	extends UpdaterBase
{
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	static final private String BaseUrl = "https://www.coolapk.com";
	static final private String Type = "APKPure";

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public UpdaterAPKPure(
		Context context,
		String pname,
		String cversion
	) {
		super(context, pname, cversion, "APKPure");
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected String getUrl(
		String pname
	) {
		return BaseUrl + "/search?q=" + pname;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected UpdaterStatus parseUrl(
		String url
	) {
		try {
			Document doc = Jsoup.connect(url).get();

			// Search for URL ending on / + package name
			Elements elements = doc.getElementsByAttributeValue("href", "/apk/"+mPname);
			if(elements == null || elements.size() == 0) {
				return UpdaterStatus.STATUS_UPDATE_NOT_FOUND;
			}

			// Build new url to request
			url = BaseUrl + elements.get(0).attr("href");

			doc = Jsoup.connect(url).get();

			// Try to get the version
			//elements = doc.getElementsByAttributeValue("itemprop", "softwareVersion");
			elements = doc.getElementsByClass("list_app_info");
			if(elements == null || elements.size() == 0) {
				return UpdaterStatus.STATUS_UPDATE_NOT_FOUND;
			}
			String version = elements.get(0).text();

			// If version is old, report update
			if (compareVersions(mCurrentVersion, version) == -1) {
				mResultUrl = url;
				mResultVersion = elements.get(0).text();
				return UpdaterStatus.STATUS_UPDATE_FOUND;
			}

			return UpdaterStatus.STATUS_UPDATE_NOT_FOUND;
		} catch (Exception e) {
			mError = addCommonInfoToError(e);
			return UpdaterStatus.STATUS_ERROR;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////