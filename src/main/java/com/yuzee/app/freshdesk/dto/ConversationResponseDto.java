package com.yuzee.app.freshdesk.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yuzee.app.freshdesk.model.Attachment;

import lombok.Data;

@Data
public class ConversationResponseDto {

    @JsonProperty("body_text")
    private String bodyText;

    @JsonProperty("body")
    private String body;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("from_email")
    private String fromEmail;

    @JsonProperty("cc_emails")
    private List<String> ccEmails;

    @JsonProperty("bcc_emails")
    private List<String> bccEmails;

    @JsonProperty("ticket_id")
    private Long ticketId;

    @JsonProperty("replied_to")
    private List<String> repliedTo;

    @JsonProperty("attachments")
    private List<Attachment> attachments;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}