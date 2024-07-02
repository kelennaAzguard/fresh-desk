package com.yuzee.app.freshdesk.processor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yuzee.app.freshdesk.dao.TicketDao;
import com.yuzee.app.freshdesk.dto.TicketDto;
import com.yuzee.app.freshdesk.dto.TicketResponseDto;
import com.yuzee.app.freshdesk.model.Ticket;
import com.yuzee.app.freshdesk.service.FreshdeskService;
import com.yuzee.common.lib.exception.NotFoundException;
import com.yuzee.local.config.MessageTranslator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketProcessor {
	@Autowired
	private TicketDao ticketDao;
	@Autowired
	private MessageTranslator messageTranslator;
	@Autowired
	private FreshdeskService ticketService;

	public TicketResponseDto createTicket(TicketDto ticketDto) {
		log.info("inside create ticket processor");
		Ticket ticketFromDb = new Ticket();
		if (ObjectUtils.isNotEmpty(ticketDto)) {
			// check if email already exist in db ...
			Ticket ticket = ticketDao.getTicketByEmail(ticketDto.getEmail());
			if (ObjectUtils.isNotEmpty(ticket)) {
				log.info("email already exist in db and freshdesk......");
				new NotFoundException(
						messageTranslator.toLocale("email already exist in db and freshdesk", ticketDto.getEmail()));

			}
			TicketResponseDto createticket = ticketService.createTicket(ticketDto);
			if (ObjectUtils.isEmpty(createticket)) {
				log.info("error while creating ticket");
				throw new NotFoundException(messageTranslator.toLocale("error while creating ticket"));
			}
			Ticket ticketFromFreshDeskDto = populateTicketModelFromDto(createticket, ticketDto.getEmail());
			ticketFromDb = ticketDao.createTicket(ticketFromFreshDeskDto);

		}

		return convertToResponseDto(ticketFromDb);
	}
	
	public TicketResponseDto getAllTicket(String filter, Long requesterId, String email, String uniqueExternalId,
			Long companyId, String updatedSince, String orderBy, String orderType, String include) {
		log.info("inside get all ticket controller");
		Map<String, String> filters = new HashMap<>();

		if (filter != null)
			filters.put("filter", filter);
		if (requesterId != null)
			filters.put("requester_id", requesterId.toString());
		if (email != null)
			filters.put("email", email);
		if (uniqueExternalId != null)
			filters.put("unique_external_id", uniqueExternalId);
		if (companyId != null)
			filters.put("company_id", companyId.toString());
		if (updatedSince != null)
			filters.put("updated_since", updatedSince);
		if (orderBy != null)
			filters.put("order_by", orderBy);
		if (orderType != null)
			filters.put("order_type", orderType);
		if (include != null)
			filters.put("include", include);

		TicketResponseDto response = ticketService.getAllTickets(filters);
		;
		return null;
	}

	private Ticket populateTicketModelFromDto(TicketResponseDto ticketDto, String email) {
		Ticket ticket = new Ticket();
		ticket.setName(ticketDto.getSubject());
		ticket.setRequesterId(ticketDto.getRequesterId());
		ticket.setEmail(email);
		ticket.setSubject(ticketDto.getSubject());
		ticket.setType(ticketDto.getType());
		ticket.setStatus(ticketDto.getStatus());
		ticket.setPriority(ticketDto.getPriority());
		ticket.setDescription(ticketDto.getDescription());
		ticket.setResponderId(ticketDto.getResponderId());
		ticket.setAttachments(ticketDto.getAttachments());
		ticket.setCcEmails(ticketDto.getCcEmails());
		ticket.setCompanyId(ticketDto.getCompanyId());
		ticket.setDueBy(ticketDto.getDueBy());
		ticket.setEmailConfigId(ticketDto.getEmailConfigId());
		ticket.setFrDueBy(ticketDto.getFrDueBy());
		ticket.setGroupId(ticketDto.getGroupId());
		ticket.setProductId(ticketDto.getProductId());
		ticket.setSource(ticketDto.getSource());
		ticket.setTags(ticketDto.getTags());
		ticket.setCreatedAt(LocalDateTime.now());
		ticket.setUpdatedAt(LocalDateTime.now());
		ticket.setDescriptionText(ticketDto.getDescription());
		ticket.setDeleted(false);
		ticket.setFrEscalated(ticketDto.getFrEscalated());
		ticket.setFwdEmails(ticketDto.getFwdEmails());
		ticket.setEscalated(ticketDto.getIsEscalated());
		ticket.setReplyCcEmails(ticketDto.getReplyCcEmails());
		ticket.setSpam(ticketDto.getSpam());
		ticket.setToEmails(ticketDto.getToEmails());

		return ticket;
	}

	// Method to convert Ticket entity to TicketResponseDto
	public TicketResponseDto convertToResponseDto(Ticket ticket) {
		TicketResponseDto responseDto = new TicketResponseDto();
		responseDto.setCcEmails(ticket.getCcEmails());
		responseDto.setFwdEmails(ticket.getFwdEmails());
		responseDto.setReplyCcEmails(ticket.getReplyCcEmails());
		responseDto.setEmailConfigId(ticket.getEmailConfigId());
		responseDto.setGroupId(ticket.getGroupId());
		responseDto.setPriority(ticket.getPriority());
		responseDto.setRequesterId(ticket.getRequesterId());
		responseDto.setResponderId(ticket.getResponderId());
		responseDto.setSource(ticket.getSource());
		responseDto.setStatus(ticket.getStatus());
		responseDto.setSubject(ticket.getSubject());
		responseDto.setCompanyId(ticket.getCompanyId());
		responseDto.setId(ticket.getId());
		responseDto.setType(ticket.getType());
		responseDto.setToEmails(ticket.getToEmails());
		responseDto.setProductId(ticket.getProductId());
		responseDto.setFrEscalated(ticket.isFrEscalated());
		responseDto.setSpam(ticket.isSpam());
		responseDto.setIsEscalated(ticket.isEscalated());
		responseDto.setCreatedAt(ticket.getCreatedAt());
		responseDto.setUpdatedAt(ticket.getUpdatedAt());
		responseDto.setDueBy(ticket.getDueBy());
		responseDto.setFrDueBy(ticket.getFrDueBy());
		responseDto.setDescriptionText(ticket.getDescriptionText());
		responseDto.setDescription(ticket.getDescription());
		responseDto.setTags(ticket.getTags());
		responseDto.setAttachments(ticket.getAttachments());

		return responseDto;
	}

}
