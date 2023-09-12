public class AnimeCache {

    private final int SIZE = 10;
    private final Anime[] animeList = new Anime[SIZE];
    private int index = 0;

    public AnimeCache() {
    }

    /**
     * takes an anime object and puts it in the list
     * @param anime the anime that you want to add
     */
    public void addAnime(Anime anime) {
        // if the list is full
        if (index == 10) {
            // delete the least recently used anime
            deleteLRU();
        } else {
            animeList[index] = anime;
            index++;
        }
    }

    /**
     * deletes the least recently used anime from the list
     */
    public void deleteLRU() {
        int minUse = animeList[0].getUseTimes();
        int LRUindex = 0;
        // find the least recently used anime
        for (int i = 0; i < SIZE; i++) {
            if (animeList[i].getUseTimes() < minUse) {
                minUse = animeList[i].getUseTimes();
                LRUindex = i;
            }
        }

        // if the LRU anime is the last anime in the list
        if (LRUindex == 9) {
            index--;
        }
        // if it's somewhere else in the list
        else {
            // delete it from the list by shifting everything above it, leaving the last spot "empty"
            for (int i = LRUindex; i < SIZE - 1; i++) {
                animeList[i] = animeList[i + 1];
            }
            index--;
        }
    }

    /**
     * searches the list for an anime that has a name close to the name provided
     * @param animeName the anime name that you want
     * @return it returns an anime object if the anime is found, else it returns null
     */
    public Anime findAnime(String animeName) {
        for (int i = 0; i < index; i++) {
            if (AnimeTracker.similar(animeName, animeList[i].getAnimeName(), 2)){
                animeList[i].incrUseTimes();
                return animeList[i];
            }
        }
        return null;
    }
}
