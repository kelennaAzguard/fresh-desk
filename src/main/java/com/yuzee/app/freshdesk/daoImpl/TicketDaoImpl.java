package com.yuzee.app.freshdesk.daoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.yuzee.app.freshdesk.dao.TicketDao;
import com.yuzee.app.freshdesk.model.Ticket;
import com.yuzee.app.freshdesk.repository.TicketRepository;

@Service
public class TicketDaoImpl implements TicketDao {
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public Ticket createTicket(Ticket ticket) {
		return ticketRepository.save(ticket);
	}

	@Override
	public Ticket getTicketByEmail(String email) {
		Query query = new Query();
		query.addCriteria(Criteria.where("email").is(email));
		return mongoTemplate.findOne(query, Ticket.class);
	}

	@Override
	public Ticket getTicketById(long id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("ticketId").is(id));
		return mongoTemplate.findOne(query, Ticket.class);
	}

}
