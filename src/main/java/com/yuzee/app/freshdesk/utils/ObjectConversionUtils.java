package com.yuzee.app.freshdesk.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;

import com.yuzee.app.freshdesk.dto.ConversationResponseDto;
import com.yuzee.app.freshdesk.model.Attachment;
import com.yuzee.app.freshdesk.model.Conversation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectConversionUtils {

	public static Conversation convertToConversation(ConversationResponseDto dto) {
		Conversation conversation = new Conversation();
		conversation.setId(dto.getId());
		conversation.setBody(dto.getBody());
		conversation.setBodyText(dto.getBodyText());
		conversation.setTicketId(dto.getTicketId());
		conversation.setUserId(dto.getUserId());
		conversation.setCreatedAt(dto.getCreatedAt());
		conversation.setUpdatedAt(dto.getUpdatedAt());

		// Convert attachments directly within this method
		if (ObjectUtils.isNotEmpty(dto.getAttachments())) {
			List<Attachment> attachments = convertToAttachment(dto.getAttachments());
			conversation.setAttachments(attachments);
		}
		return conversation;
	}

	private static List<Attachment> convertToAttachment(List<Attachment> dtos) {
		return dtos.stream().map(dto -> {
			Attachment attachment = new Attachment();
			attachment.setId(dto.getId());
			attachment.setContentType(dto.getContentType());
			attachment.setFileSize(dto.getFileSize());
			attachment.setName(dto.getName());
			attachment.setAttachmentUrl(dto.getAttachmentUrl());
			attachment.setCreatedAt(dto.getCreatedAt());
			attachment.setUpdatedAt(dto.getUpdatedAt());
			return attachment;
		}).collect(Collectors.toList());
	}
}
