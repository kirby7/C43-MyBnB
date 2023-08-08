package Services;

public class CommandService {
    public static enum Command {
        NULL,
        SETUP,
        EXIT,
        HELP,
        REGISTER,
        ACCOUNT,
        LOGIN,
        LOGOUT,
        HOSTING,
        BOOK
    }

    public static Command commandLookup(String s) {
        String arg = s.toLowerCase();
        if (arg.equals("setup"))
            return Command.SETUP;
        if (arg.equals("quit") || arg.equals("exit"))
            return Command.EXIT;
        if (arg.equals("help"))
            return Command.HELP;
        if (arg.equals("register"))
            return Command.REGISTER;
        if (arg.equals("login"))
            return Command.LOGIN;
        if (arg.equals("account"))
            return Command.ACCOUNT;
        if (arg.equals("logout"))
            return Command.LOGOUT;
        if (arg.equals("hosting"))
            return Command.HOSTING;
        if (arg.equals("book"))
            return Command.BOOK;
        return Command.NULL;
    }
}
