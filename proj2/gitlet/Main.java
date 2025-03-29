package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            Utils.exitWithError("Must have at least one argument");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                String fileName = args[1];
                Repository.add(fileName);
                break;
            case "commit":
                String message = args[1];
                Repository.commit(message);
            case "rm":
                fileName = args[1];
                Repository.rm(fileName);
            default:
                Utils.exitWithError(String.format("Unknown command: %s", args[0]));
        }
        return;
    }
}
