package lk.cmh.app.ceylonmarkethub.data.model.settings;

public class SettingsItem {

    private String title;
    private String description;
    private Class<?> activity;

    public SettingsItem() {
    }

    public SettingsItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public SettingsItem(String title, String description, Class<?> activity) {
        this.title = title;
        this.description = description;
        this.activity = activity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Class<?> getActivity() {
        return activity;
    }

    public void setActivity(Class<?> activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "SettingsItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
