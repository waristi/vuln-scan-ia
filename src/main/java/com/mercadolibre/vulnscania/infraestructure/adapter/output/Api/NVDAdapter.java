package com.mercadolibre.vulnscania.infraestructure.adapter.output.Api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.vulnscania.domain.model.AffectedArtifact;
import com.mercadolibre.vulnscania.domain.port.output.VulnerabilityCatalogPort;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class NVDAdapter implements VulnerabilityCatalogPort {
    private final WebClient nvdWebClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public NVDAdapter(WebClient nvdWebClient) {
        this.nvdWebClient = nvdWebClient;
    }

    @Override
    public List<AffectedArtifact> getAffectedArtifactsByCve(String cveId) {
        String json = nvdWebClient.get()
                .uri(uri -> uri.queryParam("cveId", cveId).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode root = mapper.readTree(json);
            List<AffectedArtifact> out = new ArrayList<>();
            JsonNode vulns = root.path("vulnerabilities");
            if (vulns.isArray()) {
                for (JsonNode v : vulns) {
                    JsonNode cve = v.path("cve");
                    JsonNode configurations = cve.path("configurations");
                    if (configurations.isArray()) {
                        for (JsonNode conf : configurations) {
                            JsonNode nodes = conf.path("nodes");
                            collectFromNodes(nodes, out);
                        }
                    }
                }
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing NVD response", e);
        }
    }

    private void collectFromNodes(JsonNode nodes, List<AffectedArtifact> out) {
        if (nodes == null || !nodes.isArray()) return;
        for (JsonNode n : nodes) {
            JsonNode cpeMatch = n.path("cpeMatch");
            if (cpeMatch.isArray()) {
                for (JsonNode c : cpeMatch) {
                    String criteria = text(c, "criteria");
                    boolean vulnerable = c.path("vulnerable").asBoolean(false);
                    String vStartInc = text(c, "versionStartIncluding");
                    String vStartExc = text(c, "versionStartExcluding");
                    String vEndInc = text(c, "versionEndIncluding");
                    String vEndExc = text(c, "versionEndExcluding");

                    Cpe23Parser.CpeParts parts = Cpe23Parser.parse(criteria);

                    out.add(new AffectedArtifact(
                            criteria,
                            parts.part(),
                            parts.vendor(),
                            parts.product(),
                            parts.version(),
                            vStartInc,
                            vStartExc,
                            vEndInc,
                            vEndExc,
                            vulnerable
                    ));
                }
            }

            JsonNode children = n.path("children");
            if (children.isArray() && children.size() > 0) {
                collectFromNodes(children, out);
            }
        }
    }

    private String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v != null && !v.isNull()) ? v.asText() : null;
    }
}
