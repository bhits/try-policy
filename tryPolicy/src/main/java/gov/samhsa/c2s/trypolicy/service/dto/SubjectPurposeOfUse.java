package gov.samhsa.c2s.trypolicy.service.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * The Enum SubjectPurposeOfUse.
 */
@XmlEnum
public enum SubjectPurposeOfUse {
    @XmlEnumValue("TREATMENT")
    HEALTHCARE_TREATMENT("TREATMENT"),
    @XmlEnumValue("PAYMENT")
    PAYMENT("HEALTHCARE PAYMENT"),
    @XmlEnumValue("EMERGENCY")
    EMERGENCY_TREATMENT("EMERGENCY"),
    @XmlEnumValue("RESEARCH")
    RESEARCH("HEALTHCARE RESEARCH");

    private final String purpose;

    SubjectPurposeOfUse(String p) {
        purpose = p;
    }

    public static SubjectPurposeOfUse fromValue(String v) {
        return valueOf(v);
    }

    public String getPurpose() {
        return purpose;
    }

    public static SubjectPurposeOfUse fromAbbreviation(String purposeOfUse) {
        for (SubjectPurposeOfUse p : SubjectPurposeOfUse.values()) {
            String pou = p.getPurpose();
            if (p.getPurpose().equalsIgnoreCase(purposeOfUse)) {
                return p;
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append("The abbreviation '");
        builder.append(purposeOfUse);
        builder.append("' is not defined in this enum.");
        throw new IllegalArgumentException(builder.toString());
    }
}