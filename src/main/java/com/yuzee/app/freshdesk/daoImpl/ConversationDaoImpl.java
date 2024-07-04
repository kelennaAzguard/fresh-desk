package com.yuzee.app.freshdesk.daoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.yuzee.app.freshdesk.dao.ConversationDao;
import com.yuzee.app.freshdesk.model.Conversation;
import com.yuzee.app.freshdesk.repository.ConversationRepository;
import org.springframework.stereotype.Service;

@Service
public class ConversationDaoImpl implements ConversationDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private ConversationRepository conversationRepository;

	@Override
	public Conversation saveConversaation(Conversation conversation) {
		// TODO Auto-generated method stub
		return conversationRepository.save(conversation);
	}

	@Override
	public Conversation getConversationById(Long id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		return mongoTemplate.findOne(query, Conversation.class);
	}

}
