package com.khoavdse170395.questionservice.model;

public enum MembershipTier {
    BASIC(1),
    SILVER(2),
    GOLD(3),
    PLATINUM(4);

    private final int rank;

    MembershipTier(int rank) {
        this.rank = rank;
    }

    public boolean isAtLeast(MembershipTier other) {
        if (other == null) {
            return true;
        }
        return this.rank >= other.rank;
    }

    public int getRank() {
        return rank;
    }
}
