package kps.util;

/**
 * Defines the priority of a piece of mail in the KPS system.
 */
public enum MailPriority {
    INTERNATIONAL_AIR,
    INTERNATIONAL_STANDARD,
    DOMESTIC_AIR,
    DOMESTIC_STANDARD;

    public static MailPriority fromString(String s) {
    	s = s.toLowerCase();
        switch (s) {
            case "international air":
                return INTERNATIONAL_AIR;
            case "international standard":
                return INTERNATIONAL_STANDARD;
            case "domestic air":
                return DOMESTIC_AIR;
            case "domestic standard":
                return DOMESTIC_STANDARD;
            default:
                throw new IllegalArgumentException("Invalid mail priority");
        }
    }
}
