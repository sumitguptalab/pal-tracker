package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {

    private TimeEntryRepository timeEntriesRepo;
    private final CounterService counter;
    private final GaugeService gauge;

    public TimeEntryController(
            TimeEntryRepository timeEntriesRepo,
            CounterService counter,
            GaugeService gauge
    ) {
        this.timeEntriesRepo = timeEntriesRepo;
        this.counter = counter;
        this.gauge = gauge;
    }

    @PostMapping("/time-entries")
    public ResponseEntity create(@RequestBody TimeEntry timeEntry) {

        TimeEntry createTimeEntry = timeEntriesRepo.create(timeEntry);
        counter.increment("TimeEntry.created");
        gauge.submit("timeEntries.count", timeEntriesRepo.list().size());
        return new ResponseEntity(createTimeEntry, HttpStatus.CREATED);
    }

    @GetMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable("id") long l) {

        TimeEntry timeEntry = timeEntriesRepo.find(l);

//        ResponseEntity<TimeEntry> timeEntryResponseEntity =
//                foundEntry == null ?
//                        new ResponseEntity<>(HttpStatus.NOT_FOUND) :
//                        new ResponseEntity<>(foundEntry, HttpStatus.OK);
//        return timeEntryResponseEntity;

        if (timeEntry != null) {
            counter.increment("TimeEntry.read");
            return new ResponseEntity<>(timeEntry, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {

        counter.increment("TimeEntry.listed");

        ResponseEntity responseEntity = new ResponseEntity<>(timeEntriesRepo.list(), HttpStatus.OK);


        return responseEntity;
    }

    @PutMapping("/time-entries/{id}")
    public ResponseEntity update(@PathVariable("id") long l, @RequestBody TimeEntry expected) {

        TimeEntry updatedTimeEntry = timeEntriesRepo.update(l, expected);
//        HttpStatus status = returnEntry == null ? HttpStatus.NOT_FOUND : HttpStatus.OK;
//
//        return new ResponseEntity(returnEntry, status);

        if (updatedTimeEntry != null) {
            counter.increment("TimeEntry.updated");
            return new ResponseEntity<>(updatedTimeEntry, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable("id") long l) {

        timeEntriesRepo.delete(l);
        counter.increment("TimeEntry.deleted");
        gauge.submit("timeEntries.count", timeEntriesRepo.list().size());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
