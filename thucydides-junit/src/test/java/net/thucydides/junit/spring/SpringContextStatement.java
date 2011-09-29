package net.thucydides.junit.spring;

import org.junit.runners.model.Statement;

final class SpringContextStatement extends Statement {

    final Statement base;

    SpringContextStatement(Statement base) {
        this.base = base;
    }

    @Override
    public void evaluate() throws Throwable {
        base.evaluate();
    }
}
