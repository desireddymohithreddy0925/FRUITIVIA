package com.fruitivia.audit;

import org.hibernate.envers.RevisionListener;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        AuditRevisionEntity auditRevisionEntity = (AuditRevisionEntity) revisionEntity;
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            auditRevisionEntity.setActor(authentication.getName());
        } else {
            auditRevisionEntity.setActor("SYSTEM");
        }
        
        String correlationId = MDC.get("correlationId");
        if (correlationId != null) {
            auditRevisionEntity.setCorrelationId(correlationId);
        }
    }
}
