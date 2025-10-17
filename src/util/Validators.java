package util;

public final class Validators {
    private Validators() {}

    public static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }

    public static char parseGender(String s) {
        require(s != null && !s.isBlank(), "Gender required (M/F).");
        char g = Character.toUpperCase(s.trim().charAt(0));
        require(g == 'M' || g == 'F', "Gender must be M or F.");
        return g;
    }

    public static int parseInt(String s, String field) {
        require(s != null && !s.isBlank(), field + " required.");
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(field + " must be a number."); }
    }

    public static java.time.LocalTime parseTime(String s) {
        require(s != null && !s.isBlank(), "Time required (HH:mm).");
        try { return java.time.LocalTime.parse(s.trim()); }
        catch (Exception e) { throw new IllegalArgumentException("Time format must be HH:mm"); }
    }
}
