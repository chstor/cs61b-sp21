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
                break;
            case "rm":
                fileName = args[1];
                Repository.rm(fileName);
                break;
            case "log":
                Repository.log();
                break;
            case "global-log":
                Repository.globalLog();
                break;
            case "find":
                message = args[1];
                Repository.find(message);
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                if(args.length == 2) {
                    String branchName = args[1];
                    Repository.checkoutBranch(branchName);
                }else if(args.length == 3) {
                    fileName = args[2];
                    Repository.checkoutFile(fileName);
                }else{
                    String commitId = args[1];
                    if(!args[2].equals("--")){
                        System.out.println("Incorrect operands.");
                        return;
                    }
                    fileName = args[3];
                    Repository.checkoutFileByCommitId(commitId,fileName);
                }
                break;
            case "branch":
                String branchName = args[1];
                Repository.createBranch(branchName);
                break;
            case "rm-branch":
                branchName = args[1];
                Repository.rmBranch(branchName);
                break;
            case "reset":
                String commitId = args[1];
                Repository.resetByCommitId(commitId);
                break;
            case "merge":
                branchName = args[1];
                Repository.merge(branchName);
                break;
            default:
                Utils.exitWithError(String.format("Unknown command: %s", args[0]));
        }
        return;
    }
}
