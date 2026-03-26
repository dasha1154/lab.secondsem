package ru.dasha.lab.service;

import ru.dasha.lab.domain.Sample;
import ru.dasha.lab.domain.SampleStatus;
import ru.dasha.lab.validation.SampleValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SampleManager {
    private final Set<Sample> samples = new HashSet<>();
    private long nextId = 1;


    public Sample addSample(String name, String type, String location, SampleStatus status, String ownerUsername) {
        Sample sample = new Sample(null, name, type, location, status, ownerUsername, null, null);
        SampleValidator.validate(sample);
        sample.setId(nextId++);
        samples.add(sample);
        return sample;
    }

    public Sample getSampleById(long id) {
        for (Sample s : samples) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }


    public List<Sample> getAllSamples() {
        return new ArrayList<>(samples);
    }

    public List<Sample> getSamplesByStatus(SampleStatus status) {
        List<Sample> result = new ArrayList<>();
        for (Sample s : samples) {
            if (s.getStatus() == status) {
                result.add(s);
            }
        }
        return result;
    }


    public boolean updateSample(long id, String newName, String newType, String newLocation, SampleStatus newStatus) {
        Sample oldSample = getSampleById(id);
        if (oldSample == null) {
            return false;
        }

        String name = (newName != null && !newName.isBlank()) ? newName : oldSample.getName();
        String type = (newType != null && !newType.isBlank()) ? newType : oldSample.getType();
        String location = (newLocation != null && !newLocation.isBlank()) ? newLocation : oldSample.getLocation();
        SampleStatus status = (newStatus != null) ? newStatus : oldSample.getStatus();

        Sample updatedSample = new Sample(oldSample.getId(), name, type, location, status, oldSample.getOwnerUsername(), oldSample.getCreatedAt(), null);

        SampleValidator.validate(updatedSample);

        samples.remove(oldSample);
        samples.add(updatedSample);
        return true;
    }


    public boolean deleteSample(long id) {
        Sample sample = getSampleById(id);
        if (sample == null) {
            return false;
        }
        samples.remove(sample);
        return true;
    }


    public boolean archiveSample(long id) {
        Sample sample = getSampleById(id);
        if (sample == null) {
            return false;
        }
        if (sample.getStatus() == SampleStatus.ARCHIVED) {
            return false;
        }
        return updateSample(id, null, null, null, SampleStatus.ARCHIVED);
    }

    public boolean exists(long id) {
        Sample sample = getSampleById(id);
        return sample != null;
    }
}