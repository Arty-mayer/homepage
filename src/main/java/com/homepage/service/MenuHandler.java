package com.homepage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homepage.Model.Menu;
import com.homepage.rpository.MenuRepository;
import com.homepage.dto.MenuDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Map;

@Service
@RequestScope
public class MenuHandler {
    MenuRepository menuRepository;

    public MenuHandler(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public String getMenuAlsJSON(Long id)  {
        Menu menu = menuRepository.getReferenceById(id);
        Map<Integer, MenuDTO.MenuItem> menuItems = null;

        try {
            menuItems = JSONItemsParsing(menu.getItems());
        }
        catch (JsonProcessingException e){
            System.out.println("Error parsing JSON: " + e.getMessage());
        }
        if (menuItems == null){
            return null;
        }

        MenuDTO menuDto = new MenuDTO();
        menuDto.setId(menu.getId());
        menuDto.setName(menu.getName());
        menuDto.setDescription(menu.getDescription());
        menuDto.setItems(menuItems);


        return "asd";
    }

    private void menuItemsFilter(Map<Integer, MenuDTO.MenuItem> menuItems){

    }

    private Map<Integer, MenuDTO.MenuItem> JSONItemsParsing(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<Integer, MenuDTO.MenuItem> menuItems;
        menuItems = objectMapper.readValue(
                json, new TypeReference<Map<Integer, MenuDTO.MenuItem>>() {});
        return menuItems;
    }
}
