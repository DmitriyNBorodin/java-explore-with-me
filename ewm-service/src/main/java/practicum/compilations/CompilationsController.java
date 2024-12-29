package practicum.compilations;

import lombok.RequiredArgsConstructor;
import practicum.compilations.dto.CompilationDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class CompilationsController {
    private final CompilationsService compilationsService;

    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) String pinned,
                                                   @RequestParam(defaultValue = "0") String from,
                                                   @RequestParam(defaultValue = "10") String size) {
        return compilationsService.findAllCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        return compilationsService.findCompilationById(compId);
    }
}
