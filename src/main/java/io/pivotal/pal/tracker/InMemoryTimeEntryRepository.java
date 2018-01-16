package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private Map<Long, TimeEntry> map;
    private Long counter;


    public InMemoryTimeEntryRepository() {
        this.map = new HashMap<>();
        this.counter = 1L;
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        Long key = timeEntry.getId();

        if (key == null)
        {
            key = counter;
            timeEntry.setId(key);
            counter++;
        }


        this.map.put(key, timeEntry);

        return timeEntry;
    }

    @Override
    public TimeEntry find(long id) {

        return this.map.get(id);
    }

    @Override
    public List<TimeEntry> list() {

        return new ArrayList<>(this.map.values());
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        timeEntry.setId(id);
        this.map.put(id, timeEntry);
        return timeEntry;
    }

    @Override
    public void delete(long id) {
        this.map.remove(id);
    }
}
