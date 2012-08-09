package net.thucydides.core.reports.html.history;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rahul
 * Date: 7/14/12
 * Time: 7:47 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TestResultSnapshotDAO {
    void saveSnapshot(TestResultSnapshot testResultSnapshot);

    List<TestResultSnapshot> findAll();

    void clearAll();
}
