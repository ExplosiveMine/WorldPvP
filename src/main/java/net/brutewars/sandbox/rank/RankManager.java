package net.brutewars.sandbox.rank;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public final class RankManager {
    private static final Map<Ranks, Rank> ranks = new HashMap<Ranks, Rank>() {{
        put(Ranks.MEMBER, new Member());
        put(Ranks.OWNER, new Owner());
    }};

    public static Rank getRank(int weight) {
        return ranks.get(getRankName(weight));
    }

    private static Ranks getRankName(int weight) {
        switch (weight) {
            case 0: return Ranks.MEMBER;
            case 1: return Ranks.OWNER;
        }
        return null;
    }

    public static Rank getMember() {
        return ranks.get(Ranks.MEMBER);
    }

    public static Rank getOwner() {
        return ranks.get(Ranks.OWNER);
    }

    enum Ranks {
        MEMBER(0),
        OWNER(1);

        @Getter private final int weight;

        Ranks(int weight) {
            this.weight = weight;
        }
    }

}