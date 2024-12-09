package com.yuzee.app.freshdesk.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WatcherRequestDto {
	@JsonProperty("user_id")
	private long userId;
	@JsonProperty("ids")
	private List<Long> ticketIds;
}
