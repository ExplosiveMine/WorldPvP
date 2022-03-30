package net.brutewars.worldpvp.rank;

public final class Member extends Rank {
    @Override
    public boolean isMember() {
        return true;
    }

    @Override
    public String getName() {
        return "Member";
    }

    @Override
    public int getWeight() {
        return 0;
    }

}