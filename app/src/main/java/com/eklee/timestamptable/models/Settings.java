package com.eklee.timestamptable.models;

/**
 * Created by Judy on 2018-04-08.
 */

public class Settings {
    private String id;
    private String SaveOriginal;
    private String SaveFavorite;
    private String style, font, size, color;

    public Settings(String id, String saveOriginal, String saveFavorite) {
        this.id = id;
        SaveOriginal = saveOriginal;
        SaveFavorite = saveFavorite;
    }
    public Settings() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSaveOriginal() {
        return SaveOriginal;
    }

    public void setSaveOriginal(String saveOriginal) {
        SaveOriginal = saveOriginal;
    }

    public String getSaveFavorite() {
        return SaveFavorite;
    }

    public void setSaveFavorite(String saveFavorite) {
        SaveFavorite = saveFavorite;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

