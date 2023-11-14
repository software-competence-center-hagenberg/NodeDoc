package at.scch.opcua.data;

public class TemplateInfo {
    private String templateName;
    private String templateDescription;

    public TemplateInfo() {
    }

    public TemplateInfo(String templateName, String templateDescription) {
        this.templateName = templateName;
        this.templateDescription = templateDescription;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateDescription() {
        return templateDescription;
    }

    public void setTemplateDescription(String templateDescription) {
        this.templateDescription = templateDescription;
    }
}
