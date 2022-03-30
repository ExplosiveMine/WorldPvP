package net.brutewars.worldpvp.rank;

import com.google.common.base.Preconditions;
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

    public static Rank getNextRank(Rank rank) {
        Preconditions.checkNotNull(rank, "rank parameter cannot be null");
        return ranks.get(getRankName(getRankName(rank).getWeight() + 1));
    }

    public static Rank getPreviousRank(Rank rank) {
        Preconditions.checkNotNull(rank, "rank parameter cannot be null");
        return ranks.get(getRankName(getRankName(rank).getWeight() - 1));
    }

    private static Ranks getRankName(Rank rank) {
        if (rank.isMember()) return Ranks.MEMBER;
        if (rank.isOwner()) return Ranks.OWNER;
        return null;
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