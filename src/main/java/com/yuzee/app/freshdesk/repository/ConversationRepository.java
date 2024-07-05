package com.yuzee.app.freshdesk.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.yuzee.app.freshdesk.model.Conversation;
@Repository
public interface ConversationRepository extends MongoRepository<Conversation, Long> {

}
