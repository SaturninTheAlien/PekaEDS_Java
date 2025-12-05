package pekaeds.decorator;

import pk2.level.PK2LevelSector;

public interface ISectorDecorator {
    public void perform(PK2LevelSector sector);
    public void setSeed(long seed);
}
