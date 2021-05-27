package com.example.demo.domain;

import lombok.Data;

@Data
public class MappingTitleAndButtons {
    Object title;
    Object buttons;

    public static MappingTitleAndButtons create(Object title, Object buttons) {
        MappingTitleAndButtons mappingTitleAndButtons = new MappingTitleAndButtons();
        mappingTitleAndButtons.setButtons(buttons);
        mappingTitleAndButtons.setTitle(title);
        return mappingTitleAndButtons;
    }
}
