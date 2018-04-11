package csc309.geocracy;

public class Computer extends Player {

    public Computer {

    }

    public Territory placeTroops(Territory list[]) {

    }

    public Territory selectTerritory(Territory listOwn[]) {
        Territory own = selectOwnTerritory(listOwn);

        return selectEnemyTerritory(own);
    }

    public Territory selectOwnTerritory(Territory listOwn[]) {
        //TODO Select territory with most troops?
    }

    public Territory selectEnemyTerritory(Territory own) {
        Territory enemy;

        //own.adjList[]?
        //TODO look at adjacent territories to own

        //TODO choose territory with least amount of troops?

        return enemy;
    }
}
