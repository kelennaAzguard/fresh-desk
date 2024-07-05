package com.yuzee.app.freshdesk.endpoint;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.yuzee.app.freshdesk.dto.ConversationResponseDto;
import com.yuzee.app.freshdesk.dto.ConverstionRequestDto;

@RequestMapping("api/v1")
public interface ConversationInterface {

	@PostMapping("/tickets/{id}/reply")
	public ResponseEntity<?> createReply(@PathVariable Long id, @RequestBody ConverstionRequestDto replyDto);

	@PostMapping("/tickets/{ticketId}/reply-attachment")
	public ResponseEntity<?> replyToTicketWithAttachments(@PathVariable("ticketId") long ticketId,
			@RequestParam("body") String body, @RequestParam("attachments") MultipartFile[] attachments) throws IOException;

	@PostMapping("tickets/{ticketId}/notes")
	public ResponseEntity<?> addNoteToTicket(@PathVariable long ticketId, @RequestBody ConverstionRequestDto noteDto);
	
	@PostMapping("tickets/{ticketId}/notes-attachment")
	public ResponseEntity<?> addNoteAttachmentToTicket(@PathVariable long ticketId, @RequestParam("body") String body,
			@RequestParam("notify_emails[]") String notifyEmail,
			@RequestParam("attachments[]") MultipartFile[] attachments) throws IOException;

	@PutMapping("/{conversationId}")
	public ResponseEntity<?> updateConversation(@PathVariable long conversationId,
			@RequestBody ConverstionRequestDto conversationRequestDto);

	@PutMapping("conversation-attachment/{conversationId}")
	public ResponseEntity<?> updateConversationAttachment(@PathVariable long conversationId,
			@RequestParam("body") String body, @RequestParam("attachments") MultipartFile[] attachments) throws IOException;
	
	@DeleteMapping("delete/{conversationId}")
    public ResponseEntity<?> deleteConversation(@PathVariable long conversationId);
}
