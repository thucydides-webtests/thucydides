package net.thucydides.core.reports.adaptors;

import net.thucydides.core.model.TestOutcome;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Test outcome adaptors provide a way to read test results from an external source.
 * In the most common situation, this data is loaded from a source file or directory,
 *
 * In situations where no source file or directory is required, this parameter can be
 * ignored.
 *
 */
public interface TestOutcomeAdaptor {
    public List<TestOutcome> loadOutcomesFrom(final File source) throws IOException;
}
