import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final List<String> BUILTINS =
            Arrays.asList("echo", "exit", "type", "pwd", "cd");

    public static void main(String[] args) throws IOException {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("$ ");
            System.out.flush();

            String line = reader.readLine();
            if (line == null) break;

            line = line.trim();
            if (line.isEmpty()) continue;

            ArrayList<String> parts = parseInput(line);
            if (parts.isEmpty()) continue;

            String command = parts.get(0);

            if (command.equals("exit")) {
                break;
            }

            if (BUILTINS.contains(command)) {
                runBuiltin(parts);
            } else {
                runExternal(parts);
            }
        }
    }

    private static void runBuiltin(ArrayList<String> parts) {
        String command = parts.get(0);

        if (command.equals("pwd")) {
            System.out.println(System.getProperty("user.dir"));
            return;
        }

        if (command.equals("cd")) {
            if (parts.size() < 2) {
                System.out.println("cd: missing operand");
                return;
            }

            String target = parts.get(1);
            File newDir;

            if (target.equals("~")) {
                newDir = new File(System.getProperty("user.home"));
            } else if (target.startsWith("~/")) {
                newDir = new File(System.getProperty("user.home"), target.substring(2));
            } else if (target.startsWith("/")) {
                newDir = new File(target);
            } else {
                newDir = new File(System.getProperty("user.dir"), target);
            }

            try {
                newDir = new File(newDir.getCanonicalPath());
            } catch (IOException e) {
                System.out.println("cd: no such file or directory: " + target);
                return;
            }

            if (!newDir.exists()) {
                System.out.println("cd: no such file or directory: " + target);
                return;
            }

            if (!newDir.isDirectory()) {
                System.out.println("cd: not a directory: " + target);
                return;
            }

            System.setProperty("user.dir", newDir.getAbsolutePath());
            return;
        }

        if (command.equals("echo")) {
            for (int i = 1; i < parts.size(); i++) {
                if (i > 1) System.out.print(" ");
                System.out.print(parts.get(i));
            }
            System.out.println();
            return;
        }

        if (command.equals("type")) {
            if (parts.size() < 2) {
                System.out.println("type: missing operand");
                return;
            }

            String target = parts.get(1);

            if (BUILTINS.contains(target)) {
                System.out.println(target + " is a shell builtin");
                return;
            }

            File executable = findExecutable(target);
            if (executable == null) {
                System.out.println(target + ": not found");
            } else {
                System.out.println(target + " is " + executable.getAbsolutePath());
            }
        }
    }

    private static void runExternal(ArrayList<String> parts) {
        String command = parts.get(0);
        File executable = resolveExecutable(command);

        if (executable == null) {
            System.out.println(command + ": command not found");
            return;
        }

        ArrayList<String> commandParts = new ArrayList<>(parts);
        commandParts.set(0, executable.getAbsolutePath());

        ProcessBuilder pb = new ProcessBuilder(commandParts);
        pb.directory(new File(System.getProperty("user.dir")));
        pb.inheritIO();

        try {
            pb.start().waitFor();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static File resolveExecutable(String command) {
        File directFile = new File(command);

        if (directFile.isAbsolute() || command.contains(File.separator)) {
            File file = directFile.isAbsolute()
                    ? directFile
                    : new File(System.getProperty("user.dir"), command);

            if (file.exists() && file.isFile() && file.canExecute()) {
                return file;
            }
            return null;
        }

        return findExecutable(command);
    }

    private static File findExecutable(String command) {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null) return null;

        String[] dirs = pathEnv.split(File.pathSeparator);
        for (String dir : dirs) {
            File file = new File(dir, command);
            if (file.exists() && file.isFile() && file.canExecute()) {
                return file;
            }
        }

        return null;
    }

    private static ArrayList<String> parseInput(String line) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\\' && !inSingleQuote) {
                if (inDoubleQuote) {
                    if (i + 1 < line.length()) {
                        char next = line.charAt(i + 1);
                        if (next == '"' || next == '\\' || next == '$' || next == '`') {
                            current.append(next);
                            i++;
                        } else {
                            current.append(c);
                        }
                    } else {
                        current.append(c);
                    }
                } else if (i + 1 < line.length()) {
                    current.append(line.charAt(i + 1));
                    i++;
                } else {
                    current.append(c);
                }
                continue;
            }

            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
                continue;
            }

            if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
                continue;
            }

            if (Character.isWhitespace(c) && !inSingleQuote && !inDoubleQuote) {
                if (current.length() > 0) {
                    result.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }

            current.append(c);
        }

        if (current.length() > 0) {
            result.add(current.toString());
        }

        return result;
    }
}
