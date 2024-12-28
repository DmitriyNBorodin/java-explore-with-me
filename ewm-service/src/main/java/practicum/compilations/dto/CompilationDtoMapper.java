package practicum.compilations.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practicum.events.EventRepository;
import practicum.events.dto.Event;
import practicum.events.dto.EventDtoMapper;
import practicum.events.dto.EventShortDto;

import java.util.InputMismatchException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CompilationDtoMapper {
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;


    public Compilation assembleCompilation(NewCompilationDto newCompilation) {
        if (newCompilation == null) {
            throw new InputMismatchException("Failed to get new compilation date");
        }
        List<Event> newEventList = eventRepository.findEventByIdIn(newCompilation.getEvents());
        return Compilation.builder()
                .title(newCompilation.getTitle())
                .pinned(newCompilation.getPinned())
                .events(newEventList)
                .build();
    }

    public CompilationDto convertCompilationToDto(Compilation compilation) {
        List<EventShortDto> eventDtoOfCompilation = compilation.getEvents().stream().map(eventDtoMapper::assembleEventShortDto).toList();
        List<EventShortDto> finalEventDtoList = eventDtoMapper.assignViewsAndRequests(eventDtoOfCompilation);
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(finalEventDtoList)
                .build();
    }

    public void updateCompilationFields(Compilation updatingCompilation, UpdateCompilationDto updateRequest) {
        if (updateRequest == null) {
            throw new InputMismatchException("Failed to get compilation update request data");
        }
        if (updateRequest.getPinned() != null) {
            updatingCompilation.setPinned(updateRequest.getPinned());
        }
        if (updateRequest.getTitle() != null) {
            updatingCompilation.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getEvents() != null) {
            List<Event> newEventList = eventRepository.findEventByIdIn(updateRequest.getEvents());
            updatingCompilation.setEvents(newEventList);
        }
    }
}
