package com.yuzee.app.freshdesk.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import 
org.springframework.data.annotation.Id;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ticket")
public class Ticket {
	@Id
	private String id;
	private Long ticketId;
	private String name;
	private Long requesterId;
	private String email;
	private String facebookId;
	private String phone;
	private String twitterId;
	private String subject;
	private String type;
	private int status;
	private int priority;
	private String description;
	private String descriptionText;
	private Long responderId;
	private List<Attachment> attachments;
	private List<String> ccEmails;
	private Long companyId;
	private Map<String, Object> customFields;
	private boolean deleted;
	private LocalDateTime dueBy;
	private Long emailConfigId;
	private LocalDateTime frDueBy;
	private boolean frEscalated;
	private List<String> fwdEmails;
	private Long groupId;
	private boolean escalated;
	private Long productId;
	private List<String> replyCcEmails;
	private int source;
	private boolean spam;
	private List<String> tags;
	private List<String> toEmails;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
}