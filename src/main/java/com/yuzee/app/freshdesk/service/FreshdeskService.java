package com.yuzee.app.freshdesk.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import java.io.IOException;
import com.yuzee.app.freshdesk.dto.ConversationResponseDto;
import com.yuzee.app.freshdesk.dto.ConverstionRequestDto;
import com.yuzee.app.freshdesk.dto.TicketDto;
import com.yuzee.app.freshdesk.dto.TicketResponseDto;
import com.yuzee.app.freshdesk.dto.WatcherRequestDto;
import com.yuzee.common.lib.dto.GenericWrapperDto;
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
	
	  private HttpHeaders createHeaders() {
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setBasicAuth(freshdeskApiKey, "X");
	        return headers;
	    }

	public TicketResponseDto createTicket(TicketDto ticketDto) {
		log.info("Creating ticket with details: {}", ticketDto);

		String CREATE_TICKET_URL = freshdeskApiUrl + "/api/v2/tickets";
		ResponseEntity<TicketResponseDto> responseEntity = null;
		HttpHeaders headers = createHeaders();

		try {
			HttpEntity<TicketDto> requestEntity = new HttpEntity<>(ticketDto, headers);
			responseEntity = restTemplate.exchange(CREATE_TICKET_URL, HttpMethod.POST, requestEntity, TicketResponseDto.class);
		} catch (Exception e) {
			log.error("Exception occurred while creating ticket: ", e);
			throw new InternalServerException("Exception occurred while creating ticket", e);
		}

		return responseEntity.getBody();
	}
	
	public TicketResponseDto createTicketWithAttachments(String email, String subject, String description, MultipartFile[] attachments) throws IOException {
        String createTicketUrl = freshdeskApiUrl + "/tickets";
        ResponseEntity<TicketResponseDto> responseEntity = null; 
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBasicAuth(freshdeskApiKey, "X");

        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("email", email);
		bodyMap.add("subject", subject);
		bodyMap.add("description", description);

		for (MultipartFile file : attachments) {
			ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
				@Override
				public String getFilename() {
					return file.getOriginalFilename();
				}
			};
			bodyMap.add("attachments[]", fileAsResource);
		}
		try {

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

			responseEntity = restTemplate.exchange(createTicketUrl, HttpMethod.POST, requestEntity,
					TicketResponseDto.class);
		} catch (Exception e) {
			log.error("Exception occurred while creating tickets: ", e);
			throw new InternalServerException("Exception occurred while creating tickets", e);
		}
		return responseEntity.getBody();
	}
	
	public List<TicketResponseDto> getAllTickets(Map<String, String> filters) {
		log.info("Fetching tickets with filters: {}", filters);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(freshdeskApiUrl + "/api/v2/tickets");
		filters.forEach(builder::queryParam);

		String url = builder.toUriString();
		log.info("URL: {}", url);

		ResponseEntity<GenericWrapperDto<List<TicketResponseDto>>> responseEntity;
		HttpHeaders headers = createHeaders();

		try {
			HttpEntity<String> requestEntity = new HttpEntity<>(headers);
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<GenericWrapperDto<List<TicketResponseDto>>>() {
			});
		} catch (Exception e) {
			log.error("Exception occurred while fetching tickets: ", e);
			throw new InternalServerException("Exception occurred while fetching tickets", e);
		}

		return responseEntity.getBody().getData();
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
	        HttpHeaders headers = createHeaders();
	        try {
	            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
	            responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, TicketResponseDto.class);
	        } catch (Exception e) {
	            log.error("Exception occurred while fetching ticket: ", e);
	            throw new InternalServerException("Exception occurred while fetching ticket", e);
	        }

	        return responseEntity.getBody();
	    }
	  
		public void deleteTicketById(Long id) {
			log.info("Deleting ticket with id: {}", id);

			String DELETE_TICKET_URL = freshdeskApiUrl + "/api/v2/tickets/" + id;

			HttpHeaders headers = createHeaders();

			try {
				HttpEntity<String> requestEntity = new HttpEntity<>(headers);
				ResponseEntity<String> responseEntity = restTemplate.exchange(DELETE_TICKET_URL, HttpMethod.DELETE,
						requestEntity, String.class);

				if (responseEntity.getStatusCode().is2xxSuccessful()) {
					log.info("Ticket with id: {} deleted successfully", id);
				} else {
					throw new InternalServerException("Failed to delete ticket");
				}
			} catch (Exception e) {
				log.error("Exception occurred while deleting ticket: ", e);
				throw new InternalServerException("Exception occurred while deleting ticket", e);
			}
		}
		
		public ConversationResponseDto createReply(Long ticketId, ConverstionRequestDto ConverstionRequestDto) {
			log.info("Creating reply for ticket with id: {}", ticketId);

			ResponseEntity<ConversationResponseDto> responseEntity = null;

			String CREATE_REPLY_URL = freshdeskApiUrl + "/api/v2/tickets/" + ticketId + "/reply";

			HttpHeaders headers = createHeaders();

			try {
				HttpEntity<ConverstionRequestDto> requestEntity = new HttpEntity<>(ConverstionRequestDto, headers);
				responseEntity = restTemplate.exchange(CREATE_REPLY_URL, HttpMethod.POST, requestEntity,
						ConversationResponseDto.class);
			} catch (Exception e) {
				log.error("Exception occurred while creating reply: ", e);
				throw new InternalServerException("Exception occurred while creating reply", e);
			}
			return responseEntity.getBody();
		}
		
		public ConversationResponseDto replyToTicketWithAttachments(long ticketId, String body,
				MultipartFile[] attachments) throws IOException {
			String replyTicketUrl = freshdeskApiUrl + "/api/v2/tickets/" + ticketId + "/reply";
			ResponseEntity<ConversationResponseDto> responseEntity = null;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.setBasicAuth(freshdeskApiKey, "X");

			MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
			bodyMap.add("body", body);

			for (MultipartFile file : attachments) {
				ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
					@Override
					public String getFilename() {
						return file.getOriginalFilename();
					}
				};
				bodyMap.add("attachments[]", fileAsResource);
			}

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

			responseEntity = restTemplate.exchange(replyTicketUrl, HttpMethod.POST, requestEntity,
					ConversationResponseDto.class);
			return responseEntity.getBody();
		}
		
		public ConversationResponseDto createNoteOnTicket(long ticketId, ConverstionRequestDto noteDto) {
			String CREATE_NOTE_URL = freshdeskApiUrl + "/tickets/" + ticketId + "/notes";
			HttpHeaders headers = createHeaders();
			ResponseEntity<ConversationResponseDto> responseEntity = null;
			try {
				HttpEntity<ConverstionRequestDto> requestEntity = new HttpEntity<>(noteDto, headers);
				responseEntity = restTemplate.exchange(CREATE_NOTE_URL, HttpMethod.POST, requestEntity,
						ConversationResponseDto.class);
			} catch (Exception e) {
				log.info("Exception occurred while creating note on ticket");
				throw new RuntimeException("Exception occurred while creating note on ticket", e);
			}
			return responseEntity.getBody();
		}
		
		public ConversationResponseDto addNoteAttachmentToTicket(long ticketId, String body, String notifyEmail,
				MultipartFile[] attachments) throws IOException {
			String addNoteUrl = freshdeskApiUrl + "/tickets/" + ticketId + "/notes";
			ResponseEntity<ConversationResponseDto> responseEntity  = null;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.setBasicAuth(freshdeskApiKey, "X");

			MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
			bodyMap.add("body", body);
			bodyMap.add("notify_emails[]", notifyEmail);

			for (MultipartFile file : attachments) {
				ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
					@Override
					public String getFilename() {
						return file.getOriginalFilename();
					}
				};
				bodyMap.add("attachments[]", fileAsResource);
			}
            try {
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

			responseEntity = restTemplate.exchange(addNoteUrl, HttpMethod.POST,
					requestEntity, ConversationResponseDto.class);
            }catch(Exception e) {
            	log.info("Exception occurred while creating note attachment on ticket");
				throw new RuntimeException("Exception occurred while creating note attachment on ticket", e);
            }
			if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
				return responseEntity.getBody();
			} else {
				throw new RuntimeException(
						"Failed to create note with attachments. Status code: " + responseEntity.getStatusCode());
			}
		}
		
		public ConversationResponseDto updateConversation(long conversationId, ConverstionRequestDto conversationRequestDto) {
	        String updateConversationUrl = freshdeskApiUrl + "/conversations/" + conversationId;
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setBasicAuth(freshdeskApiKey, "X");

	        // Create request entity
	        HttpEntity<ConverstionRequestDto> requestEntity = new HttpEntity<>(conversationRequestDto, headers);

	        // Make the API call
	        ResponseEntity<ConversationResponseDto> responseEntity = restTemplate.exchange(updateConversationUrl, HttpMethod.PUT, requestEntity, ConversationResponseDto.class);

	        // Check response status
	        if (responseEntity.getStatusCode() == HttpStatus.OK) {
	            return responseEntity.getBody();
	        } else {
	            throw new RuntimeException("Failed to update conversation. Status code: " + responseEntity.getStatusCode());
	        }
	    }
		
		   public ConversationResponseDto updateConversationAttachment(long conversationId, String body, MultipartFile[] attachments) throws IOException {
		        String updateConversationUrl = freshdeskApiUrl + "/conversations/" + conversationId;
		        
		        HttpHeaders headers = new HttpHeaders();
		        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		        headers.setBasicAuth(freshdeskApiKey, "X");

		        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		        bodyMap.add("body", body);

		        for (MultipartFile file : attachments) {
		            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
		                @Override
		                public String getFilename() {
		                    return file.getOriginalFilename();
		                }
		            };
		            bodyMap.add("attachments[]", fileAsResource);
		        }

		        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		        ResponseEntity<ConversationResponseDto> responseEntity = restTemplate.exchange(updateConversationUrl, HttpMethod.PUT, requestEntity, ConversationResponseDto.class);

		        if (responseEntity.getStatusCode() == HttpStatus.OK) {
		            return responseEntity.getBody();
		        } else {
		            throw new RuntimeException("Failed to update conversation with attachments. Status code: " + responseEntity.getStatusCode());
		        }
		    }
		   
		   public void deleteConversation(long conversationId) {
		        String deleteConversationUrl = freshdeskApiUrl + "/conversations/" + conversationId;

		        HttpHeaders headers = new HttpHeaders();
		        headers.setBasicAuth(freshdeskApiKey, "X");

		        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

		        ResponseEntity<String> responseEntity = restTemplate.exchange(deleteConversationUrl, HttpMethod.DELETE, requestEntity, String.class);

		        if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
		            throw new RuntimeException("Failed to delete conversation. Status code: " + responseEntity.getStatusCode());
		        }
		    }
		   
			public void addWatcherToTicket(long ticketId, WatcherRequestDto watcherRequestDto) {
				String addWatcherUrl = freshdeskApiUrl + "/tickets/" + ticketId + "/watch";

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.setBasicAuth(freshdeskApiKey, "X");

				HttpEntity<WatcherRequestDto> requestEntity = new HttpEntity<>(watcherRequestDto, headers);

				ResponseEntity<String> responseEntity = restTemplate.exchange(addWatcherUrl, HttpMethod.POST,
						requestEntity, String.class);

				if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
					throw new RuntimeException(
							"Failed to add watcher to ticket. Status code: " + responseEntity.getStatusCode());
				}
			}
}
