package com.yuzee.app.freshdesk.dao;

import com.yuzee.app.freshdesk.model.Conversation;

public interface ConversationDao {
	
	public Conversation saveConversaation(Conversation conversation);
	
	public Conversation getConversationById(Long id);
	
	public void deleteConversationById(long id);
	
   

}
