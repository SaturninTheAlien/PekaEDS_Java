package pekaeds.ui.misc;

public enum AppEnum {
    NOT_SELECTED("App not selected", false),
    LEVEL_EDITOR("Level Editor", true),
    SPRITE_EDITOR("Sprite Editor", true),
    EPISODE_PACKER("Episode packing tool", false);

    private final String label;
    private final boolean visible;

    AppEnum(String label, boolean visible){
        this.label = label;
        this.visible = visible;
    }

    @Override
    public String toString() {
        return label;
    }

    public boolean isVisible(){
        return this.visible;
    }
}
