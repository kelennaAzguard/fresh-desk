package com.yuzee.app.freshdesk.dto;

import java.util.List;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yuzee.app.freshdesk.model.Attachment;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TicketDto {
	@JsonProperty("name")
	private String name;

	@JsonProperty("requester_id")
	private Long requesterId;

	@JsonProperty("email")
	private String email;

	@JsonProperty("facebook_id")
	private String facebookId;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("twitter_id")
	private String twitterId;

	@JsonProperty("unique_external_id")
	private String uniqueExternalId;

	@JsonProperty("subject")
	private String subject;

	@JsonProperty("type")
	private String type;

	@JsonProperty("status")
	private Integer status;

	@JsonProperty("priority")
	private Integer priority;

	@JsonProperty("description")
	private String description;

	@JsonProperty("responder_id")
	private Long responderId;

	@JsonProperty("attachments")
	private List<Attachment> attachments;

	@JsonProperty("cc_emails")
	private List<String> ccEmails;

	@JsonProperty("custom_fields")
	private Map<String, Object> customFields;

	@JsonProperty("due_by")
	private String dueBy;

	@JsonProperty("email_config_id")
	private String emailConfigId;

	@JsonProperty("fr_due_by")
	private String frDueBy;

	@JsonProperty("group_id")
	private Long groupId;

	@JsonProperty("parent_id")
	private Long parentId;

	@JsonProperty("product_id")
	private Long productId;

	@JsonProperty("source")
	private Integer source;

	@JsonProperty("tags")
	private List<String> tags;

	@JsonProperty("company_id")
	private Long companyId;

	@JsonProperty("internal_agent_id")
	private Integer internalAgentId;

	@JsonProperty("internal_group_id")
	private Integer internalGroupId;

	@JsonProperty("lookup_parameter")
	private String lookupParameter;

}
