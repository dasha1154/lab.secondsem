package ru.dasha.lab.service;

import ru.dasha.lab.domain.MeasurementParam;
import ru.dasha.lab.domain.Protocol;
import ru.dasha.lab.validation.ProtocolValidator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProtocolManager {
    private final Set<Protocol> protocols = new HashSet<>();
    private long nextId = 1;


    public Protocol createProtocol(String name, Set<MeasurementParam> requiredParams, String ownerUsername) {
        Protocol protocol = new Protocol(name, requiredParams, ownerUsername);
        ProtocolValidator.validate(protocol);
        protocol.setId(nextId++);
        protocol.setCreatedAt(Instant.now());
        protocol.setUpdatedAt(Instant.now());
        protocols.add(protocol);
        return protocol;
    }

    public Protocol getProtocolById(long id) {
        for (Protocol p : protocols) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public List<Protocol> getAllProtocols() {
        return new ArrayList<>(protocols);
    }


    public boolean updateProtocol(long id, String newName, Set<MeasurementParam> newRequiredParams) {
        Protocol oldProtocol = getProtocolById(id);
        if (oldProtocol == null) {
            return false;
        }

        String name = (newName != null && !newName.isBlank()) ? newName : oldProtocol.getName();
        Set<MeasurementParam> requiredParams = (newRequiredParams != null) ? newRequiredParams : oldProtocol.getRequiredParams();

        Protocol updatedProtocol = new Protocol(name, requiredParams, oldProtocol.getOwnerUsername());
        updatedProtocol.setId(oldProtocol.getId());
        updatedProtocol.setCreatedAt(oldProtocol.getCreatedAt());

        ProtocolValidator.validate(updatedProtocol);
        updatedProtocol.setUpdatedAt(Instant.now());

        protocols.remove(oldProtocol);
        protocols.add(updatedProtocol);
        return true;
    }

    public boolean deleteProtocol(long id) {
        Protocol protocol = getProtocolById(id);
        if (protocol == null) {
            return false;
        }
        protocols.remove(protocol);
        return true;
    }

    public boolean exists(long id) {
        return getProtocolById(id) != null;
    }

    public boolean isProtocolComplete(long protocolId, Set<MeasurementParam> actualParams) {
        Protocol protocol = getProtocolById(protocolId);
        if (protocol == null) {
            throw new IllegalArgumentException("Протокол с id=" + protocolId + " не найден");
        }
        return actualParams.containsAll(protocol.getRequiredParams());
    }

    public Set<MeasurementParam> getMissingParams(long protocolId, Set<MeasurementParam> actualParams) {
        Protocol protocol = getProtocolById(protocolId);
        if (protocol == null) {
            throw new IllegalArgumentException("Протокол с id=" + protocolId + " не найден");
        }
        Set<MeasurementParam> missing = new HashSet<>(protocol.getRequiredParams());
        missing.removeAll(actualParams);
        return missing;
    }
}