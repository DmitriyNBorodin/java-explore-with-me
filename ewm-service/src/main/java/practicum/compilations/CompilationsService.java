package practicum.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import practicum.compilations.dto.Compilation;
import practicum.compilations.dto.CompilationDto;
import practicum.compilations.dto.CompilationDtoMapper;
import practicum.compilations.dto.NewCompilationDto;
import practicum.compilations.dto.UpdateCompilationDto;
import practicum.util.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationsService {
    private final CompilationsRepository compilationsRepository;
    private final CompilationDtoMapper compilationDtoMapper;

    @Transactional
    public CompilationDto addNewCompilation(NewCompilationDto newCompilationDto) {
        log.info("Добавление новой подборки {}", newCompilationDto);
        Compilation newCompilation = compilationsRepository.save(compilationDtoMapper.assembleCompilation(newCompilationDto));
        return compilationDtoMapper.convertCompilationToDto(newCompilation);
    }

    @Transactional
    public void deleteCompilationById(Long id) {
        log.info("Удаление подборки событий id={}", id);
        if (compilationsRepository.deleteCompilationById(id) == 0) {
            throw new ObjectNotFoundException("Compilation with id=" + id + " was not found");
        }
    }

    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateRequest) {
        log.info("Обновление подборки id={}, данные для обновления {}", compId, updateRequest);
        Compilation updatingCompilation = compilationsRepository.findCompilationById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " was not found"));
        compilationDtoMapper.updateCompilationFields(updatingCompilation, updateRequest);
        return compilationDtoMapper.convertCompilationToDto(compilationsRepository.save(updatingCompilation));
    }

    @Transactional(readOnly = true)
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
        return rawCompilationsList.stream().map(compilationDtoMapper::convertCompilationToDto).toList();
    }

    @Transactional(readOnly = true)
    public CompilationDto findCompilationById(Long compId) {
        Compilation requiredCompilation = compilationsRepository.findCompilationById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Compilation with id=" + compId + " was not found"));
        return compilationDtoMapper.convertCompilationToDto(requiredCompilation);
    }


}
