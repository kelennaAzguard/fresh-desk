package com.yuzee.app.freshdesk.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yuzee.app.freshdesk.model.Attachment;

import lombok.Data;

@Data
public class ConverstionRequestDto {
	@JsonProperty("body")
	private String body;

	@JsonProperty("attachments")
	private List<Attachment> attachments;

	@JsonProperty("from_email")
	private String fromEmail;

	@JsonProperty("user_id")
	private Long userId;

	@JsonProperty("cc_emails")
	private List<String> ccEmails;

	@JsonProperty("bcc_emails")
	private List<String> bccEmails;
	
	@JsonProperty("private")
    private boolean isPrivate;
    
	@JsonProperty("notify_emails")
    private List<String> notifyEmails;
}
