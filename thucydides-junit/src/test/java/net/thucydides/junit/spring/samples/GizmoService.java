package net.thucydides.junit.spring.samples;

public class GizmoService {

    private String name;
    private WidgetService widgetService;
    private GizmoDao dao;

    public GizmoDao getDao() {
        return dao;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WidgetService getWidgetService() {
        return widgetService;
    }

    public void setWidgetService(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

}
