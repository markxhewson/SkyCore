package xyz.lotho.me.skycore.handlers.rank;

public class Rank {

    private int id;
    private String name;
    private String prefix;
    private int weight;
    private boolean defaultRank;
    private RankType rankType;

    public Rank() {
        this.id = 0;
        this.prefix = "";
        this.name = "";
        this.weight = 0;
        this.defaultRank = true;
        this.rankType = RankType.DEFAULT;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isDefaultRank() {
        return this.defaultRank;
    }

    public void setDefaultRank(boolean defaultRank) {
        this.defaultRank = defaultRank;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setRankType(RankType rankType) {
        this.rankType = rankType;
    }

    public RankType getRankType() {
        return this.rankType;
    }
}
