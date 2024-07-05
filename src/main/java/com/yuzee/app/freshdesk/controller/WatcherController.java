package com.yuzee.app.freshdesk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.yuzee.app.freshdesk.dto.TicketResponseDto;
import com.yuzee.app.freshdesk.dto.WatcherRequestDto;
import com.yuzee.app.freshdesk.endpoint.WatcherInterface;
import com.yuzee.app.freshdesk.processor.WatcherProcessor;
import com.yuzee.common.lib.handler.GenericResponseHandlers;
import com.yuzee.local.config.MessageTranslator;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class WatcherController implements WatcherInterface{
	
	@Autowired
	private WatcherProcessor watcherProcessor;
	@Autowired
	private MessageTranslator messageTranslator;

	@Override
	public ResponseEntity<?> addWatcherToTicket(long ticketId, WatcherRequestDto watcherRequestDto) {
		log.info("inside addWatchercontroller");
		watcherProcessor.createWatcher(ticketId,watcherRequestDto);
		return new GenericResponseHandlers.Builder()
				.setMessage(messageTranslator.toLocale("watcher.created.successufully")).setStatus(HttpStatus.OK)
				.create();
		
	}

}
