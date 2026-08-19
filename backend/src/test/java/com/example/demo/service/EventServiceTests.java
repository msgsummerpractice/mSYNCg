package com.example.demo.service;

import com.example.demo.dto.response.EventDetailsResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.filtering.events.EventSpec;
import com.example.demo.model.Event;
import com.example.demo.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

	@Mock
	private EventRepository eventRepository;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private EventService eventService;

	@Test
	void getEvents_WhenEventsExist_ReturnsMappedPage() {
		Event event = new Event();
		EventViewResponse viewResponse = new EventViewResponse();
		viewResponse.setId(1);
		viewResponse.setName("Team event");
		EventSpec spec = mock(EventSpec.class);
		Pageable pageable = PageRequest.of(0, 20);
		Page<Event> eventsPage = new PageImpl<>(List.of(event), pageable, 1);

		when(eventRepository.findAll(spec, pageable)).thenReturn(eventsPage);
		when(modelMapper.map(event, EventViewResponse.class)).thenReturn(viewResponse);

		Page<EventViewResponse> result = eventService.getAll(spec, pageable);

		assertEquals(1, result.getTotalElements());
		assertEquals(viewResponse, result.getContent().get(0));
		assertEquals(pageable, result.getPageable());
		verify(modelMapper).map(event, EventViewResponse.class);
	}

	@Test
	void getEvents_WhenNoEventsMatch_ReturnsEmptyPageWithoutMapping() {
		EventSpec spec = mock(EventSpec.class);
		Pageable pageable = PageRequest.of(0, 20);

		when(eventRepository.findAll(spec, pageable))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		Page<EventViewResponse> result = eventService.getAll(spec, pageable);

		assertTrue(result.getContent().isEmpty());
		assertEquals(0, result.getTotalElements());
		verifyNoInteractions(modelMapper);
	}

	@Test
	void getEvents_WhenCalledWithSpecAndPageable_PassesThemToRepository() {
		EventSpec spec = mock(EventSpec.class);
		Pageable pageable = PageRequest.of(2, 5);

		when(eventRepository.findAll(spec, pageable))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		eventService.getAll(spec, pageable);

		ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(eventRepository).findAll(specCaptor.capture(), pageableCaptor.capture());

		assertEquals(spec, specCaptor.getValue());
		assertEquals(pageable, pageableCaptor.getValue());
	}

	@Test
	void getEvents_WhenSpecIsNull_QueriesRepositoryWithoutFilters() {
		Pageable pageable = PageRequest.of(0, 20);

		when(eventRepository.findAll((EventSpec) isNull(), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		Page<EventViewResponse> result = eventService.getAll(null, pageable);

		assertTrue(result.getContent().isEmpty());
		verify(eventRepository).findAll((EventSpec) isNull(), eq(pageable));
	}

	@Test
	void getEvents_WhenRepositoryFails_PropagatesException() {
		EventSpec spec = mock(EventSpec.class);
		Pageable pageable = PageRequest.of(0, 20);

		when(eventRepository.findAll(spec, pageable))
				.thenThrow(new DataAccessResourceFailureException("Database unavailable"));

		assertThrows(DataAccessResourceFailureException.class, () -> eventService.getAll(spec, pageable));
	}

	@Test
	void getById_WhenEventExists_ReturnsMappedDetails() {
		Event event = new Event();
		event.setId(1);
		event.setName("Team event");

		EventDetailsResponse detailsResponse = new EventDetailsResponse();
		detailsResponse.setId(1);
		detailsResponse.setName("Team event");

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(modelMapper.map(event, EventDetailsResponse.class)).thenReturn(detailsResponse);

		EventDetailsResponse result = eventService.getById(1);

		assertEquals(1, result.getId());
		assertEquals("Team event", result.getName());
		verify(eventRepository).findById(1);
		verify(modelMapper).map(event, EventDetailsResponse.class);
	}

	@Test
	void getById_WhenEventHasImage_EncodesImageAsBase64() {
		byte[] image = new byte[] { 1, 2, 3, 4 };
		Event event = new Event();
		event.setId(1);
		event.setImage(image);

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(modelMapper.map(event, EventDetailsResponse.class)).thenReturn(new EventDetailsResponse());

		EventDetailsResponse result = eventService.getById(1);

		assertEquals(Base64.getEncoder().encodeToString(image), result.getImage());
	}

	@Test
	void getById_WhenEventHasNoImage_ReturnsNullImage() {
		Event event = new Event();
		event.setId(1);

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(modelMapper.map(event, EventDetailsResponse.class)).thenReturn(new EventDetailsResponse());

		EventDetailsResponse result = eventService.getById(1);

		assertNull(result.getImage());
	}

	@Test
	void getById_WhenEventDoesNotExist_ThrowsNotFoundException() {
		when(eventRepository.findById(99)).thenReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class, () -> eventService.getById(99));

		assertEquals("Event with id 99 not found", exception.getMessage());
		verifyNoInteractions(modelMapper);
	}

	@Test
	void getById_WhenRepositoryFails_PropagatesException() {
		when(eventRepository.findById(1))
				.thenThrow(new DataAccessResourceFailureException("Database unavailable"));

		assertThrows(DataAccessResourceFailureException.class, () -> eventService.getById(1));
	}
}
