package com.yuzee.app.freshdesk.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.yuzee.app.freshdesk.dto.TicketDto;
import com.yuzee.common.lib.exception.IOException;

@RequestMapping("/api/v1")
public interface TicketInterface {

	@PostMapping("/create-ticket")
	public ResponseEntity<?> createTicket(@RequestBody TicketDto ticketDto);

	@PostMapping("/create-attachment")
	public ResponseEntity<?> createTicketWithAttachments(@RequestParam("email") String email,
			@RequestParam("subject") String subject, @RequestParam("description") String description,
			@RequestParam("attachments") MultipartFile[] attachments) throws IOException, java.io.IOException;

	@GetMapping("/tickets")
	public ResponseEntity<?> getTickets(@RequestParam(required = false) String filter,
			@RequestParam(required = false) Long requesterId, @RequestParam(required = false) String email,
			@RequestParam(required = false) String uniqueExternalId, @RequestParam(required = false) Long companyId,
			@RequestParam(required = false) String updatedSince, @RequestParam(required = false) String orderBy,
			@RequestParam(required = false) String orderType, @RequestParam(required = false) String include);

}
