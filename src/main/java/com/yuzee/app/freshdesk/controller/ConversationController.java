package com.yuzee.app.freshdesk.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yuzee.app.freshdesk.dto.ConversationResponseDto;
import com.yuzee.app.freshdesk.dto.ConverstionRequestDto;
import com.yuzee.app.freshdesk.endpoint.ConversationInterface;
import com.yuzee.app.freshdesk.processor.ConversationProcessor;
import com.yuzee.common.lib.handler.GenericResponseHandlers;

@RestController
public class ConversationController  implements ConversationInterface{
	@Autowired
	private ConversationProcessor conversationProcessor;

	@Override
	public ResponseEntity<?> createReply(Long id, ConverstionRequestDto replyDto) {
		ConversationResponseDto createReply = conversationProcessor.createReply(id,replyDto);
		return new GenericResponseHandlers.Builder().setMessage("create reply")
				.setStatus(HttpStatus.OK).setData(createReply).create();
	}

	@Override
	public ResponseEntity<?> replyToTicketWithAttachments(long ticketId, String body,
			MultipartFile[] attachments) throws IOException {
		ConversationResponseDto createReplyAttachment = conversationProcessor.replyToTicketWithAttachments(ticketId,body,attachments);
		return new GenericResponseHandlers.Builder().setMessage("create attachment reply successful")
				.setStatus(HttpStatus.OK).setData(createReplyAttachment).create();
	}

	@Override
	public ResponseEntity<?> addNoteToTicket(long ticketId, ConverstionRequestDto noteDto) {
		ConversationResponseDto createReply = conversationProcessor.createNote(ticketId,noteDto);
		return new GenericResponseHandlers.Builder().setMessage("create note successfull")
				.setStatus(HttpStatus.OK).setData(createReply).create();
	}

	@Override
	public ResponseEntity<?> addNoteAttachmentToTicket(long ticketId, String body, String notifyEmail,
			MultipartFile[] attachments) throws IOException {
		ConversationResponseDto createReply = conversationProcessor.createNoteAttacment(ticketId,body,notifyEmail,attachments);
		return new GenericResponseHandlers.Builder().setMessage("create note attachment successfull")
				.setStatus(HttpStatus.OK).setData(createReply).create();
	}

}
