package ru.dasha.lab.service;

import ru.dasha.lab.domain.Measurement;
import ru.dasha.lab.domain.MeasurementParam;
import ru.dasha.lab.domain.SampleStatus;
import ru.dasha.lab.validation.MeasurementValidator;

import java.util.*;
import java.util.stream.Collectors;

public class MeasurementManager {
    private final Set<Measurement> measurements = new HashSet<>();
    private long nextId = 1;
    private final SampleManager sampleManager;
    public MeasurementManager(SampleManager sampleManager) {
        this.sampleManager = sampleManager;
    }

    public Measurement addMeasurement(long sampleId, MeasurementParam param, double value, String unit, String method, String ownerUsername) {
        var sample = sampleManager.getSampleById(sampleId);
        if (sample == null) {
            throw new IllegalArgumentException("Образец с id=" + sampleId + " не найден");
        }
        if (sample.getStatus() == SampleStatus.ARCHIVED) {
            throw new IllegalArgumentException("Нельзя добавлять измерения к архивному образцу");
        }

        Measurement measurement = new Measurement(null, sampleId, param, value, unit, method, null, ownerUsername, null, null);
        MeasurementValidator.validate(measurement);
        measurement.setId(nextId++);

        measurements.add(measurement);
        return measurement;
    }

    public Measurement getMeasurementById(long id) {
        for (Measurement m : measurements) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    public List<Measurement> getAllMeasurements() {
        return new ArrayList<>(measurements);
    }

    public List<Measurement> getMeasurementsBySampleId(long sampleId) {
        List<Measurement> result = new ArrayList<>();
        for (Measurement m : measurements) {
            if (m.getSampleId() == sampleId) {
                result.add(m);
            }
        }
        return result;
    }

    public List<Measurement> getMeasurementsBySampleIdAndParam(long sampleId, MeasurementParam param) {
        List<Measurement> result = new ArrayList<>();
        for (Measurement m : measurements) {
            if (m.getSampleId() == sampleId && m.getParam() == param) {
                result.add(m);
            }
        }
        return result;
    }

    public List<Measurement> getLastMeasurements(long sampleId, int n) {
        return measurements.stream()
                .filter(m -> m.getSampleId() == sampleId)
                .sorted(Comparator.comparing(Measurement::getMeasuredAt).reversed()) // use method not lambda
                .limit(n)
                .collect(Collectors.toList());
    }

    public MeasurementStats getStatistics(long sampleId, MeasurementParam param) {
        List<Measurement> list = getMeasurementsBySampleIdAndParam(sampleId, param);
        if (list.isEmpty()) {
            return null;
        }
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double sum = 0;
        for (Measurement m : list) {
            double val = m.getValue();
            if (val < min) min = val;
            if (val > max) max = val;
            sum += val;
        }
        return new MeasurementStats(list.size(), min, max, sum / list.size());
    }

    public static class MeasurementStats {
        private final int count;
        private final double min;
        private final double max;
        private final double avg;

        public MeasurementStats(int count, double min, double max, double avg) {
            this.count = count;
            this.min = min;
            this.max = max;
            this.avg = avg;
        }

        public int getCount() { return count; }
        public double getMin() { return min; }
        public double getMax() { return max; }
        public double getAvg() { return avg; }
    }

    public boolean updateMeasurement(long id, MeasurementParam newParam, Double newValue, String newUnit, String newMethod) {
        Measurement oldMeasurement = getMeasurementById(id);
        if (oldMeasurement == null) {
            return false;
        }

        MeasurementParam param = (newParam != null) ? newParam : oldMeasurement.getParam();
        double value = (newValue != null) ? newValue : oldMeasurement.getValue();
        String unit = (newUnit != null && !newUnit.isBlank()) ? newUnit : oldMeasurement.getUnit();
        String method = (newMethod != null && !newMethod.isBlank()) ? newMethod : oldMeasurement.getMethod();

        Measurement updatedMeasurement = new Measurement(
                oldMeasurement.getId(),
                oldMeasurement.getSampleId(),
                param,
                value,
                unit,
                method,
                oldMeasurement.getMeasuredAt(),
                oldMeasurement.getOwnerUsername(),
                oldMeasurement.getCreatedAt(),
                null
        );

        MeasurementValidator.validate(updatedMeasurement);

        measurements.remove(oldMeasurement);
        measurements.add(updatedMeasurement);
        return true;
    }

    public boolean deleteMeasurement(long id) {
        Measurement m = getMeasurementById(id);
        if (m == null) {
            return false;
        }
        measurements.remove(m);
        return true;
    }

    public boolean exists(long id) {
        return getMeasurementById(id) != null;
    }
}