package me.hazedev.hapi.enchanting;

public interface CostAlgorithm {

    /**
     * @param level Current level of the enchantment
     * @return Cost of the next level
     */
    long getCost(int level);

    static CostAlgorithm single(long price) {
        return level -> price;
    }

    static CostAlgorithm linear(long increment) {
        return linear(increment, increment);
    }

    static CostAlgorithm linear(long initialCost, long increment) {
        return level -> initialCost + ((long) level * increment);
    }

    static CostAlgorithm exponential(long base, long multi, float exponent)  {
        return level -> (base - multi) + (long) (multi * Math.pow(exponent, level));
    }

    static CostAlgorithm exponential(long initialCost, float multiplier) {
        return level -> (long) (initialCost * Math.pow(multiplier, level));
    }

    @Deprecated
    static CostAlgorithm fibonacci(int initialCost) {
        return level -> {
            long lastCost = initialCost;
            long thisCost = initialCost;
            for (int i = 1; i <= level; i++) {
                long nextCost = lastCost + thisCost;
                lastCost = thisCost;
                thisCost = nextCost;
            }
            return thisCost;
        };
    }

}
