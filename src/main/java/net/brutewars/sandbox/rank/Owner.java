package net.brutewars.sandbox.rank;

public final class Owner extends Rank {
    @Override
    public boolean isOwner() {
        return true;
    }

    @Override
    public String getName() {
        return "Owner";
    }

}