package net.brutewars.worldpvp.rank;

public abstract class Rank {
    public abstract String getName();

    public abstract int getWeight();

    public boolean isOwner() {
        return false;
    }

    public boolean isMember() {
        return false;
    }
}