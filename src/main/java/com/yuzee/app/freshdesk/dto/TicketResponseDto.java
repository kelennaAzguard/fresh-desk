package com.yuzee.app.freshdesk.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yuzee.app.freshdesk.model.Attachment;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TicketResponseDto {
	@JsonProperty("cc_emails")
	private List<String> ccEmails;

	@JsonProperty("fwd_emails")
	private List<String> fwdEmails;

	@JsonProperty("reply_cc_emails")
	private List<String> replyCcEmails;

	@JsonProperty("email_config_id")
	private Long emailConfigId;

	@JsonProperty("group_id")
	private Long groupId;

	@JsonProperty("priority")
	private Integer priority;

	@JsonProperty("requester_id")
	private Long requesterId;

	@JsonProperty("responder_id")
	private Long responderId;

	@JsonProperty("source")
	private Integer source;

	@JsonProperty("status")
	private Integer status;

	@JsonProperty("subject")
	private String subject;

	@JsonProperty("company_id")
	private Long companyId;

	@JsonProperty("id")
	private Long id;

	@JsonProperty("type")
	private String type;

	@JsonProperty("to_emails")
	private List<String> toEmails;

	@JsonProperty("product_id")
	private Long productId;

	@JsonProperty("fr_escalated")
	private Boolean frEscalated;

	@JsonProperty("spam")
	private Boolean spam;

	@JsonProperty("urgent")
	private Boolean urgent;

	@JsonProperty("is_escalated")
	private Boolean isEscalated;

	@JsonProperty("created_at")
	private LocalDateTime createdAt;

	@JsonProperty("updated_at")
	private LocalDateTime updatedAt;

	@JsonProperty("due_by")
	private LocalDateTime dueBy;

	@JsonProperty("fr_due_by")
	private LocalDateTime frDueBy;

	@JsonProperty("description_text")
	private String descriptionText;

	@JsonProperty("description")
	private String description;

	@JsonProperty("tags")
	private List<String> tags;

	@JsonProperty("attachments")
	private List<Attachment> attachments;
}
