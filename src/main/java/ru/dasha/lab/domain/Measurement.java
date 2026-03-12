package ru.dasha.lab.domain;

import java.time.Instant;
import java.util.Objects;

public final class Measurement {
    private long id;
    private long sampleId;
    private MeasurementParam param;
    private double value;
    private String unit;
    private String method;
    private Instant measuredAt;
    private String ownerUsername;
    private Instant createdAt;
    private Instant updatedAt;


    public Measurement(long id, long sampleId, MeasurementParam param, double value, String unit, String method, Instant measuredAt, String ownerUsername, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.sampleId = sampleId;
        this.param = param;
        this.value = value;
        this.unit = unit;
        this.method = method;
        this.measuredAt = measuredAt;
        this.ownerUsername = ownerUsername;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSampleId() {
        return sampleId;
    }

    public void setSampleId(long sampleId) {
        this.sampleId = sampleId;
    }

    public MeasurementParam getParam() {
        return param;
    }

    public void setParam(MeasurementParam param) {
        this.param = param;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Instant getMeasuredAt() {
        return measuredAt;
    }

    public void setMeasuredAt(Instant measuredAt) {
        this.measuredAt = measuredAt;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measurement that = (Measurement) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "id=" + id +
                ", sampleId=" + sampleId +
                ", param=" + param +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                ", method='" + method + '\'' +
                ", owner='" + ownerUsername + '\'' +
                '}';
    }
}