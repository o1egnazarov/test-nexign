package ru.noleg.testnexign.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class CdrRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String typeCall;
    private String initiator;
    private String receiver;
    private LocalDateTime startCall;
    private LocalDateTime endCall;

    public CdrRecord() {
    }

    public CdrRecord(Long id,
                     String typeCall,
                     String initiator,
                     String receiver,
                     LocalDateTime startCall,
                     LocalDateTime endCall) {
        this.id = id;
        this.typeCall = typeCall;
        this.initiator = initiator;
        this.receiver = receiver;
        this.startCall = startCall;
        this.endCall = endCall;
    }

    public Long getId() {
        return id;
    }

    public String getTypeCall() {
        return typeCall;
    }

    public String getInitiator() {
        return initiator;
    }

    public String getReceiver() {
        return receiver;
    }

    public LocalDateTime getStartCall() {
        return startCall;
    }

    public LocalDateTime getEndCall() {
        return endCall;
    }
}
