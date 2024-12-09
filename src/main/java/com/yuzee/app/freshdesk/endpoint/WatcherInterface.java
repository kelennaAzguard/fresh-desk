package com.yuzee.app.freshdesk.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yuzee.app.freshdesk.dto.WatcherRequestDto;

@RequestMapping("/api/tickets")
public interface WatcherInterface {

	@PostMapping("/{ticketId}/watch")
	public ResponseEntity<?> addWatcherToTicket(@PathVariable long ticketId, @RequestBody WatcherRequestDto watcherRequestDto);

}
