package com.yuzee.app.freshdesk.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.yuzee.app.freshdesk.dto.TicketDto;
import com.yuzee.app.freshdesk.dto.TicketResponseDto;
import com.yuzee.app.freshdesk.endpoint.TicketInterface;
import com.yuzee.app.freshdesk.model.Attachment;
import com.yuzee.app.freshdesk.processor.TicketProcessor;
import com.yuzee.common.lib.exception.IOException;
import com.yuzee.common.lib.handler.GenericResponseHandlers;
import com.yuzee.local.config.MessageTranslator;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@NoArgsConstructor
public class TicketController implements TicketInterface {

	@Autowired
	private TicketProcessor ticketProcessor;
	@Autowired
	private MessageTranslator messageTranslator;

	@Override
	public ResponseEntity<?> createTicket(TicketDto ticketDto) {
		// TODO Auto-generated method stub
		log.info("inside create ticket controller");
		TicketResponseDto createTicketDto = ticketProcessor.createTicket(ticketDto);
		return new GenericResponseHandlers.Builder()
				.setMessage(messageTranslator.toLocale("ticket.created.successufully")).setStatus(HttpStatus.OK)
				.setData(createTicketDto).create();
	}

	@Override
	public ResponseEntity<?> createTicketWithAttachments(String email, String subject, String description,
			MultipartFile[] multipartFiles) throws IOException, java.io.IOException {
		log.info("inside createTicketWithAttachments controller");
		log.info("Creating ticket with email: {}, subject: {}, description: {}", email, subject, description);

		log.info("inside group dialog controller");
		TicketResponseDto createTicketDto = ticketProcessor.createTicketAttachments(email, subject, description,
				multipartFiles);
		return new GenericResponseHandlers.Builder()
				.setMessage(messageTranslator.toLocale("createTicketWithAttachments.successufully")).setStatus(HttpStatus.OK)
				.setData(createTicketDto).create();
	}

	@Override
	public ResponseEntity<?> getTickets(String filter, Long requesterId, String email, String uniqueExternalId,
			Long companyId, String updatedSince, String orderBy, String orderType, String include) {
		// TODO Auto-generated method stub
		TicketResponseDto createdTicket = ticketProcessor.getAllTicket(filter, requesterId, email, uniqueExternalId,
				companyId, updatedSince, orderBy, orderType, include);
		return new GenericResponseHandlers.Builder().setMessage("get all ticket")
				.setStatus(HttpStatus.OK).setData(createdTicket).create();
	}

	@Override
	public ResponseEntity<?> getTicketById(Long id, String include) {
		TicketResponseDto getTicket = ticketProcessor.getTicketById(id,include);
		return new GenericResponseHandlers.Builder().setMessage("get ticket by id")
				.setStatus(HttpStatus.OK).setData(getTicket).create();
	}

	@Override
	public ResponseEntity<?> deleteTicketById(Long id) {
	   ticketProcessor.deleteTicketById(id);
		return new GenericResponseHandlers.Builder().setMessage("delete ticket by id")
				.setStatus(HttpStatus.OK).create();
	}

}
