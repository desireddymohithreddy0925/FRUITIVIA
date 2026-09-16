package com.fruitivia.customs.rules;

import com.fruitivia.customs.CustomsClearance;

public interface ComplianceRule {
    /**
     * Checks if the clearance is compliant with this rule.
     * @param clearance The customs clearance to check
     * @return true if compliant, false otherwise
     */
    boolean isCompliant(CustomsClearance clearance);

    /**
     * Gets the error message if the rule is not compliant.
     * @return error message
     */
    String getErrorMessage();
    
    /**
     * Determines if this rule applies to the given clearance (e.g., specific to a country)
     */
    boolean appliesTo(CustomsClearance clearance);
}
