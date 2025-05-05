package com.homepage.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class MenuDTO {
    private Long id;
    private String name;
    private String description;
    private Map<Integer, MenuItem> items = new LinkedHashMap<>();

    public MenuDTO() {
        //for Jackson Serialization
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

    public static class MenuItem {
        private Long id;
        private String link;
        private String name;
        private String title;
        private String description;
        private Long[] accessRoles;
        private Map<Integer, MenuItem> menuSubItems = new LinkedHashMap<>();

        public MenuItem() {
            //for Jackson Serialization
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
