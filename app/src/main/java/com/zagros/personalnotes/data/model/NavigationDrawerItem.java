package com.zagros.personalnotes.data.model;

public class NavigationDrawerItem {
    private int iconId;
    private int title;

    public NavigationDrawerItem(int iconId, int title) {
        this.iconId = iconId;
        this.title = title;
    }

    public int getIconId() {
        return iconId;
    }

    public int getTitle() {
        return title;
    }
}
