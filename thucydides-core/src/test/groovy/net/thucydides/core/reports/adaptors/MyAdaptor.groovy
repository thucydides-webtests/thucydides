package net.thucydides.core.reports.adaptors

import net.thucydides.core.model.TestOutcome


class MyAdaptor implements TestOutcomeAdaptor {
    @Override
    List<TestOutcome> loadOutcomesFrom(File source) throws IOException {
        return null
    }
}
