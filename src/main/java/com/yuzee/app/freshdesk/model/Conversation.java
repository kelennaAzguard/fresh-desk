package com.yuzee.app.freshdesk.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversation")
public class Conversation {

	@Id
    private Long id;

    private List<Attachment> attachments;

    private String body;

    private String bodyText;


    private Boolean incoming;

    private List<String> toEmails;

    private Boolean isPrivate;

    private Integer source;

    private String supportEmail;

    private Long ticketId;

    private Long userId;

    private LocalDateTime lastEditedAt;

    private Long lastEditedUserId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}