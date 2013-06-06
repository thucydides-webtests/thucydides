package net.thucydides.core.reports.adaptors;

import net.thucydides.core.model.TestOutcome;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface TestOutcomeAdaptor {
    public List<TestOutcome> loadOutcomesFrom(final File directory) throws IOException;
}
