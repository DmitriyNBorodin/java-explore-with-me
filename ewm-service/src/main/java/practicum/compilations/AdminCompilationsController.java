package practicum.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import practicum.compilations.dto.CompilationDto;
import practicum.compilations.dto.NewCompilationDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import practicum.compilations.dto.UpdateCompilationDto;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationsController {
    private final CompilationsService compilationsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addNewCompilation(@Validated @RequestBody NewCompilationDto newCompilation) {
        return compilationsService.addNewCompilation(newCompilation);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@PathVariable Long compId) {
        compilationsService.deleteCompilationById(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilationById(@PathVariable Long compId, @Validated @RequestBody UpdateCompilationDto compilationUpdate) {
        return compilationsService.updateCompilation(compId, compilationUpdate);
    }
}
