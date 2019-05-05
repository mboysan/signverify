package ee.mboysan.signverify;

import ee.mboysan.signverify.hashing.IHash;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import ee.mboysan.signverify.ops.SignVerify;
import ee.mboysan.signverify.tree.HashTree;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HashCLI {

    public static void main(String[] args) throws Exception {
        ArgumentParser parser = ArgumentParsers.newFor("HashCLI").build();
        parser.addArgument("-opmod", "--operation-mode")
                .dest("opmod")
                .choices("MEM", "CPU").setDefault("MEM")
                .help("Specify operation mode to use. \n" +
                        "MEM: Memory intensive construction of the hash tree.\n" +
                        "CPU: CPU intensive construction of the hash tree.");
        Subparsers subparsers = parser.addSubparsers()
                .title("subcommands")
                .description("valid subcommands");

        Map<String, AbsCmd> commandMap = Stream.of(
                new SignCmd(subparsers),
                new VerifyCmd(subparsers),
                new ExtractHashChainCmd(subparsers),
                new VisualizeCmd(subparsers)
        ).collect(Collectors.toMap(o -> o.commandName, Function.identity()));

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);

            HashTree.OPERATION_MODE = HashTree.OperationMode.valueOf(ns.getString("opmod"));

            String cmdStr = Arrays.stream(args).filter(s -> commandMap.keySet().contains(s)).findFirst().get();
            commandMap.get(cmdStr).process(ns);

            System.out.println();
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

    private static abstract class AbsCmd {
        String commandName;
        AbsCmd(Subparsers subparsers, String commandName){
            this.commandName = commandName;
        }
        abstract void process(Namespace ns) throws Exception;
    }

    private static class SignCmd extends AbsCmd {
        SignCmd(Subparsers subparsers) {
            super(subparsers, "sign");
            ArgumentParser parser = subparsers.addParser("sign")
                    .defaultHelp(true)
                    .description("Sign a log file.");

            parser.addArgument("fileToSign").nargs(1)
                    .type(File.class)
                    .help("File to sign");
            parser.addArgument("signatureFile").nargs(1)
                    .type(File.class)
                    .help("Signature file output");

            parser.addArgument("-ha", "--hash-algorithm")
                    .dest("ha")
                    .choices("SHA-256", "MD5", "SHA1").setDefault("SHA-256")
                    .help("Specify hash algorithm to use");
            parser.addArgument("-aa", "--allow-append")
                    .dest("aa")
                    .setDefault(false)
                    .type(Boolean.class)
                    .help("Allow append to log file.");
        }

        @Override
        void process(Namespace ns) throws Exception {
            File fileToSign = (ns.<List<File>>get("fileToSign")).get(0);
            File signatureFile = (ns.<List<File>>get("signatureFile")).get(0);

            String hashAlg = ns.getString("ha");
            boolean allowAppend = ns.get("aa");

            new SignVerify().sign(fileToSign, signatureFile, allowAppend ,hashAlg);
        }
    }

    private static class VerifyCmd extends AbsCmd {
        VerifyCmd(Subparsers subparsers) {
            super(subparsers, "verify");
            ArgumentParser parser = subparsers.addParser("verify")
                    .defaultHelp(true)
                    .description("Verify a log file");
            parser.addArgument("fileToVerify").nargs(1)
                    .type(File.class)
                    .help("File to verify");
            parser.addArgument("signatureFile").nargs(1)
                    .type(File.class)
                    .help("Signature file for verification");
        }

        @Override
        void process(Namespace ns) throws Exception {
            File fileToVerify = (ns.<List<File>>get("fileToVerify")).get(0);
            File signatureFile = (ns.<List<File>>get("signatureFile")).get(0);

            boolean verif = new SignVerify().verify(signatureFile, fileToVerify);
            System.out.println("Verification " + (verif ? "successful" : "failed"));
        }
    }

    private static class ExtractHashChainCmd extends AbsCmd {
        ExtractHashChainCmd(Subparsers subparsers) {
            super(subparsers, "hashchain");
            ArgumentParser parser = subparsers.addParser("hashchain")
                    .defaultHelp(true)
                    .description("Extract hash chain from a given log file and input (event)");

            parser.addArgument("logFile").nargs(1)
                    .type(File.class)
                    .help("File to extract hash chain");
            parser.addArgument("event").nargs(1)
                    .help("Input/event string to search for.\nexample: \"event to test\"");

            parser.addArgument("-ha", "--hash-algorithm")
                    .dest("ha")
                    .choices("SHA-256", "MD5", "SHA1").setDefault("SHA-256")
                    .help("Specify hash algorithm to use");
            parser.addArgument("-out", "--out-file")
                    .dest("out")
                    .type(File.class)
                    .setDefault((Object) null)
                    .help("Output file to write the hash chain.");
        }

        @Override
        void process(Namespace ns) throws Exception {
            File file = (ns.<List<File>>get("logFile")).get(0);
            String event = (ns.<List<String>>get("event")).get(0);
            File outFile = ns.get("out");
            String hashAlg = ns.getString("ha");

            List<IHash> hashChain = new SignVerify().hashChainForEvent(file, outFile, event, hashAlg);

            System.out.println(hashChain);
            if (outFile != null) {
                System.out.println("output written to: " + outFile.toString());
            }
        }
    }

    private static class VisualizeCmd extends AbsCmd {
        VisualizeCmd(Subparsers subparsers) {
            super(subparsers, "visualize");
            ArgumentParser parser = subparsers.addParser("visualize")
                    .defaultHelp(true)
                    .description("Visualize the hash tree of a given log file.\n" +
                            "Disclaimer: This feature is currently not stable!");

            parser.addArgument("logFile").nargs(1)
                    .type(File.class)
                    .help("File to visualize hash tree.");

            parser.addArgument("-ha", "--hash-algorithm")
                    .dest("ha")
                    .choices("SHA-256", "MD5", "SHA1").setDefault("SHA-256")
                    .help("Specify hash algorithm to use");
            parser.addArgument("-out", "--out-file")
                    .dest("out")
                    .type(File.class)
                    .setDefault((Object) null)
                    .help("Output file to write the visualized hash tree.");
            parser.addArgument("-hl", "--hash-length")
                    .dest("hl")
                    .type(Integer.class)
                    .setDefault(-1)
                    .help("String length of a hash.");
        }

        @Override
        void process(Namespace ns) throws Exception {
            File file = (ns.<List<File>>get("logFile")).get(0);
            String hashAlg = ns.getString("ha");
            File outFile = ns.get("out");
            int hashLength = ns.get("hl");

            String visual = new SignVerify().visualizeHashMap(file, outFile, hashAlg, hashLength);

            System.out.println(visual);
            if (outFile != null) {
                System.out.println("output written to: " + visual);
            }
        }
    }
}