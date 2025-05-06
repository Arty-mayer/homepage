package com.homepage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homepage.model.Menu;
import com.homepage.rpository.MenuRepository;
import com.homepage.dto.MenuDTO;
import com.homepage.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Service
@RequestScope
public class MenuHandler {
    private final MenuRepository menuRepository;
    private final Long userRoleId;

    public MenuHandler(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
        userRoleId = getUserRoleId();
    }

    public String getMenuAlsJSON(Long id) {
        Optional<Menu> menuOptional = menuRepository.findById(id);
        if (menuOptional.isEmpty()) {
            System.out.println("MenuHandler error: there is no menu with id " + id + " in DB");
            return null;
        }
        Menu menu = menuOptional.get();

        MenuDTO menuDto = new MenuDTO();
        menuDto.setId(menu.getId());
        menuDto.setName(menu.getName());
        if (menu.getDescription() != null && !menu.getDescription().isEmpty()) {
            menuDto.setDescription(menu.getDescription());
        }

        if (menu.getHtmlClass() != null && !menu.getHtmlClass().isEmpty()) {
            menuDto.setHtmlClass(menu.getHtmlClass());
        }

        if (Arrays.stream(menu.getAccessRoles()).findAny().isPresent()) {
            menuDto.setAccessRoles(menu.getAccessRoles());
        } else {
            Long[] adminRole = {1L};
            menuDto.setAccessRoles(adminRole);
        }

        if (Arrays.stream(menuDto.getAccessRoles()).noneMatch(item -> item.equals(userRoleId))) {
            menuDto.setAccessable(false);
            menuDto.setItems(null);

        } else {
            menuDto.setAccessable(true);
            Map<Integer, MenuDTO.MenuItem> menuItems = null;
            if (menu.getItems() != null) {
                try {
                    menuItems = JSONItemsParsing(menu.getItems());
                } catch (JsonProcessingException e) {
                    System.out.println("Error parsing JSON: " + e.getMessage());
                }
            }

            if (menuItems != null && !menu.getItems().isEmpty()) {
                menuItemsFilter(menuItems);
                menuDto.setItems(menuItems);
            }
        }

        String jsonMenu = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonMenu = objectMapper.writeValueAsString(menuDto);
        } catch (JsonProcessingException e) {
            System.out.println("Error JSON writeValueAsString: " + e.getMessage());
        }
        return jsonMenu;
    }

    private Long getUserRoleId() {
        if (!SecurityUtils.isLoggedIn()) {
            return 2L;
        }
        if (SecurityUtils.getUserCustom() != null) {
            return SecurityUtils.getUserCustom().getRoleIds();
        }
       return 2L;
    }

    private void menuItemsFilter(Map<Integer, MenuDTO.MenuItem> menuItems) {
        ArrayList<Integer> keysToRemove = new java.util.ArrayList<Integer>();

        menuItems.forEach((key, item) -> {
            Long[] accessRolesLost;
            if (Arrays.stream(item.getAccessRoles()).findAny().isPresent()) {
                accessRolesLost = item.getAccessRoles();
            } else {
                accessRolesLost = new Long[]{1L};
            }

            if (Arrays.stream(accessRolesLost).noneMatch(lost -> lost.equals(userRoleId))) {
                keysToRemove.add(key);
            }
            else {
                if (!item.getMenuSubItems().isEmpty()) {
                    menuItemsFilter(item.getMenuSubItems());
                }
            }
        });
        keysToRemove.forEach(menuItems::remove);
    }

    private Map<Integer, MenuDTO.MenuItem> JSONItemsParsing(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<Integer, MenuDTO.MenuItem> menuItems;
        menuItems = objectMapper.readValue(
                json, new TypeReference<Map<Integer, MenuDTO.MenuItem>>() {
                });
        return menuItems;
    }
}
