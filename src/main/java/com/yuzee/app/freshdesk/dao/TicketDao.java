package com.yuzee.app.freshdesk.dao;

import com.yuzee.app.freshdesk.model.Ticket;

public interface TicketDao {

	public Ticket createTicket(Ticket ticket);
	
	public Ticket getTicketByEmail(String email);

}
