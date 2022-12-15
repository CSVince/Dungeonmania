package dungeonmania.entities.enemies;

import dungeonmania.util.Position;

public class Hydra extends ZombieToast {
    public static final double DEFAULT_HEALTH = 10.0;
    public static final double DEFAULT_ATTACK = 10.0;
    public static final double DEFAULT_HEALTH_INCREASE_AMOUNT = 1.0;
    public static final double DEFAULT_HEALTH_INCREASE_RATE = 0.5;
    private double healthIncreaseRate;
    private double healthIncrease;

    public Hydra(Position position, double health, double attack, double increase, double rate) {
        super(position, health, attack);
        this.healthIncreaseRate = rate;
        this.healthIncrease = increase;
    }

    public boolean growBack() {
        double randomValue = Math.random();
        return randomValue <= healthIncreaseRate;
    }

    public double getHealthIncrease() {
        return healthIncrease;
    }
}
