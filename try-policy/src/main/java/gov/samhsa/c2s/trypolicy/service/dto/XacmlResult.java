package gov.samhsa.c2s.trypolicy.service.dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

/**
 * The Class XacmlResult.
 */
@XmlRootElement(name = "xacmlResult")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class XacmlResult {

    /**
     * The pdp decision.
     */
    private String pdpDecision;

    /**
     * The subject purpose of use.
     */
    @XmlElement(name = "purposeOfUse")
    private SubjectPurposeOfUse subjectPurposeOfUse;

    /**
     * The message id.
     */
    private String messageId;

    /**
     * The home community id.
     */
    private String homeCommunityId;

    /**
     * The pdp obligations.
     */
    @XmlElement(name = "pdpObligation")
    private List<String> pdpObligations;

    /**
     * The patient id.
     */
    private String patientId;

    /**
     * Instantiates a new xacml result.
     */
    public XacmlResult() {
        pdpObligations = new LinkedList<String>();
    }
}