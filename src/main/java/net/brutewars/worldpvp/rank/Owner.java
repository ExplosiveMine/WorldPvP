package net.brutewars.worldpvp.rank;

public final class Owner extends Rank {
    @Override
    public boolean isOwner() {
        return true;
    }

    @Override
    public String getName() {
        return "Owner";
    }

    @Override
    public int getWeight() {
        return 1;
    }

}