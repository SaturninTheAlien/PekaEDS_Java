package pekaeds.decorator;

import pk2.level.PK2LevelSector;

public interface ISectorDecorator {
    public void perform(PK2LevelSector sector) throws Exception;
    public void setSeed(long seed);
}
