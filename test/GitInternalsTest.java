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

    private final String gitOnePath = "Git Internals/task/test/git-internals-test-files/stage1/gitone/objects/";

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
            System.err.println("Failed to clone the test repository. Please check your internet connection and the repository URI.");
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
                                gitOnePath + "cd/0875583aabe89ee197ea133980a9085d08e497\n")
                        .setAttach(Arrays.asList(
                                "Enter git object location:",
                                "blob 13",
                                "Hello world!")),
                new TestCase<List<String>>()
                        .setInput(
                                gitOnePath + "06/fcdd77c9348567c50638b30d406500f521c304\n")
                        .setAttach(Arrays.asList(
                                "Enter git object location:",
                                "blob 23",
                                "first line",
                                "second line")),
                new TestCase<List<String>>()
                        .setInput(
                                gitOnePath + "20/aeba2bad864cf6904f9caaea55f46f03ce6ac1\n")
                        .setAttach(Arrays.asList(
                                "Enter git object location:",
                                "blob 34",
                                "first line",
                                "second line",
                                "third line"))
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
