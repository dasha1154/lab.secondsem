package ru.dasha.lab.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public final class Protocol {
    private long id;
    private String name;
    private Set<MeasurementParam> requiredParams;
    private String ownerUsername;
    private Instant createdAt;
    private Instant updatedAt;

    public Protocol(Long id, String name, Set<MeasurementParam> requiredParams, String ownerUsername, Instant createdAt, Instant updatedAt) {
        this.id = id != null ? id : 0;
        this.name = name;
        this.requiredParams = requiredParams;
        this.ownerUsername = ownerUsername;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<MeasurementParam> getRequiredParams() {
        return requiredParams;
    }

    public void setRequiredParams(Set<MeasurementParam> requiredParams) {
        this.requiredParams = requiredParams;
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
        Protocol protocol = (Protocol) o;
        return id == protocol.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Protocol{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", requiredParams=" + requiredParams +
                ", owner='" + ownerUsername + '\'' +
                '}';
    }
}