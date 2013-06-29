package net.thucydides.core.requirements;

import java.util.Comparator;

public class PlaceFileSystemRequirementsFirst implements Comparator<RequirementsTagProvider> {
    public int compare(RequirementsTagProvider firstRequirementsTagProvider, RequirementsTagProvider secondRequirementsTagProvider) {
        if ((firstRequirementsTagProvider instanceof FileSystemRequirementsTagProvider) && (secondRequirementsTagProvider instanceof FileSystemRequirementsTagProvider)) {
            return firstRequirementsTagProvider.getClass().getName().compareTo(secondRequirementsTagProvider.getClass().getName());
        }
        if (firstRequirementsTagProvider instanceof FileSystemRequirementsTagProvider) {
            return -1;
        }
        if (secondRequirementsTagProvider instanceof FileSystemRequirementsTagProvider) {
            return 1;
        }
        return firstRequirementsTagProvider.getClass().getName().compareTo(secondRequirementsTagProvider.getClass().getName());
    }
}
