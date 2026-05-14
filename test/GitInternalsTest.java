import org.eclipse.jgit.api.Git;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

// version 1.2
public class GitInternalsTest extends StageTest<List<String>> {

    private final String gitOnePath = "Git Internals/task/test/git-internals-test-files/stage2/gitone/\n";


    @BeforeClass
    public static void setup() {
        File repoDir = new File("Git Internals/task/test/git-internals-test-files");
        if (repoDir.exists()) {
            deleteDirectory(repoDir);
        }
        try {
            Git.cloneRepository()
                    .setURI("https://github.com/hyperskill-content/git-internals-test-files.git")
                    .setDirectory(repoDir)
                    .call();
        } catch (Exception e) {
            System.err.println("Failed to clone the test repository. Please check your internet connection. For other issues, report this to support.");
            e.printStackTrace();
        }
    }


    @AfterClass
    public static void tearDown() {
        deleteDirectory(new File("Git Internals/task/test/git-internals-test-files"));
    }

    private static void deleteDirectory(File file) {
        if (file.exists()) {
            try {
                Files.walk(file.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<TestCase<List<String>>> generate() {

        return Arrays.asList(
                new TestCase<List<String>>()
                        .setInput(
                                gitOnePath +
                                        "0eee6a98471a350b2c2316313114185ecaf82f0e\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "type:commit length:216")),
                new TestCase<List<String>>()
                        .setInput(
                                gitOnePath +
                                        "490f96725348e92770d3c6bab9ec532564b7ebe0\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "type:blob length:85")),
                new TestCase<List<String>>()
                        .setInput(
                                gitOnePath +
                                        "618383db6d7ee3bd2e97b871205f113b6a3ba854\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "type:blob length:14")),
                new TestCase<List<String>>()
                        .setInput(
                                gitOnePath +
                                        "a7b882bbf2db5d90287e9affc7e6f3b3c740b327\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "type:tree length:35")),
                new TestCase<List<String>>()
                        .setInput(
                                gitOnePath +
                                        "fb043556c251cb450a0d55e4ceb1ff35e12029c3\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "type:tree length:73")),
                new TestCase<List<String>>()
                        .setInput(
                                gitOnePath +
                                        "ad3a818dc87b9940935b24a5aa93fac00f086bf9\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "type:tree length:35"))
        );
    }

    @Override
    public CheckResult check(String reply, List<String> expectedOutput) {
        List<String> lines = Arrays.asList(reply.split("(\\r\\n|\\r|\\n)"));

        if (lines.size() != expectedOutput.size()) {
            return CheckResult.wrong(String.format(
                    "Number of lines in your output (%d) does not match expected value(%d)",
                    lines.size(), expectedOutput.size()));
        }

        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).equals(expectedOutput.get(i))) {
                return CheckResult.wrong(String.format(
                        "Output text at line (%d) (%s) does not match expected (%s)",
                        i, lines.get(i), expectedOutput.get(i)));
            }
        }


        return CheckResult.correct();
    }
}
