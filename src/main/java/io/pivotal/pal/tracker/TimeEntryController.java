package io.pivotal.pal.tracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {

    private TimeEntryRepository repository;

    public TimeEntryController(TimeEntryRepository timeEntryRepository) {
        this.repository = timeEntryRepository;
    }

    @PostMapping("/time-entries")
    public ResponseEntity create(@RequestBody TimeEntry timeEntry) {
        return new ResponseEntity(repository.create(timeEntry), HttpStatus.CREATED);
    }

    @GetMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable("id") long l) {

        TimeEntry foundEntry = repository.find(l);

        ResponseEntity<TimeEntry> timeEntryResponseEntity =
                foundEntry == null ?
                        new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                        new ResponseEntity<>(foundEntry, HttpStatus.OK);
        return timeEntryResponseEntity;
    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {
        ResponseEntity responseEntity = new ResponseEntity<>(repository.list(), HttpStatus.OK);


        return responseEntity;
    }

    @PutMapping("/time-entries/{id}")
    public ResponseEntity update(@PathVariable("id") long l, @RequestBody TimeEntry expected) {

        TimeEntry returnEntry = repository.update(l, expected);
        HttpStatus status = returnEntry == null ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        return new ResponseEntity(returnEntry, status);

    }

    @DeleteMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable("id") long l) {

        repository.delete(l);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
