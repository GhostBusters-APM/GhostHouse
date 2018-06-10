package com.github.ghostbusters.ghosthouse.helper.unity;

public class ArPlugin {

	public void call(final ArPluginCallback callback) {
		// Do something
		callback.onSuccess("onSuccess");
		// Do something horrible
		callback.onError("onError");
	}
}
