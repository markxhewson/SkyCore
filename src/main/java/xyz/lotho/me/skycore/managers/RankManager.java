package xyz.lotho.me.skycore.managers;

import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.handlers.rank.Rank;
import xyz.lotho.me.skycore.handlers.rank.RankType;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class RankManager {

    private final SkyCore instance;
    private final Map<String, Rank> ranksMap = new HashMap<>();

    public RankManager(SkyCore instance) {
        this.instance = instance;
    }

    public Map<String, Rank> getRanksMap() {
        return this.ranksMap;
    }

    public void loadRanks() {
        // check if any ranks exist (none will exist on first install)
        this.instance.sql.select("SELECT count(*) FROM skycore_ranks", resultSet -> {
            try {
                if (resultSet.next()) {
                    int entries = resultSet.getInt(1);

                    if (entries == 0) { // import some default ranks
                        this.instance.sql.execute("INSERT INTO skycore_ranks (id, name, prefix, `default`, `weight`, type) VALUES (1, 'Member', '&8[&7Member&8] ', 1, 10, 'DEFAULT')");
                        this.instance.sql.execute("INSERT INTO skycore_ranks (id, name, prefix, `default`, `weight`, type) VALUES (2, 'Admin', '&8[&cAdmin&8] ', 0, 100, 'STAFF')");
                    }
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });

        // load all ranks into ranks map
        this.instance.sql.select("SELECT * FROM skycore_ranks", resultSet -> {
            try {
                while (resultSet.next()) {
                    Rank rank = new Rank();

                    rank.setID(resultSet.getInt("id"));
                    rank.setName(resultSet.getString("name"));
                    rank.setWeight(resultSet.getInt("weight"));
                    rank.setPrefix(resultSet.getString("prefix"));
                    rank.setDefaultRank(resultSet.getBoolean("default"));
                    rank.setRankType(RankType.valueOf(resultSet.getString("type")));

                    this.getRanksMap().put(rank.getName(), rank);
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public Rank getRank(String name) {
        return this.getRanksMap().get(name);
    }

    public Rank getDefaultRank() {
        HashMap<String, Rank> results = new HashMap<>();

        this.getRanksMap().forEach((rankName, rank) -> {
            if (rank.isDefaultRank()) results.put("result", rank);
        });

        return results.get("result");
    }
}
