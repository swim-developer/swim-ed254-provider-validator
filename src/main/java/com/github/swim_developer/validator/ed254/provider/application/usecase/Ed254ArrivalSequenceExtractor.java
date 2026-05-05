package com.github.swim_developer.validator.ed254.provider.application.usecase;

import com.github.swim_developer.validator.ed254.provider.domain.model.ArrivalSequenceEvent;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class Ed254ArrivalSequenceExtractor {

    private static final Logger LOG = Logger.getLogger(Ed254ArrivalSequenceExtractor.class);
    private static final Pattern AERODROME_PATTERN = Pattern.compile("<[^:]*:?aerodromeDesignator>([A-Z]{4})</");
    private static final Pattern ARCID_PATTERN = Pattern.compile("<[^:]*:?arcid>([^<]+)</");
    private static final Pattern PUBLICATION_TIME_PATTERN = Pattern.compile("<[^:]*:?publicationTime>([^<]+)</");

    public ArrivalSequenceEvent extract(String xmlBody) {
        if (xmlBody == null || xmlBody.isBlank()) {
            return new ArrivalSequenceEvent("UNKNOWN", "UNKNOWN", 0, "", "");
        }

        try {
            String messageType = detectMessageType(xmlBody);
            String aerodrome = extractFirst(AERODROME_PATTERN, xmlBody, "UNKNOWN");
            String publicationTime = extractFirst(PUBLICATION_TIME_PATTERN, xmlBody, "");

            List<String> callsignList = new ArrayList<>();
            Matcher arcidMatcher = ARCID_PATTERN.matcher(xmlBody);
            while (arcidMatcher.find()) {
                callsignList.add(arcidMatcher.group(1));
            }

            return new ArrivalSequenceEvent(
                aerodrome,
                messageType,
                callsignList.size(),
                String.join(", ", callsignList),
                publicationTime
            );
        } catch (Exception e) {
            LOG.warnf("Failed to extract arrival sequence data: %s", e.getMessage());
            return new ArrivalSequenceEvent("UNKNOWN", "UNKNOWN", 0, "", "");
        }
    }

    private String detectMessageType(String xml) {
        if (xml.contains("arrivalSequence") || xml.contains("ArrivalSequence")) {
            return "ARRIVAL_SEQUENCE";
        } else if (xml.contains("providerException") || xml.contains("ProviderException")) {
            return "PROVIDER_EXCEPTION";
        }
        return "UNKNOWN";
    }

    private String extractFirst(Pattern pattern, String input, String defaultValue) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.group(1) : defaultValue;
    }
}
