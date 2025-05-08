package com.homepage.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

public class MenuDTO {
    private Long id;
    private String name;
    private String description;
    private String htmlClass;
    @JsonIgnore
    private Long[] accessRoles;
    private boolean accessable;
    private Map<Integer, MenuItem> items = new LinkedHashMap<>();

    public MenuDTO() {
        //for Jackson Serialization
    }

    public String getHtmlClass() {
        return htmlClass;
    }

    public void setHtmlClass(String htmlClass) {
        this.htmlClass = htmlClass;
    }

    public Long[] getAccessRoles() {
        return accessRoles;
    }

    public void setAccessRoles(Long[] accessRoles) {
        this.accessRoles = accessRoles;
    }

    public int getItemsCount() {
        return items.size();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<Integer, MenuItem> getItems() {
        return items;
    }

    public void setItems(Map<Integer, MenuItem> items) {
        this.items = items;
    }

    public boolean isAccessable() {
        return accessable;
    }

    public void setAccessable(boolean accessable) {
        this.accessable = accessable;
    }

    public static class MenuItem {
        private Long id;
        private String link;
        private String name;
        private String title;
        private String description;
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private Long[] accessRoles;
        private String htmlClass;
        private Map<Integer, MenuItem> menuSubItems = new LinkedHashMap<>();

        public MenuItem() {
            //for Jackson Serialization
        }

        public String getHtmlClass() {
            return htmlClass;
        }

        public void setHtmlClass(String htmlClass) {
            this.htmlClass = htmlClass;
        }

        public int getSubItemsCount() {
            return menuSubItems.size();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public Long[] getAccessRoles() {
            return accessRoles;
        }

        public void setAccessRoles(Long[] accessRoles) {
            this.accessRoles = accessRoles;
        }

        public Map<Integer, MenuItem> getMenuSubItems() {
            return menuSubItems;
        }

        public void setMenuSubItems(Map<Integer, MenuItem> menuSubItems) {
            this.menuSubItems = menuSubItems;
        }
    }
}
