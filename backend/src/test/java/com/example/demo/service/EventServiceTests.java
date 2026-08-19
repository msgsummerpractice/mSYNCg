package com.example.demo.service;

import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.dto.request.EventRequest;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

	private Event createEvent(Integer id) {
		Event event = new Event();
		event.setId(id);
		return event;
	}

	private EventRequest createEventRequest(String name) {
		return createEventRequest(name, null);
	}

	private EventRequest createEventRequest(String name, String imageBase64) {
		EventRequest request = new EventRequest();
		request.setName(name);
		request.setImageBase64(imageBase64);
		return request;
	}

	private EventViewResponse createViewResponse(Integer id, String name) {
		EventViewResponse response = new EventViewResponse();
		response.setId(id);
		response.setName(name);
		return response;
	}

	@Test
	void getEvents_whenEventsExist_returnsMappedPage() {
		Event event = createEvent(1);
		EventViewResponse viewResponse = createViewResponse(1, "Team event");
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
	void getEvents_whenNoEventsMatch_returnsEmptyPageWithoutMapping() {
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
	void getEvents_whenCalledWithSpecAndPageable_passesThemToRepository() {
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
	void getEvents_whenSpecIsNull_queriesRepositoryWithoutFilters() {
		Pageable pageable = PageRequest.of(0, 20);

		when(eventRepository.findAll((EventSpec) isNull(), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		Page<EventViewResponse> result = eventService.getAll(null, pageable);

		assertTrue(result.getContent().isEmpty());
		verify(eventRepository).findAll((EventSpec) isNull(), eq(pageable));
	}

	@Test
	void getEvents_whenRepositoryFails_propagatesException() {
		EventSpec spec = mock(EventSpec.class);
		Pageable pageable = PageRequest.of(0, 20);

		when(eventRepository.findAll(spec, pageable))
				.thenThrow(new DataAccessResourceFailureException("Database unavailable"));

		assertThrows(DataAccessResourceFailureException.class, () -> eventService.getAll(spec, pageable));
	}

	@Test
	void updateEvent_whenEventExists_updatesAndReturnsMappedResponse() {
		Integer eventId = 1;
		Event existingEvent = createEvent(eventId);
		EventRequest eventRequest = createEventRequest("Updated Event");
		Event mappedEvent = createEvent(null);
		Event savedEvent = createEvent(eventId);
		EventViewResponse viewResponse = createViewResponse(eventId, "Updated Event");

		when(eventRepository.findById(eventId)).thenReturn(java.util.Optional.of(existingEvent));
		when(modelMapper.map(eventRequest, Event.class)).thenReturn(mappedEvent);
		when(eventRepository.save(mappedEvent)).thenReturn(savedEvent);
		when(modelMapper.map(savedEvent, EventViewResponse.class)).thenReturn(viewResponse);

		EventViewResponse result = eventService.updateEvent(eventId, eventRequest);

		assertEquals(viewResponse, result);
		verify(eventRepository).findById(eventId);
		verify(modelMapper).map(eventRequest, Event.class);
		verify(eventRepository).save(mappedEvent);
		verify(modelMapper).map(savedEvent, EventViewResponse.class);
	}

	@Test
	void updateEvent_whenEventNotFound_throwsException() {
		Integer eventId = 999;
		EventRequest eventRequest = new EventRequest();

		when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> eventService.updateEvent(eventId, eventRequest));
		verify(eventRepository).findById(eventId);
		verifyNoInteractions(modelMapper);
	}

	@Test
	void updateEvent_whenImageBase64Provided_decodesAndSaves() {
		Integer eventId = 1;
		Event existingEvent = createEvent(eventId);
		String imageBase64 = "aGVsbG8gd29ybGQ=";
		EventRequest eventRequest = createEventRequest("Event with image", imageBase64);
		Event mappedEvent = createEvent(null);
		Event savedEvent = createEvent(eventId);
		EventViewResponse viewResponse = createViewResponse(eventId, null);

		when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
		when(modelMapper.map(eventRequest, Event.class)).thenReturn(mappedEvent);
		when(eventRepository.save(mappedEvent)).thenReturn(savedEvent);
		when(modelMapper.map(savedEvent, EventViewResponse.class)).thenReturn(viewResponse);

		EventViewResponse result = eventService.updateEvent(eventId, eventRequest);

		assertEquals(viewResponse, result);
		verify(eventRepository).findById(eventId);
		verify(eventRepository).save(mappedEvent);
	}

	@Test
	void updateEvent_whenNoImageProvided_savesWithoutImage() {
		Integer eventId = 1;
		Event existingEvent = createEvent(eventId);
		EventRequest eventRequest = createEventRequest("Event without image", null);
		Event mappedEvent = createEvent(null);
		Event savedEvent = createEvent(eventId);
		EventViewResponse viewResponse = createViewResponse(eventId, null);

		when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
		when(modelMapper.map(eventRequest, Event.class)).thenReturn(mappedEvent);
		when(eventRepository.save(mappedEvent)).thenReturn(savedEvent);
		when(modelMapper.map(savedEvent, EventViewResponse.class)).thenReturn(viewResponse);

		EventViewResponse result = eventService.updateEvent(eventId, eventRequest);

		assertEquals(viewResponse, result);
		verify(eventRepository).findById(eventId);
		verify(eventRepository).save(mappedEvent);
	}
}
