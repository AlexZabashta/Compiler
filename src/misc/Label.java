package misc;

public class Label {
    private static int text = 0, data = 0;

    public static String getTextLabel() {
        return "action" + (++text);
    }

    public static String getDataLabel() {
        return "value" + (++data);
    }

}
