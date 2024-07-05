package com.yuzee.app.freshdesk.processor;

import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yuzee.app.freshdesk.dao.TicketDao;
import com.yuzee.app.freshdesk.dao.WatcherDao;
import com.yuzee.app.freshdesk.dto.WatcherRequestDto;
import com.yuzee.app.freshdesk.model.Ticket;
import com.yuzee.app.freshdesk.model.Watcher;
import com.yuzee.app.freshdesk.service.FreshdeskService;
import com.yuzee.common.lib.exception.NotFoundException;
import com.yuzee.local.config.MessageTranslator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WatcherProcessor {
	@Autowired
	private WatcherDao watcherDao;
	@Autowired
	private TicketDao ticketDao;
	@Autowired
	private MessageTranslator messageTranslator;
	@Autowired
	private FreshdeskService ticketService;

	public void createWatcher(long ticketId, WatcherRequestDto watcherRequestDto) {
		log.info("inside create watcher");

		log.info("checking if ticket exist in db");
		Ticket ticket = ticketDao.getTicketById(ticketId);
		if (ObjectUtils.isEmpty(ticket)) {
			log.info("ticket id is empty in db and freshdesk......");
			new NotFoundException(messageTranslator.toLocale("ticket id is empty in db and freshdesk"));

		}
		// if exist then proceed ... by calling watcher api ...
		ticketService.addWatcherToTicket(ticketId, watcherRequestDto);
		log.info("proceed to save watcher to  db");
		// Save watcher to local database
		Watcher watcher = new Watcher();
		watcher.setId(UUID.randomUUID().toString());
		watcher.setUserId(watcherRequestDto.getUserId());
		watcher.setTicketIds(Arrays.asList(ticketId));
		watcherDao.saveWatcher(watcher);

	}

}
