package net.brutewars.sandbox.rank;

public abstract class Rank {
    public abstract String getName();

    public boolean isOwner() {
        return false;
    }

}