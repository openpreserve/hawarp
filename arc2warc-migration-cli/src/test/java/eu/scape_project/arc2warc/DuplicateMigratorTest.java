package eu.scape_project.arc2warc;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import eu.scape_project.arc2warc.cli.Arc2WarcMigrationConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;

public class DuplicateMigratorTest {

    private static final Log LOG = LogFactory.getLog(ArcMigratorTest.class);
    protected static final Charset CHARSET = Charset.forName("UTF-8");
    private File tempDir;

    public DuplicateMigratorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        tempDir = Files.createTempDir();
        LOG.info("Temporary directory: " + tempDir);
    }


    @Test
    public void testWarcCreator() throws Exception {
        String warcFileName = "example.warc";
        File arcFile = new File(Resources.getResource("arc-dedup/2-metadata-1.arc").toURI());
        File tmpWarcFile = new File(tempDir.getAbsolutePath() + "/" + warcFileName);
        Arc2WarcMigrationConfig conf = new Arc2WarcMigrationConfig();
        DuplicateMigrator warcCreator = new DuplicateMigrator(conf, arcFile, tmpWarcFile);
        warcCreator.migrate();
        //ArcMigratorTest.validateWarcFile(tmpWarcFile);
        System.out.println(FileUtils.readFileToString(tmpWarcFile));
    }
}