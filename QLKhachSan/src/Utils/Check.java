package Utils;

public class Check {
    public static <T> T getValueOrNull(T value) {
        if (value == null) return null;

        if (value instanceof String) {
            String s = ((String) value).trim();
            return s.isEmpty() ? null : (T) s;
        }

        return value;
    }
}
