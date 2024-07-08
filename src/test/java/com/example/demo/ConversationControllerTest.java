package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyVararg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.yuzee.app.freshdesk.FreshDeskServiceApplication;
import com.yuzee.app.freshdesk.dao.ConversationDao;
import com.yuzee.app.freshdesk.dao.TicketDao;
import com.yuzee.app.freshdesk.dto.ConversationResponseDto;
import com.yuzee.app.freshdesk.dto.ConverstionRequestDto;
import com.yuzee.app.freshdesk.model.Attachment;
import com.yuzee.app.freshdesk.model.Conversation;
import com.yuzee.app.freshdesk.model.Ticket;
import com.yuzee.app.freshdesk.service.FreshdeskService;
import com.yuzee.common.lib.dto.GenericWrapperDto;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = FreshDeskServiceApplication.class)
@TestInstance(Lifecycle.PER_CLASS)
class ConversationControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private FreshdeskService freshdeskService;

	@MockBean
	private ConversationDao conversationDao;

	@MockBean
	private TicketDao ticketDao;

	private String baseUrl = "/api/v1";

	@BeforeEach
	void setUp() {
	}

	private ConverstionRequestDto createSampleConversationRequestDto() {
		ConverstionRequestDto dto = new ConverstionRequestDto();
		dto.setBody("This is a reply.");
		return dto;
	}

	private Ticket createSampleTicket() {
		Ticket ticket = new Ticket();
		ticket.setId("12345");
		return ticket;
	}

	@Test
	void testCreateReplySuccess() {
		Long ticketId = 12345L;
		ConverstionRequestDto replyDto = createSampleConversationRequestDto();
		ConversationResponseDto mockResponse = populateRsponse();
		mockResponse.setBody(replyDto.getBody());

		// Mocking ticketDao.getTicketById
		when(ticketDao.getTicketById(ticketId)).thenReturn(createSampleTicket());

		// Mocking freshdeskService.createReply to match any long and
		// ConverstionRequestDto parameters
		when(freshdeskService.createReply(anyLong(), any(ConverstionRequestDto.class))).thenReturn(mockResponse);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<ConverstionRequestDto> entity = new HttpEntity<>(replyDto, headers);

		ResponseEntity<GenericWrapperDto<ConversationResponseDto>> response = restTemplate.exchange(
				baseUrl + "/tickets/" + ticketId + "/reply", HttpMethod.POST, entity,
				new ParameterizedTypeReference<GenericWrapperDto<ConversationResponseDto>>() {
				});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getData().getBody()).isEqualTo(replyDto.getBody());
	}

	@Test
	void testCreateReplyFailure() {
		Long ticketId = 12345L;
		ConverstionRequestDto replyDto = createSampleConversationRequestDto();

		// Mocking ticketDao.getTicketById
		when(ticketDao.getTicketById(ticketId)).thenReturn(createSampleTicket());

		when(freshdeskService.createReply(ticketId, replyDto)).thenReturn(null);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<ConverstionRequestDto> entity = new HttpEntity<>(replyDto, headers);

		ResponseEntity<GenericWrapperDto<ConversationResponseDto>> response = restTemplate.exchange(
				baseUrl + "/tickets/" + ticketId + "/reply", HttpMethod.POST, entity,
				new ParameterizedTypeReference<GenericWrapperDto<ConversationResponseDto>>() {
				});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		
	}

	@Test
	void testCreateReplyFailureForNullTicketId() {
		Long ticketId = 12345L;
		ConverstionRequestDto replyDto = createSampleConversationRequestDto();

		// Mocking ticketDao.getTicketById
		when(ticketDao.getTicketById(ticketId)).thenReturn(null);

		when(freshdeskService.createReply(ticketId, replyDto)).thenThrow(new RuntimeException("Error creating reply"));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<ConverstionRequestDto> entity = new HttpEntity<>(replyDto, headers);

		ResponseEntity<GenericWrapperDto<ConversationResponseDto>> response = restTemplate.exchange(
				baseUrl + "/tickets/" + ticketId + "/reply", HttpMethod.POST, entity,
				new ParameterizedTypeReference<GenericWrapperDto<ConversationResponseDto>>() {
				});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody().getMessage()).isEqualTo("Error creating reply");
	}

	@Test
	void testReplyToTicketWithAttachmentsSuccess() throws IOException {
		Long ticketId = 12345L;
		MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.txt", "text/plain",
				"test file 1".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("attachments", "file2.txt", "text/plain",
				"test file 2".getBytes());

		ConversationResponseDto mockResponse = populateRsponse();
		mockResponse.setBody("This is a reply with attachments.");

		// Mocking ticketDao.getTicketById
		when(ticketDao.getTicketById(ticketId)).thenReturn(createSampleTicket());

		when(freshdeskService.replyToTicketWithAttachments(anyLong(), anyString(), any(MultipartFile[].class)))
				.thenReturn(mockResponse);

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("body", "This is a reply with attachments.");
		params.add("attachments", file1.getResource());
		params.add("attachments", file2.getResource());

		ResponseEntity<ConversationResponseDto> response = restTemplate.postForEntity(
				baseUrl + "/tickets/" + ticketId + "/reply-attachment", params, ConversationResponseDto.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	void testReplyToTicketWithAttachmentsFailure() throws IOException {
		Long ticketId = 12345L;
		MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.txt", "text/plain",
				"test file 1".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("attachments", "file2.txt", "text/plain",
				"test file 2".getBytes());

		// Mocking ticketDao.getTicketById
		when(ticketDao.getTicketById(ticketId)).thenReturn(createSampleTicket());

		when(freshdeskService.replyToTicketWithAttachments(ticketId, "This is a reply with attachments.",
				new MultipartFile[] { file1, file2 }))
				.thenThrow(new RuntimeException("Error creating reply with attachments"));

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("body", "This is a reply with attachments.");
		params.add("attachments", file1.getResource());
		params.add("attachments", file2.getResource());

		ResponseEntity<ConversationResponseDto> response = restTemplate.postForEntity(
				baseUrl + "/tickets/" + ticketId + "/reply-attachment", params, ConversationResponseDto.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Test
	void testReplyToTicketWithAttachmentsFailureNullTicketId() throws IOException {
		Long ticketId = 12345L;
		MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.txt", "text/plain",
				"test file 1".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("attachments", "file2.txt", "text/plain",
				"test file 2".getBytes());

		// Mocking ticketDao.getTicketById
		when(ticketDao.getTicketById(ticketId)).thenReturn(null);

		when(freshdeskService.replyToTicketWithAttachments(ticketId, "This is a reply with attachments.",
				new MultipartFile[] { file1, file2 }))
				.thenThrow(new RuntimeException("Error creating reply with attachments"));

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("body", "This is a reply with attachments.");
		params.add("attachments", file1.getResource());
		params.add("attachments", file2.getResource());

		ResponseEntity<ConversationResponseDto> response = restTemplate.postForEntity(
				baseUrl + "/tickets/" + ticketId + "/reply-attachment", params, ConversationResponseDto.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Test
	void testAddNoteToTicket() {
		Long ticketId = 12345L;
		ConverstionRequestDto noteDto = createSampleConversationRequestDto();
		ConversationResponseDto mockResponse = populateRsponse();
		mockResponse.setBody(noteDto.getBody());

		when(ticketDao.getTicketById(ticketId)).thenReturn(createSampleTicket());
		when(freshdeskService.createNoteOnTicket(anyLong(), any(ConverstionRequestDto.class))).thenReturn(mockResponse);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<ConverstionRequestDto> entity = new HttpEntity<>(noteDto, headers);

		ResponseEntity<GenericWrapperDto<ConversationResponseDto>> response = restTemplate.exchange(
				baseUrl + "/tickets/" + ticketId + "/notes", HttpMethod.POST, entity,
				new ParameterizedTypeReference<GenericWrapperDto<ConversationResponseDto>>() {
				});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getData().getBody()).isEqualTo(noteDto.getBody());
	}

	@Test
	void testAddNoteToTicketWithINvalidTicketId() {
		Long ticketId = 12345L;
		ConverstionRequestDto noteDto = createSampleConversationRequestDto();
		ConversationResponseDto mockResponse = populateRsponse();
		mockResponse.setBody(noteDto.getBody());

		when(ticketDao.getTicketById(ticketId)).thenReturn(null);
		when(freshdeskService.createNoteOnTicket(anyLong(), any(ConverstionRequestDto.class))).thenReturn(mockResponse);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<ConverstionRequestDto> entity = new HttpEntity<>(noteDto, headers);

		ResponseEntity<GenericWrapperDto<ConversationResponseDto>> response = restTemplate.exchange(
				baseUrl + "/tickets/" + ticketId + "/notes", HttpMethod.POST, entity,
				new ParameterizedTypeReference<GenericWrapperDto<ConversationResponseDto>>() {
				});

		assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);

	}

	@Test
	void testAddNoteToTicketWithEmptyService() {
		Long ticketId = 12345L;
		ConverstionRequestDto noteDto = createSampleConversationRequestDto();
		ConversationResponseDto mockResponse = populateRsponse();
		mockResponse.setBody(noteDto.getBody());

		when(ticketDao.getTicketById(ticketId)).thenReturn(createSampleTicket());
		when(freshdeskService.createNoteOnTicket(anyLong(), any(ConverstionRequestDto.class))).thenReturn(null);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<ConverstionRequestDto> entity = new HttpEntity<>(noteDto, headers);

		ResponseEntity<GenericWrapperDto<ConversationResponseDto>> response = restTemplate.exchange(
				baseUrl + "/tickets/" + ticketId + "/notes", HttpMethod.POST, entity,
				new ParameterizedTypeReference<GenericWrapperDto<ConversationResponseDto>>() {
				});

		assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);
	}

	@Test
	void testAddNoteAttachmentToTicket() throws IOException {
		Long ticketId = 12345L;
		MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.txt", "text/plain",
				"test file 1".getBytes());

		ConversationResponseDto mockResponse = populateRsponse();
		mockResponse.setBody("This is a note with attachments.");

		when(ticketDao.getTicketById(ticketId)).thenReturn(createSampleTicket());
		when(freshdeskService.addNoteAttachmentToTicket(anyLong(), anyString(), anyString(),
				any(MultipartFile[].class))).thenReturn(mockResponse);

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("body", "This is a note with attachments.");
		params.add("notify_emails[]", "notify@example.com");
		params.add("attachments[]", file1.getResource());

		ResponseEntity<ConversationResponseDto> response = restTemplate.postForEntity(
				baseUrl + "/tickets/" + ticketId + "/notes-attachment", params, ConversationResponseDto.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	void testAddNoteAttachmentToTicketFailure() throws IOException {
		Long ticketId = 12345L;
		MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.txt", "text/plain",
				"test file 1".getBytes());

		ConversationResponseDto mockResponse = populateRsponse();
		mockResponse.setBody("This is a note with attachments.");

		when(ticketDao.getTicketById(ticketId)).thenReturn(createSampleTicket());
		when(freshdeskService.addNoteAttachmentToTicket(anyLong(), anyString(), anyString(),
				any(MultipartFile[].class))).thenReturn(null);

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("body", "This is a note with attachments.");
		params.add("notify_emails[]", "notify@example.com");
		params.add("attachments[]", file1.getResource());

		ResponseEntity<ConversationResponseDto> response = restTemplate.postForEntity(
				baseUrl + "/tickets/" + ticketId + "/notes-attachment", params, ConversationResponseDto.class);

		assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);
	}

	@Test
	void testAddNoteAttachmentWithEmptyTicketId() throws IOException {
		Long ticketId = 12345L;
		MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.txt", "text/plain",
				"test file 1".getBytes());

		ConversationResponseDto mockResponse = populateRsponse();
		mockResponse.setBody("This is a note with attachments.");

		when(ticketDao.getTicketById(ticketId)).thenReturn(null);
		when(freshdeskService.addNoteAttachmentToTicket(anyLong(), anyString(), anyString(),
				any(MultipartFile[].class))).thenReturn(mockResponse);

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("body", "This is a note with attachments.");
		params.add("notify_emails[]", "notify@example.com");
		params.add("attachments[]", file1.getResource());

		ResponseEntity<ConversationResponseDto> response = restTemplate.postForEntity(
				baseUrl + "/tickets/" + ticketId + "/notes-attachment", params, ConversationResponseDto.class);

		assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);
	}

	@Test
	void testUpdateConversation() {
		Long conversationId = 1L;
		ConverstionRequestDto updateDto = createSampleConversationRequestDto();
		updateDto.setBody("Updated body");

		ConversationResponseDto mockResponse = populateRsponse();
		mockResponse.setBody(updateDto.getBody());
		when(conversationDao.getConversationById(anyLong())).thenReturn(createSampleConversation());
		when(freshdeskService.updateConversation(anyLong(), any(ConverstionRequestDto.class))).thenReturn(mockResponse);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<ConverstionRequestDto> entity = new HttpEntity<>(updateDto, headers);

		ResponseEntity<GenericWrapperDto<ConversationResponseDto>> response = restTemplate.exchange(
				baseUrl + "/" + conversationId, HttpMethod.PUT, entity,
				new ParameterizedTypeReference<GenericWrapperDto<ConversationResponseDto>>() {
				});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getData().getBody()).isEqualTo(updateDto.getBody());
	}

	@Test
	void testUpdateConversationNullId() {
		Long conversationId = 1L;
		ConverstionRequestDto updateDto = createSampleConversationRequestDto();
		updateDto.setBody("Updated body");

		ConversationResponseDto mockResponse = populateRsponse();
		mockResponse.setBody(updateDto.getBody());
		when(conversationDao.getConversationById(anyLong())).thenReturn(null);
		when(freshdeskService.updateConversation(anyLong(), any(ConverstionRequestDto.class))).thenReturn(mockResponse);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<ConverstionRequestDto> entity = new HttpEntity<>(updateDto, headers);

		ResponseEntity<GenericWrapperDto<ConversationResponseDto>> response = restTemplate.exchange(
				baseUrl + "/" + conversationId, HttpMethod.PUT, entity,
				new ParameterizedTypeReference<GenericWrapperDto<ConversationResponseDto>>() {
				});

		assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);
	}

	@Test
	void testUpdateConversationNullService() {
		Long conversationId = 1L;
		ConverstionRequestDto updateDto = createSampleConversationRequestDto();
		updateDto.setBody("Updated body");

		ConversationResponseDto mockResponse = populateRsponse();
		mockResponse.setBody(updateDto.getBody());
		when(conversationDao.getConversationById(anyLong())).thenReturn(createSampleConversation());
		when(freshdeskService.updateConversation(anyLong(), any(ConverstionRequestDto.class))).thenReturn(null);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<ConverstionRequestDto> entity = new HttpEntity<>(updateDto, headers);

		ResponseEntity<GenericWrapperDto<ConversationResponseDto>> response = restTemplate.exchange(
				baseUrl + "/" + conversationId, HttpMethod.PUT, entity,
				new ParameterizedTypeReference<GenericWrapperDto<ConversationResponseDto>>() {
				});

		assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);
	}

	@Test
	void testUpdateConversationAttachment() throws IOException {
		Long conversationId = 1L;
		MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.txt", "text/plain",
				"test file 1".getBytes());

		ConversationResponseDto mockResponse = new ConversationResponseDto();
		mockResponse.setBody("Updated body with attachment");

		// Mock the behavior of DAO and service
		when(conversationDao.getConversationById(anyLong())).thenReturn(createSampleConversation());
		when(freshdeskService.updateConversationAttachment(eq(conversationId), anyString(), any()))
				.thenReturn(mockResponse);

		// Prepare form data with MultiValueMap
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("body", "Updated body with attachment");
		params.add("attachments", new ByteArrayResource(file1.getBytes()) {
			@Override
			public String getFilename() {
				return file1.getOriginalFilename();
			}
		});

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers);

		// Make the HTTP request
		ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/conversation-attachment/" + conversationId,
				HttpMethod.PUT, entity, String.class);

		// Assert the response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void testUpdateConversationAttachmentWithNullService() throws IOException {
		Long conversationId = 1L;
		MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.txt", "text/plain",
				"test file 1".getBytes());

		ConversationResponseDto mockResponse = new ConversationResponseDto();
		mockResponse.setBody("Updated body with attachment");

		// Mock the behavior of DAO and service
		when(conversationDao.getConversationById(anyLong())).thenReturn(createSampleConversation());
		when(freshdeskService.updateConversationAttachment(eq(conversationId), anyString(), any()))
				.thenReturn(null);

		// Prepare form data with MultiValueMap
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("body", "Updated body with attachment");
		params.add("attachments", new ByteArrayResource(file1.getBytes()) {
			@Override
			public String getFilename() {
				return file1.getOriginalFilename();
			}
		});

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers);

		// Make the HTTP request
		ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/conversation-attachment/" + conversationId,
				HttpMethod.PUT, entity, String.class);

		// Assert the response
		assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);
	}

	@Test
	void testUpdateConversationAttachmentWithNullId() throws IOException {
		Long conversationId = 1L;
		MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.txt", "text/plain",
				"test file 1".getBytes());

		ConversationResponseDto mockResponse = new ConversationResponseDto();
		mockResponse.setBody("Updated body with attachment");

		// Mock the behavior of DAO and service
		when(conversationDao.getConversationById(anyLong())).thenReturn(null);
		when(freshdeskService.updateConversationAttachment(eq(conversationId), anyString(), any()))
				.thenReturn(mockResponse);

		// Prepare form data with MultiValueMap
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("body", "Updated body with attachment");
		params.add("attachments", new ByteArrayResource(file1.getBytes()) {
			@Override
			public String getFilename() {
				return file1.getOriginalFilename();
			}
		});

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers);

		// Make the HTTP request
		ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/conversation-attachment/" + conversationId,
				HttpMethod.PUT, entity, String.class);

		// Assert the response
		assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);
	}

	@Test
	void testDeleteConversation() {
		Long conversationId = 1L;
		when(conversationDao.getConversationById(anyLong())).thenReturn(createSampleConversation());
		doNothing().when(freshdeskService).deleteConversation(anyLong());
		ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/delete/" + conversationId,
				HttpMethod.DELETE, null, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void testDeleteConversationFailure() {
		Long conversationId = 1L;

		when(conversationDao.getConversationById(anyLong())).thenReturn(null);
		doNothing().when(freshdeskService).deleteConversation(anyLong());

		ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/delete/" + conversationId,
				HttpMethod.DELETE, null, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ConversationResponseDto populateRsponse() {
		ConversationResponseDto mockResponse = new ConversationResponseDto();
		mockResponse.setBodyText("Sample body text");
		mockResponse.setBody("Sample body");
		mockResponse.setId(1L);
		mockResponse.setUserId(2L);
		mockResponse.setFromEmail("user@example.com");
		mockResponse.setCcEmails(List.of("cc1@example.com", "cc2@example.com"));
		mockResponse.setBccEmails(List.of("bcc1@example.com", "bcc2@example.com"));
		mockResponse.setTicketId(123L);
		mockResponse.setRepliedTo(List.of("reply1@example.com", "reply2@example.com"));
		mockResponse.setAttachments(List.of(new Attachment()));
		mockResponse.setCreatedAt(LocalDateTime.now());
		mockResponse.setUpdatedAt(LocalDateTime.now());
		return mockResponse;
	}

	private Conversation createSampleConversation() {
		Conversation conversation = new Conversation();
		conversation.setId(1L);
		conversation.setBody("Sample body");
		conversation.setCreatedAt(LocalDateTime.now());
		conversation.setUpdatedAt(LocalDateTime.now());
		return conversation;
	}
}