package table;

import fixtures.Fixture;
import java.util.Map;

@FunctionalInterface
public interface UpdatePositions {
    public void update(Fixture fixture, Map<Integer, TablePosition> positions);
}