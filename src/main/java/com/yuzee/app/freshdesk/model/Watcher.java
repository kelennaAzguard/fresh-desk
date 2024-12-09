package com.yuzee.app.freshdesk.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "watcher")
public class Watcher {
	@Id
	private String id;
	private List<Long> ticketIds;
	private Long userId;
	private List<Long> watcherIds;
}
