import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        Map<String, String> idMap = formatAndGetClasses();

        writeChanges(idMap);
    }

    private static void writeChanges(Map<String, String> idMap) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(new File("src/temp.txt"));
            FileWriter fileWriter = new FileWriter(new File("src/result.txt"));
            int i;
            char c;
            int nullIdx = 0;
            while ((i = fileReader.read()) != -1) {
                c = (char) i;
                if (c == '\n') {
                    String line = stringBuilder.toString();
                    if (line.contains("text") || line.contains("rect")) {

                        boolean isRect = line.contains("rect");

                        String id = getId(stringBuilder, isRect);
                        int idx = line.indexOf("id=") + 4;
                        int val = 0;
                        if (!isRect) val = 1;

                        String tag = idMap.get(id);
                        String clickTag;
                        if (idMap.get(id) == null) {
                            tag = "null" + nullIdx;
                            nullIdx++;
                        } else
                            tag = "classroom_" + tag;
                        clickTag = tag;
                        if (!isRect) clickTag = tag + "_text";


                        stringBuilder.replace(idx, idx + id.length() + val, tag);

                        int offset = stringBuilder.toString().indexOf(">");
                        if (isRect) offset = offset - 1;
                        stringBuilder.insert(offset, " (click)=\"pageService.onClick('" + clickTag + "')\"");
                    }

                    fileWriter.write(stringBuilder.toString() + "\n");
                    stringBuilder.delete(0, stringBuilder.length());
                } else {
                    stringBuilder.append(c);
                }
            }
            fileWriter.close();
            fileReader.close();
        } catch (IOException exception) {
            System.out.println("File does not exits!!!");
        }
    }

    private static Map<String, String> formatAndGetClasses() {
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, String> idMap = new HashMap<>();
        try {
            FileReader fileReader = new FileReader(new File("src/template.html"));
            FileWriter fileWriter = new FileWriter(new File("src/temp.txt"));
            int i;
            char c;
            boolean opened = false;
            boolean lastWasSpace = false;
            while ((i = fileReader.read()) != -1) {
                c = (char) i;

                opened = isOpened(c, opened);
                if (c == '\n' && opened) continue;

                if (c == ' ' && lastWasSpace) continue;
                lastWasSpace = lastWasSpace(c, lastWasSpace);

                if (c == '\n') {
                    if (stringBuilder.toString().contains("text")) {
                        getClass(stringBuilder, idMap);
                    }
                    fileWriter.write(stringBuilder.toString() + "\n");
                    System.out.println(stringBuilder.toString());
                    stringBuilder.delete(0, stringBuilder.length());
                } else
                    stringBuilder.append(c);
            }
            fileWriter.close();
            fileReader.close();
        } catch (IOException exception) {
            System.out.println("File does not exits!!!");
        }
        return idMap;
    }

    private static void getClass(StringBuilder stringBuilder, Map<String, String> idMap) {
        int idx = stringBuilder.indexOf(">") + 1;
        StringBuilder n = new StringBuilder();
        while (stringBuilder.charAt(idx) != '<') {
            n.append(stringBuilder.charAt(idx));
            idx++;
        }
        String id = getId(stringBuilder, false);

        idMap.put(id, n.toString());
    }

    private static String getId(StringBuilder stringBuilder, boolean isRect) {
        int idx;
        idx = stringBuilder.indexOf("id=") + 4;
        StringBuilder id = new StringBuilder();

        int val;
        if (isRect)
            val = 0;
        else
            val = 1;

        while (stringBuilder.charAt(idx + val) != '"') {
            id.append(stringBuilder.charAt(idx));
            idx++;
        }
        return id.toString();
    }

    private static boolean lastWasSpace(char c, boolean lastWasSpace) {
        if (c != ' ' && lastWasSpace) return false;
        return c == ' ';
    }

    private static boolean isOpened(char c, boolean opened) {
        if (c == '<')
            opened = true;

        if (c == '>')
            opened = false;
        return opened;
    }
}
