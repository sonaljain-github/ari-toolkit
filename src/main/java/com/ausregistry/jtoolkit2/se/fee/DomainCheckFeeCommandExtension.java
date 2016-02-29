package com.ausregistry.jtoolkit2.se.fee;

import com.ausregistry.jtoolkit2.ErrorPkg;
import com.ausregistry.jtoolkit2.se.*;
import com.ausregistry.jtoolkit2.xml.XMLWriter;
import org.w3c.dom.Element;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Extension for the EPP Domain Check command, representing the Check Domain aspect of the
 * Domain Name Fee Extension.</p>
 *
 * <p>Use this to acknowledge the price associated with this domain name as part of an EPP Domain Check
 * command compliant with RFC5730 and RFC5731. The "currency", "name", "command" and "period" values
 * supplied, should match the fees that are set for the domain name for the requested period.
 * The response expected from a server should be handled by a Domain Check Response.</p>
 *
 * @see DomainCheckCommand
 * @see DomainCheckResponse
 * @see <a href="https://tools.ietf.org/html/draft-brown-epp-fees-03">Domain Name Fee Extension
 * Mapping for the Extensible Provisioning Protocol (EPP)</a>
 */
public class DomainCheckFeeCommandExtension implements CommandExtension {
    private static final String FIELD_IDENTIFIER = "<<field>>";
    private Map<String, FeeCheckData> feeCheckDomains = new LinkedHashMap<String, FeeCheckData>();


    @Override
    public void addToCommand(Command command) {

        if (!feeCheckDomains.isEmpty()) {
            final XMLWriter xmlWriter = command.getXmlWriter();
            final Element extensionElement = command.getExtensionElement();
            final Element checkElement = xmlWriter.appendChild(extensionElement, "check",
                    ExtendedObjectType.FEE.getURI());

            for (Map.Entry<String, FeeCheckData> feeCheckDomain: feeCheckDomains.entrySet()) {
                FeeCheckData feeCheckData = feeCheckDomain.getValue();
                final Element domainElement = xmlWriter.appendChild(checkElement, "domain");
                xmlWriter.appendChild(domainElement, "name").setTextContent(feeCheckData.getName());
                if (feeCheckData.getCurrency() != null) {
                    xmlWriter.appendChild(domainElement, "currency").setTextContent(feeCheckData.getCurrency());
                }
                final Element commandElement = xmlWriter.appendChild(domainElement, "command");
                commandElement.setTextContent(feeCheckData.getCommand().getName());
                if (feeCheckData.getCommand().getPhase() != null) {
                    commandElement.setAttribute("phase", feeCheckData.getCommand().getPhase());
                }
                if (feeCheckData.getCommand().getSubphase() != null) {
                    commandElement.setAttribute("subphase", feeCheckData.getCommand().getSubphase());
                }
                if (feeCheckData.getPeriod() != null) {
                    Element periodElement = xmlWriter.appendChild(domainElement, "period");
                    periodElement.setTextContent(String.valueOf(feeCheckData.getPeriod().getPeriod()));
                    periodElement.setAttribute("unit", feeCheckData.getPeriod().getUnit().toString());
                }
            }
        }
    }

    public void addFeeCheckDomain(FeeCheckData feeCheckDomain) {
        if (feeCheckDomain != null) {
            if (feeCheckDomain.getName() == null) {
                throw new IllegalArgumentException(ErrorPkg.getMessage("se.ar.domain.chcek.fee", FIELD_IDENTIFIER,
                        "name"));
            }
            if (feeCheckDomain.getCommand() == null || feeCheckDomain.getCommand().getName() == null) {
                throw new IllegalArgumentException(ErrorPkg.getMessage("se.ar.domain.chcek.fee",
                        FIELD_IDENTIFIER, "command"));
            }
            feeCheckDomains.put(feeCheckDomain.getName(), feeCheckDomain);
        }
    }
}