package com.mercadolibre.vulnscania.infraestructure.adapter.output.Api;

class Cpe23Parser {
    static CpeParts parse(String cpe23uri) {
        if (cpe23uri == null || !cpe23uri.startsWith("cpe:2.3:")) {
            return new CpeParts(null, null, null, null);
        }
        String[] parts = cpe23uri.split(":");
        // índices: 0 cpe,1 2.3,2 part,3 vendor,4 product,5 version
        String part = parts.length > 2 ? nullIfStar(parts[2]) : null;
        String vendor = parts.length > 3 ? nullIfStar(parts[3]) : null;
        String product = parts.length > 4 ? nullIfStar(parts[4]) : null;
        String version = parts.length > 5 ? nullIfStar(parts[5]) : null;
        return new CpeParts(part, vendor, product, version);
    }

    private static String nullIfStar(String s) {
        return (s == null || "*".equals(s) || "-".equals(s)) ? null : s;
    }

    static record CpeParts(String part, String vendor, String product, String version) {}
}