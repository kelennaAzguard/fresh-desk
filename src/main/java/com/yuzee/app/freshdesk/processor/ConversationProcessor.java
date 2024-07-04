package com.yuzee.app.freshdesk.processor;

import java.io.IOException;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectTagging;
import com.yuzee.app.freshdesk.dao.ConversationDao;
import com.yuzee.app.freshdesk.dao.TicketDao;
import com.yuzee.app.freshdesk.dto.ConversationResponseDto;
import com.yuzee.app.freshdesk.dto.ConverstionRequestDto;
import com.yuzee.app.freshdesk.model.Conversation;
import com.yuzee.app.freshdesk.model.Ticket;
import com.yuzee.app.freshdesk.service.FreshdeskService;
import com.yuzee.app.freshdesk.utils.ObjectConversionUtils;
import com.yuzee.common.lib.exception.NotFoundException;
import com.yuzee.local.config.MessageTranslator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConversationProcessor {

	@Autowired
	private ConversationDao conversationDao;

	@Autowired
	private MessageTranslator messageTranslator;
	@Autowired
	private FreshdeskService ticketService;
	@Autowired
	private TicketDao ticketDao;

	public ConversationResponseDto createReply(Long id, ConverstionRequestDto replyDto) {
		log.info("inside create reply processor");
		ConversationResponseDto reply = new ConversationResponseDto();
		if (ObjectUtils.isNotEmpty(replyDto)) {
			log.info("checking if ticket exist in db");
			Ticket ticket = ticketDao.getTicketById(id);
			if (ObjectUtils.isEmpty(ticket)) {
				log.info("ticket id is empty in db and freshdesk......");
				new NotFoundException(messageTranslator.toLocale("ticket id is empty in db and freshdesk"));
			}

			reply = ticketService.createReply(id, replyDto);
			if (ObjectUtils.isEmpty(reply)) {
				log.info("error while creating conversation");
				throw new NotFoundException(messageTranslator.toLocale("error while creating conversation"));
			}
			Conversation conversationModel = ObjectConversionUtils.convertToConversation(reply);
			conversationDao.saveConversaation(conversationModel);

		}
		return reply;
	}

	public ConversationResponseDto replyToTicketWithAttachments(long ticketId, String body, MultipartFile[] attachments)
			throws IOException {
		log.info("checking if ticket exist in db");
		Ticket ticket = ticketDao.getTicketById(ticketId);
		if (ObjectUtils.isEmpty(ticket)) {
			log.info("ticket id is empty in db and freshdesk......");
			new NotFoundException(messageTranslator.toLocale("ticket id is empty in db and freshdesk"));
		}
		ConversationResponseDto reply = ticketService.replyToTicketWithAttachments(ticketId, body, attachments);
		if (ObjectUtils.isEmpty(reply)) {
			log.info("error while creating conversation");
			throw new NotFoundException(messageTranslator.toLocale("error while creating conversation"));
		}
		Conversation conversationModel = ObjectConversionUtils.convertToConversation(reply);
		conversationDao.saveConversaation(conversationModel);
		return reply;
	}

	public ConversationResponseDto createNote(long ticketId, ConverstionRequestDto noteDto) {
		log.info("checking if ticket exist in db");
		Ticket ticket = ticketDao.getTicketById(ticketId);
		if (ObjectUtils.isEmpty(ticket)) {
			log.info("ticket id is empty in db and freshdesk......");
			new NotFoundException(messageTranslator.toLocale("ticket id is empty in db and freshdesk"));
		}
		log.info("calling create note external api service");
		ConversationResponseDto note = ticketService.createNoteOnTicket(ticketId, noteDto);
		if (ObjectUtils.isEmpty(note)) {
			log.info("error while creating conversation");
			throw new NotFoundException(messageTranslator.toLocale("error while creating conversation"));
		}
		Conversation conversationModel = ObjectConversionUtils.convertToConversation(note);
		conversationDao.saveConversaation(conversationModel);
		return note;
	}

	public ConversationResponseDto createNoteAttacment(long ticketId, String body, String notifyEmail,
			MultipartFile[] attachments) throws IOException {
		log.info("checking if ticket exist in db");
		Ticket ticket = ticketDao.getTicketById(ticketId);
		if (ObjectUtils.isEmpty(ticket)) {
			log.info("ticket id is empty in db and freshdesk......");
			new NotFoundException(messageTranslator.toLocale("ticket id is empty in db and freshdesk"));
		}
		log.info("calling create note external api service");
		ConversationResponseDto note = ticketService.addNoteAttachmentToTicket(ticketId, body, notifyEmail,
				attachments);
		if (ObjectUtils.isEmpty(note)) {
			log.info("error while creating note conversation");
			throw new NotFoundException(messageTranslator.toLocale("error while creating note conversation"));
		}
		Conversation conversationModel = ObjectConversionUtils.convertToConversation(note);
		conversationDao.saveConversaation(conversationModel);
		return note;
	}

}
