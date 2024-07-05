package com.yuzee.app.freshdesk.daoImpl;

import com.yuzee.app.freshdesk.dao.WatcherDao;
import com.yuzee.app.freshdesk.model.Watcher;
import com.yuzee.app.freshdesk.repository.WatcherRepository;

public class WatcherDaoImpl implements WatcherDao {
	
	private WatcherRepository watcherRepository;

	@Override
	public Watcher saveWatcher(Watcher watcher) {
		// TODO Auto-generated method stub
		return watcherRepository.save(watcher);
	}

	@Override
	public Watcher getWatcherById(String Id) {
		// TODO Auto-generated method stub
		return watcherRepository.findById(Id).orElse(null);
	}

}
