package com.yuzee.app.freshdesk.dao;

import com.yuzee.app.freshdesk.model.Watcher;

public interface WatcherDao {
	public Watcher saveWatcher(Watcher watcher);

	public Watcher getWatcherById(String Id);

}
