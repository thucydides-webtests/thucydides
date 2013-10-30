package net.thucydides.core.requirements;

import net.thucydides.core.model.Release;

import java.util.List;

/**
 * This interface is used to implement plugins that provide a complete list of the known releases.
 * It should be used to extend a class that implements the RequirementsTagProvider interface.
 *
 */
public interface ReleaseProvider {
    /**
     * Return a full tree-structure of known releases.
     */
    List<Release> getReleases();

    /**
     * Is this provider currently activated
     * Some release providers can be deactivated via system properties.
     */
    boolean isActive();
}
