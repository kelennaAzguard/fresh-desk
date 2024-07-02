package com.yuzee.app.freshdesk.service;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.yuzee.app.freshdesk.dto.TicketDto;
import com.yuzee.app.freshdesk.dto.TicketResponseDto;
import com.yuzee.common.lib.exception.InternalServerException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FreshdeskService {

	@Value("${freshdesk.api.url}")
	private String freshdeskApiUrl;

	@Value("${freshdesk.api.key}")
	private String freshdeskApiKey;

	@Autowired
	private RestTemplate restTemplate;

	public TicketResponseDto createTicket(TicketDto ticketDto) {
		log.info("Creating ticket with details: {}", ticketDto);

		String CREATE_TICKET_URL = freshdeskApiUrl + "/api/v2/tickets";
		ResponseEntity<TicketResponseDto> responseEntity = null;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization",
				"Basic " + Base64.getEncoder().encodeToString((freshdeskApiKey + ":X").getBytes()));
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");

		try {
			HttpEntity<TicketDto> requestEntity = new HttpEntity<>(ticketDto, headers);
			responseEntity = restTemplate.exchange(CREATE_TICKET_URL, HttpMethod.POST, requestEntity, TicketResponseDto.class);
		} catch (Exception e) {
			log.error("Exception occurred while creating ticket: ", e);
			throw new InternalServerException("Exception occurred while creating ticket", e);
		}

		return responseEntity.getBody();
	}
	
	public TicketResponseDto getAllTickets(Map<String, String> filters) {
		log.info("Fetching tickets with filters: {}", filters);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(freshdeskApiUrl + "/api/v2/tickets");
		filters.forEach(builder::queryParam);

		String url = builder.toUriString();
		log.info("URL: {}", url);

		ResponseEntity<TicketResponseDto> responseEntity;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization",
				"Basic " + Base64.getEncoder().encodeToString((freshdeskApiKey + ":X").getBytes()));
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");

		try {
			HttpEntity<String> requestEntity = new HttpEntity<>(headers);
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, TicketResponseDto.class);
		} catch (Exception e) {
			log.error("Exception occurred while fetching tickets: ", e);
			throw new InternalServerException("Exception occurred while fetching tickets", e);
		}

		return responseEntity.getBody();
	}
	
	  public TicketResponseDto getTicketById(Long id, String include) {
	        log.info("Fetching ticket with id: {} and include: {}", id, include);

	        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(freshdeskApiUrl + "/api/v2/tickets/" + id);
	        if (!ObjectUtils.isEmpty(include)) {
	            builder.queryParam("include", include);
	        }

	        String url = builder.toUriString();
	        log.info("URL: {}", url);

	        ResponseEntity<TicketResponseDto> responseEntity;
	        HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization",
					"Basic " + Base64.getEncoder().encodeToString((freshdeskApiKey + ":X").getBytes()));
			headers.add("Accept", "application/json");
			headers.add("Content-Type", "application/json");

	        try {
	            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
	            responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, TicketResponseDto.class);
	        } catch (Exception e) {
	            log.error("Exception occurred while fetching ticket: ", e);
	            throw new InternalServerException("Exception occurred while fetching ticket", e);
	        }

	        return responseEntity.getBody();
	    }
}
