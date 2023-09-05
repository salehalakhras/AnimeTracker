public class Anime {
    private String animeName;
    private Float animeRating;
    private int useTimes = 0;

    public int getUseTimes() {
        return useTimes;
    }

    public void incrUseTimes() {
        this.useTimes++;
    }

    public Anime(){}

    public String getAnimeName() {
        return animeName;
    }

    public void setAnimeName(String animeName) {
        this.animeName = animeName;
    }

    public Float getAnimeRating() {
        return animeRating;
    }

    public void setAnimeRating(Float animeRating) {
            this.animeRating = animeRating;
    }

}
