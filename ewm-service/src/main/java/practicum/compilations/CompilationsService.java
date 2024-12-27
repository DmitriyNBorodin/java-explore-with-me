package practicum.compilations;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import practicum.compilations.dto.Compilation;
import practicum.compilations.dto.CompilationDto;
import practicum.compilations.dto.NewCompilationDto;
import practicum.compilations.dto.UpdateCompilationDto;
import practicum.events.EventRepository;
import practicum.events.dto.Event;
import practicum.events.dto.EventDtoMapper;
import practicum.events.dto.EventShortDto;
import practicum.util.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationsService {
    private final CompilationsRepository compilationsRepository;
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;

    public CompilationDto addNewCompilation(NewCompilationDto newCompilation) {
        log.info("Добавление новой подборки {}", newCompilation);
        return convertCompilationDaoToDto(compilationsRepository.save(assembleCompilationDao(newCompilation)));
    }

    @Transactional
    public void deleteCompilationById(Long id) {
        log.info("Удаление подборки событий id={}", id);
        if (compilationsRepository.deleteCompilationDaoById(id) == 0) {
            throw new ObjectNotFoundException("Compilation with id=" + id + " was not found");
        }
    }

    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateRequest) {
        log.info("Обновление подборки id={}, данные для обновления {}", compId, updateRequest);
        Compilation updatingCompilation = compilationsRepository.findCompilationDaoById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " was not found"));
        updateCompilationFields(updatingCompilation, updateRequest);
        return convertCompilationDaoToDto(compilationsRepository.save(updatingCompilation));
    }

    public List<CompilationDto> findAllCompilations(String pinnedString, String fromString, String sizeString) {
        Long from = Long.parseLong(fromString);
        Long size = Long.parseLong(sizeString);
        Boolean pinned;
        if (pinnedString != null) {
            pinned = Boolean.parseBoolean(pinnedString);
        } else {
            pinned = null;
        }
        List<Compilation> rawCompilationsList = compilationsRepository.findAllCompilations(pinned, from, size);
        return rawCompilationsList.stream().map(this::convertCompilationDaoToDto).toList();
    }

    public CompilationDto findCompilationById(Long compId) {
        Compilation requiredCompilation = compilationsRepository.findCompilationDaoById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " was not found"));
        return convertCompilationDaoToDto(requiredCompilation);
    }

    private Compilation assembleCompilationDao(NewCompilationDto newCompilation) {
        List<Event> newEventList = eventRepository.findEventDaoByIdIn(newCompilation.getEvents());
        return Compilation.builder()
                .title(newCompilation.getTitle())
                .pinned(newCompilation.getPinned())
                .events(newEventList)
                .build();
    }

    private CompilationDto convertCompilationDaoToDto(Compilation compilationDao) {
        List<EventShortDto> eventDtoOfCompilation = compilationDao.getEvents().stream().map(eventDtoMapper::assembleEventShortDto).toList();
        List<EventShortDto> finalEventDtoList = eventDtoMapper.assignViewsAndRequests(eventDtoOfCompilation);
        return CompilationDto.builder()
                .id(compilationDao.getId())
                .pinned(compilationDao.getPinned())
                .title(compilationDao.getTitle())
                .events(finalEventDtoList)
                .build();
    }

    private Compilation updateCompilationFields(Compilation updatingCompilation, UpdateCompilationDto updateRequest) {
        if (updateRequest.getPinned() != null) {
            updatingCompilation.setPinned(updateRequest.getPinned());
        }
        if (updateRequest.getTitle() != null) {
            updatingCompilation.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getEvents() != null) {
            List<Event> newEventList = eventRepository.findEventDaoByIdIn(updateRequest.getEvents());
            updatingCompilation.setEvents(newEventList);
        }
        return updatingCompilation;
    }
}
