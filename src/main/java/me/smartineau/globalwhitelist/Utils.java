package me.smartineau.globalwhitelist;

abstract public class Utils {
    public static String applyColor(String message) {
        // Based on https://pastebin.com/BADd6K43
        boolean lastIsCode = false;
        String lastFormatCode = null;
        String lastColourCode = null;

        StringBuilder colouredMessage = new StringBuilder();

        for (char c : message.toCharArray()) {
            System.out.println(lastIsCode);
            System.out.println(lastFormatCode);
            System.out.println(lastColourCode);
            System.out.println(c);
            if (c == '§') lastIsCode = true;
            else if (lastIsCode) switch (c) {
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                    lastIsCode = false;
                    lastFormatCode = String.valueOf(c);
                    break;
                case 'r':
                    lastIsCode = false;
                    lastFormatCode = null;
                    lastColourCode = null;
                default:
                    lastIsCode = false;
                    lastColourCode = String.valueOf(c);
                    break;
            }
            else
                colouredMessage.append(lastColourCode != null ? "§" + lastColourCode : "").append(lastFormatCode != null ? "§" + lastFormatCode : "").append(c);
        }
        return colouredMessage.toString();
    }
}
