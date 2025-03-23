package ui;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;


public class PreloginUI {

    private ServerFacade serverFacade;
    private boolean signedIn = false;

    public PreloginUI(String url) {
        serverFacade = new ServerFacade(url);
    }

    public void run() {
        System.out.println("Good Morning User!\n♟Time to play chess. Sign in to start.♟");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if (signedIn) {
                result = new PostloginUI(serverFacade).run();
                signedIn = false;
                continue;
            }
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    private String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                case "clear" -> clear();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String register(String... params) throws ResponseException {
        if (params.length < 3) {
            throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
        }

        serverFacade.register(params[0], params[1], params[2]);

        signedIn = true;
        return String.format("You logged in as %s.\n", params[0]);
    }

    private String login(String... params) throws ResponseException {
        if (params.length < 2) {
            throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
        }

        serverFacade.login(params[0], params[1]);

        signedIn = true;
        return String.format("You logged in as %s.\n", params[0]);
    }

    private String help() {
        return (SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL> " + SET_TEXT_COLOR_MAGENTA + "- to create an account\n" +
                SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD> " + SET_TEXT_COLOR_MAGENTA + "- to play chess\n" +
                SET_TEXT_COLOR_BLUE + "quit " + SET_TEXT_COLOR_MAGENTA + "- playing chess\n" +
                SET_TEXT_COLOR_BLUE + "help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands\n");

    }

    private String clear() throws ResponseException {
        serverFacade.clear();
        return "";
    }

}
