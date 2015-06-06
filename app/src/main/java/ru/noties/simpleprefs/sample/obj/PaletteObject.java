package ru.noties.simpleprefs.sample.obj;

/**
 * Created by Dimitry Ivanov on 06.06.2015.
 */
public class PaletteObject {

    private String name;
    private ColorObject[] colors;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColorObject[] getColors() {
        return colors;
    }

    public void setColors(ColorObject[] colors) {
        this.colors = colors;
    }
}
