package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
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
import com.yuzee.app.freshdesk.dao.TicketDao;
import com.yuzee.app.freshdesk.dto.TicketDto;
import com.yuzee.app.freshdesk.dto.TicketResponseDto;
import com.yuzee.app.freshdesk.model.Ticket;
import com.yuzee.app.freshdesk.service.FreshdeskService;
import com.yuzee.common.lib.dto.GenericWrapperDto;

import org.springframework.boot.test.mock.mockito.MockBean;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = FreshDeskServiceApplication.class)
@TestInstance(Lifecycle.PER_CLASS)
class FreshDeskServiceApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private FreshdeskService freshdeskService;
	@MockBean
	private TicketDao ticketDao;

	private String baseUrl = "/api/v1";

	@BeforeEach
	void setUp() {

	}

	private TicketDto createSampleTicketDto() {
		TicketDto ticketDto = new TicketDto();
		ticketDto.setName("Test User");
		ticketDto.setRequesterId(123L);
		ticketDto.setEmail("testuser@example.com");
		ticketDto.setFacebookId("test_facebook_id");
		ticketDto.setPhone("1234567890");
		ticketDto.setTwitterId("test_twitter_id");
		ticketDto.setUniqueExternalId("unique_id_123");
		ticketDto.setSubject("Test Subject");
		ticketDto.setType("Issue");
		ticketDto.setStatus(2);
		ticketDto.setPriority(1);
		ticketDto.setDescription("Test description");
		ticketDto.setResponderId(456L);
		ticketDto.setCcEmails(Arrays.asList("cc1@example.com", "cc2@example.com"));
		ticketDto.setCustomFields(Map.of("custom_field_1", "value1"));
		ticketDto.setDueBy("2024-07-15T10:00:00Z");
		ticketDto.setEmailConfigId("email_config_1");
		ticketDto.setFrDueBy("2024-07-16T10:00:00Z");
		ticketDto.setGroupId(789L);
		ticketDto.setParentId(999L);
		ticketDto.setProductId(101L);
		ticketDto.setSource(2);
		ticketDto.setTags(Arrays.asList("tag1", "tag2"));
		ticketDto.setCompanyId(102L);
		ticketDto.setInternalAgentId(103);
		ticketDto.setInternalGroupId(104);
		ticketDto.setLookupParameter("lookup_param");
		return ticketDto;
	}

	private Ticket createSampleTicket() {
		Ticket ticketDto = new Ticket();
		ticketDto.setId(UUID.randomUUID().toString());
		ticketDto.setName("Test User");
		ticketDto.setRequesterId(123L);
		ticketDto.setEmail("testuser@example.com");
		ticketDto.setFacebookId("test_facebook_id");
		ticketDto.setPhone("1234567890");
		ticketDto.setTwitterId("test_twitter_id");
		ticketDto.setSubject("Test Subject");
		ticketDto.setType("Issue");
		ticketDto.setStatus(2);
		ticketDto.setPriority(1);
		ticketDto.setDescription("Test description");
		ticketDto.setResponderId(456L);
		ticketDto.setCcEmails(Arrays.asList("cc1@example.com", "cc2@example.com"));
		ticketDto.setCustomFields(Map.of("custom_field_1", "value1"));

		ticketDto.setEmailConfigId(123L);
		ticketDto.setProductId(101L);
		ticketDto.setSource(2);
		ticketDto.setTags(Arrays.asList("tag1", "tag2"));
		ticketDto.setCompanyId(102L);

		return ticketDto;
	}

	private TicketResponseDto createSampleTicketResponseDto() {
		TicketResponseDto responseDto = new TicketResponseDto();
		responseDto.setCcEmails(Arrays.asList("cc1@example.com", "cc2@example.com"));
		responseDto.setFwdEmails(Arrays.asList("fwd1@example.com", "fwd2@example.com"));
		responseDto.setReplyCcEmails(Arrays.asList("replyCc1@example.com", "replyCc2@example.com"));
		responseDto.setEmailConfigId(105L);
		responseDto.setGroupId(789L);
		responseDto.setPriority(1);
		responseDto.setRequesterId(123L);
		responseDto.setResponderId(456L);
		responseDto.setSource(2);
		responseDto.setStatus(2);
		responseDto.setSubject("Test Subject");
		responseDto.setCompanyId(102L);
		responseDto.setId(12345L);
		responseDto.setType("Issue");
		responseDto.setToEmails(Arrays.asList("to1@example.com", "to2@example.com"));
		responseDto.setProductId(101L);
		responseDto.setFrEscalated(false);
		responseDto.setSpam(false);
		responseDto.setUrgent(false);
		responseDto.setIsEscalated(false);
		responseDto.setCreatedAt(LocalDateTime.now());
		responseDto.setUpdatedAt(LocalDateTime.now());
		responseDto.setDueBy(LocalDateTime.now().plusDays(2));
		responseDto.setFrDueBy(LocalDateTime.now().plusDays(3));
		responseDto.setDescriptionText("Test description text");
		responseDto.setDescription("Test description");
		responseDto.setTags(Arrays.asList("tag1", "tag2"));
		responseDto.setAttachments(Collections.emptyList());
		return responseDto;
	}

	@Test
	void testCreateTicketSuccess() {
		TicketDto ticketDto = createSampleTicketDto();
		TicketResponseDto mockResponse = createSampleTicketResponseDto();
		// Prepare HTTP headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Create HTTP entity with request body and headers
		HttpEntity<TicketDto> entity = new HttpEntity<>(ticketDto, headers);

		when(freshdeskService.createTicket(ticketDto)).thenReturn(mockResponse);

		// Mock behavior of TicketDao
		when(ticketDao.createTicket(any(Ticket.class))).thenReturn(createSampleTicket());

		ResponseEntity<GenericWrapperDto<TicketResponseDto>> response = restTemplate.exchange(
				baseUrl + "/create-ticket", HttpMethod.POST, entity,
				new ParameterizedTypeReference<GenericWrapperDto<TicketResponseDto>>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getData().getRequesterId()).isEqualTo(ticketDto.getRequesterId());
		assertThat(response.getBody().getData().getResponderId()).isEqualTo(ticketDto.getResponderId());
	}

	@Test
	void testCreateTicketFailure() {
		TicketDto ticketDto = createSampleTicketDto();
		// Mock behavior of TicketDao

		when(freshdeskService.createTicket(ticketDto)).thenReturn(null);
		// Prepare HTTP headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Create HTTP entity with request body and headers
		HttpEntity<TicketDto> entity = new HttpEntity<>(ticketDto, headers);

		ResponseEntity<GenericWrapperDto<TicketResponseDto>> response = restTemplate.exchange(
				baseUrl + "/create-ticket", HttpMethod.POST, entity,
				new ParameterizedTypeReference<GenericWrapperDto<TicketResponseDto>>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

		verify(freshdeskService).createTicket(ticketDto);
	}
	
	@Test
	void testCreateTicketFailureFforExistingEmail() {
		TicketDto ticketDto = createSampleTicketDto();
		
		
		when(ticketDao.getTicketByEmail(anyString())).thenReturn(createSampleTicket());
		when(freshdeskService.createTicket(ticketDto)).thenThrow(new RuntimeException("Error creating ticket"));
		
		// Prepare HTTP headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Create HTTP entity with request body and headers
		HttpEntity<TicketDto> entity = new HttpEntity<>(ticketDto, headers);

		ResponseEntity<GenericWrapperDto<TicketResponseDto>> response = restTemplate.exchange(
				baseUrl + "/create-ticket", HttpMethod.POST, entity,
				new ParameterizedTypeReference<GenericWrapperDto<TicketResponseDto>>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody().getMessage()).isEqualTo("Error creating ticket");

		verify(freshdeskService).createTicket(ticketDto);
	}

	@Test
	void testCreateTicketWithAttachmentsSuccess() throws IOException {
		MockMultipartFile file1 = new MockMultipartFile("attachments[]", "file1.txt", "text/plain",
				"test file 1".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("attachments[]", "file2.txt", "text/plain",
				"test file 2".getBytes());
		MultipartFile[] files = new MultipartFile[] { file1, file2 };

		TicketResponseDto mockResponse = createSampleTicketResponseDto();

		when(freshdeskService.createTicketWithAttachments(eq("email@example.com"), eq("Subject"), eq("Description"),
				any(MultipartFile[].class))).thenReturn(mockResponse);

		// Mock behavior of TicketDao
		when(ticketDao.createTicket(any(Ticket.class))).thenReturn(createSampleTicket());

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("email", "email@example.com");
		params.add("subject", "Subject");
		params.add("description", "Description");
		params.add("attachments[]", file1.getResource());
		params.add("attachments[]", file2.getResource());

		ResponseEntity<TicketResponseDto> response = restTemplate.postForEntity(baseUrl + "/create-attachment", params,
				TicketResponseDto.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void testCreateTicketWithAttachmentsFailure() throws IOException {
		MockMultipartFile file1 = new MockMultipartFile("attachments[]", "file1.txt", "text/plain",
				"test file 1".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("attachments[]", "file2.txt", "text/plain",
				"test file 2".getBytes());
		MultipartFile[] files = new MultipartFile[] { file1, file2 };

		when(freshdeskService.createTicketWithAttachments(eq("email@example.com"), eq("Subject"), eq("Description"),
				any(MultipartFile[].class))).thenReturn(null);

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("email", "email@example.com");
		params.add("subject", "Subject");
		params.add("description", "Description");
		params.add("attachments[]", file1.getResource());
		params.add("attachments[]", file2.getResource());

		ResponseEntity<TicketResponseDto> response = restTemplate.postForEntity(baseUrl + "/create-attachment", params,
				TicketResponseDto.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	@Test
	void testCreateTicketWithAttachmentsFailureForExistingEmail() throws IOException {
		MockMultipartFile file1 = new MockMultipartFile("attachments[]", "file1.txt", "text/plain",
				"test file 1".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("attachments[]", "file2.txt", "text/plain",
				"test file 2".getBytes());
		MultipartFile[] files = new MultipartFile[] { file1, file2 };

		when(freshdeskService.createTicketWithAttachments(eq("email@example.com"), eq("Subject"), eq("Description"),
				any(MultipartFile[].class))).thenReturn(null);
		
		when(ticketDao.getTicketByEmail(anyString())).thenReturn(createSampleTicket());

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("email", "email@example.com");
		params.add("subject", "Subject");
		params.add("description", "Description");
		params.add("attachments[]", file1.getResource());
		params.add("attachments[]", file2.getResource());

		ResponseEntity<TicketResponseDto> response = restTemplate.postForEntity(baseUrl + "/create-attachment", params,
				TicketResponseDto.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Test
	void testGetTicketsSuccess() {
		TicketResponseDto mockResponse = createSampleTicketResponseDto();
		when(freshdeskService.getAllTickets(anyMap())).thenReturn(Arrays.asList(mockResponse));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Create HTTP entity with request body and headers
		HttpEntity<TicketDto> entity = new HttpEntity<>(null, headers);

		ResponseEntity<GenericWrapperDto<List<TicketResponseDto>>> response = restTemplate.exchange(
				baseUrl + "/tickets", HttpMethod.GET, entity,
				new ParameterizedTypeReference<GenericWrapperDto<List<TicketResponseDto>>>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getMessage()).isEqualTo("get all ticket");

		verify(freshdeskService).getAllTickets(anyMap());
	}
	
	 @Test
	    void testGetTicketsWithParams() throws Exception {
	        // Mock service response
	        TicketResponseDto mockResponse = createSampleTicketResponseDto();
	        when(freshdeskService.getAllTickets(anyMap())).thenReturn(Arrays.asList(mockResponse));

	        // Prepare query parameters
	        MultiValueMap<String, Object> queryParams = new LinkedMultiValueMap<>();
	        queryParams.add("filter", "status:open");
	        queryParams.add("email", "test@example.com");
	        queryParams.add("uniqueExternalId", "ext-123");
	        queryParams.add("updatedSince", "2023-01-01T00:00:00Z");
	        queryParams.add("orderBy", "createdAt");
	        queryParams.add("orderType", "asc");
	        queryParams.add("include", "metrics");

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        // Create HTTP entity with request body and headers
	        HttpEntity<Void> entity = new HttpEntity<>(headers);

	        // Execute GET request with query parameters
	        ResponseEntity<GenericWrapperDto<List<TicketResponseDto>>> response = restTemplate.exchange(
	        		baseUrl + "/tickets" + "?filter={filter}&email={email}&uniqueExternalId={uniqueExternalId}"
	                        + "&updatedSince={updatedSince}&orderBy={orderBy}&orderType={orderType}&include={include}",
	                HttpMethod.GET, entity,
	                new ParameterizedTypeReference<GenericWrapperDto<List<TicketResponseDto>>>() {
	                }, queryParams);

	        // Assert responses
	        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	        assertThat(response.getBody().getMessage()).isEqualTo("get all ticket");

	        // Verify service method called with expected parameters
	        verify(freshdeskService).getAllTickets(anyMap());
	    }

	@Test
	void testGetTicketByIdSuccess() {
		Long ticketId = 12345L;
		String include = "conversations";

		TicketResponseDto mockResponse = createSampleTicketResponseDto();
		when(freshdeskService.getTicketById(ticketId, include)).thenReturn(mockResponse);

		// Mock behavior of TicketDao
		when(ticketDao.getTicketById(ticketId)).thenReturn(createSampleTicket());

		ResponseEntity<GenericWrapperDto<TicketResponseDto>> response = restTemplate.exchange(
				baseUrl + "/tickets/" + ticketId + "?include=" + include, HttpMethod.GET, null,
				new ParameterizedTypeReference<GenericWrapperDto<TicketResponseDto>>() {
				});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getData().getId()).isEqualTo(ticketId);

		verify(freshdeskService).getTicketById(ticketId, include);
	}

	@Test
	void testGetTicketByIdNotFound() {
		Long ticketId = 12345L;
		String include = "conversations";

		when(freshdeskService.getTicketById(ticketId, include)).thenReturn(null);

		ResponseEntity<GenericWrapperDto<TicketResponseDto>> response = restTemplate.exchange(
				baseUrl + "/tickets/" + ticketId + "?include=" + include, HttpMethod.GET, null,
				new ParameterizedTypeReference<GenericWrapperDto<TicketResponseDto>>() {
				});

		assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);

		verify(freshdeskService).getTicketById(ticketId, include);
	}

	@Test
	void testDeleteTicketByIdSuccess() {
		Long ticketId = 12345L;

		TicketResponseDto mockResponse = createSampleTicketResponseDto();
		when(freshdeskService.getTicketById(ticketId, null)).thenReturn(mockResponse);

		// Mock behavior of TicketDao
		when(ticketDao.getTicketById(ticketId)).thenReturn(createSampleTicket());

		ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/tickets/" + ticketId, HttpMethod.DELETE, null,
				Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		verify(freshdeskService).deleteTicketById(ticketId);
	}

	@Test
	void testDeleteTicketByIdNotFound() {
		Long ticketId = 12345L;

		when(freshdeskService.getTicketById(ticketId, null)).thenReturn(null);

		ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/tickets/" + ticketId, HttpMethod.DELETE, null,
				Void.class);

		assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);

		verify(freshdeskService).getTicketById(ticketId, null);
	}
}
