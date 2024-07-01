package com.yuzee.app.freshdesk.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

import com.yuzee.app.freshdesk.model.Ticket;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, Long> {

}
