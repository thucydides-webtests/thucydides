package net.thucydides.core.requirements;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.thucydides.core.annotations.Narrative;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A requirements Provider that read requirement from class or package annotation.
 *
 * A class or package needs to be annotated with {@link net.thucydides.core.annotations.Narrative}
 * to be a requirement. All package above the class or package will also be considered requirement.
 * The default root package is "stories", but can be change using {@link net.thucydides.core.ThucydidesSystemProperty#ANNOTATED_REQUIREMENTS_DIRECTORY}
 * It is recommanded to change the root package if the {@link net.thucydides.core.requirements.FileSystemRequirementsTagProvider} is used.
 *
 * @see net.thucydides.core.annotations.Narrative
 * @see net.thucydides.core.ThucydidesSystemProperty#ANNOTATED_REQUIREMENTS_DIRECTORY
 */
public class AnnotationBasedTagProvider extends AbstractRequirementsTagProvider implements RequirementsTagProvider {
    private static final String DOT_REGEX = "\\.";

    private List<Requirement> requirements;
    private final Configuration configuration;
    private final RequirementPersister persister;
    private final CachedReq cachedReq;

    public AnnotationBasedTagProvider() {
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public AnnotationBasedTagProvider(EnvironmentVariables vars) {
        super(vars);
        configuration = new SystemPropertiesConfiguration(environmentVariables);
        persister = new RequirementPersister(configuration.getOutputDirectory(), rootDirectory);
        cachedReq = new CachedReq(rootDirectory);
        initialize();
    }

    private synchronized void initialize() {
        if (cachedReq.get() == null) {
            List<Class<?>> classes = ClassFinder.loadClasses().annotatedWith(Narrative.class).fromPackage(rootDirectory);
            SortedMap<String, Req> map;
            if (classes.isEmpty()) {
                map = persister.read();
            } else {
                map = initializeReqMapFromPackageAnnotations(classes);
            }
            cachedReq.set(map);
        }
        if (requirements == null) {
            initializeRequirementsFromReqMap();
        }
    }

    private void initializeRequirementsFromReqMap() {
        Map<String, Req> map = cachedReq.get();
        requirements = new ArrayList<Requirement>();
        for (SortedMap.Entry<String, Req> e : map.entrySet()) {
            Req req = e.getValue();
            //this is order children first
            Map<String, Requirement> children = new TreeMap<String, Requirement>();
            for (Req r : req.getChildren()) {
                children.put(r.getName(), r.getRequirement());
            }
            Requirement requirement = toRequirement(req, children);
            e.getValue().setRequirement(requirement);
            if (!e.getKey().contains(".")) {
                requirements.add(requirement);
            }
        }
    }

    private Requirement toRequirement(Req req, Map<String, Requirement> children) {
        return Requirement.named(humanReadableVersionOf(req.getName()))
                        .withOptionalDisplayName(req.getPublicName() == null || "".equals(req.getPublicName()) ? humanReadableVersionOf(req.getName()) : req.getPublicName())
                        .withOptionalCardNumber(req.getCardNumber())
                        .withType(getType(req))
                        .withNarrativeText(req.getNarrativeText())
                        .withChildren(new ArrayList(children.values()));
    }

    private SortedMap<String, Req> initializeReqMapFromPackageAnnotations(Collection<Class<?>> classes) {
        SortedMap<String, Req> map;
        map = new ChildrenFirstOrderedMap();
        for (Class p : classes) {
            Narrative n = (Narrative) p.getAnnotation(Narrative.class);
            String name = p.getName().replace(rootDirectory + ".", "").replace("package-info", "");
            String[] names = name.split(DOT_REGEX);
            String parentPackage = null;
            String currentPackage = "";
            for (int i = 0; i < names.length; i++) {
                currentPackage = currentPackage + names[i];
                Req r = map.get(currentPackage);
                Req parentRequirement;
                parentRequirement = parentPackage == null ? null : map.get(parentPackage);
                if (i == names.length - 1) {//this is the leaf having the annotation
                    r = new Req(i, names[i], n.title(), n.cardNumber(), n.type(), Joiner.on("\n").join(n.text()), r == null ? new ArrayList<Req>() : r.getChildren());
                    //do we have a class or a package as leaf
                    r.setAnnotatedClass(!p.getName().endsWith("package-info"));
                } else if (r == null) {
                    r = new Req(names[i], names[i], i);
                }
                if (parentRequirement != null) {
                    parentRequirement.getChildren().add(r);
                }
                map.put(currentPackage, r);
                parentPackage = currentPackage;
                currentPackage = currentPackage + ".";
            }
        }
        persister.write(map);
        return map;
    }

    private String getType(Req req) {
        String type = req.getType();
        if (req.isAnnotatedClass()) {
            type = "story";
        } else if (req.getType() == null || "".equals(req.getType())) {
            type = getDefaultType(req.getLevel());
        }
        return type;
    }

    @Override
    public List<Requirement> getRequirements() {
        return requirements;
    }

    @Override
    public Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome) {
        if (testOutcome.getUserStory() == null || testOutcome.getUserStory().getUserStoryClass() == null ||
                testOutcome.getUserStory().getUserStoryClass().getName() == null) {
            return Optional.absent();
        }
        String name = testOutcome.getUserStory().getUserStoryClass().getName().replace(rootDirectory + ".", "");
        getRequirements();
        Req req = cachedReq.get().get(name);
        if (req != null) {
            return Optional.of(req.getRequirement());
        } else {
            return Optional.absent();
        }
    }

    @Override
    public Optional<Requirement> getRequirementFor(TestTag testTag) {
        Preconditions.checkNotNull(testTag.getName());
        Preconditions.checkNotNull(testTag.getType());

        Optional<Requirement> result = Optional.absent();
        for (Req req : cachedReq.get().values()) {
            if (req.getRequirement().asTag().equals(testTag)) {
                return Optional.of(req.getRequirement());
            }
        }
        return result;
    }

    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        Set<TestTag> result = new HashSet<TestTag>();
        if (testOutcome.getPath() != null) {
            for (Req req : cachedReq.get().values()) {
                result.add(TestTag.withName(humanReadableVersionOf(req.getName())).andType(getType(req)));
            }
        }
        return result;
    }

}
