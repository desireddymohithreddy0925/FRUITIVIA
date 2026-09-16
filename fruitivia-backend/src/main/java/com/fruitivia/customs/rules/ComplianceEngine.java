package com.fruitivia.customs.rules;

import com.fruitivia.customs.CustomsClearance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplianceEngine {

    private final List<ComplianceRule> rules;

    public List<String> validateClearance(CustomsClearance clearance) {
        List<String> errors = new ArrayList<>();
        
        for (ComplianceRule rule : rules) {
            if (rule.appliesTo(clearance)) {
                if (!rule.isCompliant(clearance)) {
                    errors.add(rule.getErrorMessage());
                }
            }
        }
        
        return errors;
    }
}
