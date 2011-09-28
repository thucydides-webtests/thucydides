package net.thucydides.junit.spring.samples;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class GizmoDao {

    private String name;
    private WidgetService widgetService;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> findAll() { return Arrays.asList("Red Gizmo", "Blue Gizmo", "Green Gizmo"); }

}
