package dev.cobblesword.nachospigot.knockback;

public class CraftKnockbackProfile implements KnockbackProfile {

	private String name;

	private double horizontal = 0.4D;
	private double vertical = 0.4D;
	private double verticalMin = -1.0D;
	private double verticalMax = 0.4D;
	private double extraHorizontal = 0.5D;
	private double extraVertical = 0.1D;
	private double frictionHorizontal = 0.5D;
	private double frictionVertical = 0.5D;

	private boolean stopSprint = true;

	private double rodHorizontal = 0.4D;
	private double rodVertical = 0.4D;
	private double arrowHorizontal = 0.4D;
	private double arrowVertical = 0.4D;
	private double pearlHorizontal = 0.4D;
	private double pearlVertical = 0.4D;
	private double snowballHorizontal = 0.4D;
	private double snowballVertical = 0.4D;
	private double eggHorizontal = 0.4D;
	private double eggVertical = 0.4D;

	public CraftKnockbackProfile(String name) {
		this.name = name;
	}

	@Override
	public void save() {
		save(false);
	}

	@Override
	public void save(boolean projectiles) {
		final String path = "knockback.profiles." + this.name;

		KnockbackConfig.set(path + ".stop-sprint", this.stopSprint);
		KnockbackConfig.set(path + ".friction-horizontal", this.frictionHorizontal);
		KnockbackConfig.set(path + ".friction-vertical", this.frictionVertical);
		KnockbackConfig.set(path + ".horizontal", this.horizontal);
		KnockbackConfig.set(path + ".vertical", this.vertical);
		KnockbackConfig.set(path + ".vertical-max", this.verticalMax);
		KnockbackConfig.set(path + ".vertical-min", this.verticalMin);
		KnockbackConfig.set(path + ".extra-horizontal", this.extraHorizontal);
		KnockbackConfig.set(path + ".extra-vertical", this.extraVertical);

		if (projectiles) {
			KnockbackConfig.set(path + ".projectiles.rod.horizontal", this.rodHorizontal);
			KnockbackConfig.set(path + ".projectiles.rod.vertical", this.rodVertical);
			KnockbackConfig.set(path + ".projectiles.arrow.horizontal", this.arrowHorizontal);
			KnockbackConfig.set(path + ".projectiles.arrow.vertical", this.arrowVertical);
			KnockbackConfig.set(path + ".projectiles.pearl.horizontal", this.pearlHorizontal);
			KnockbackConfig.set(path + ".projectiles.pearl.vertical", this.pearlVertical);
			KnockbackConfig.set(path + ".projectiles.snowball.horizontal", this.snowballHorizontal);
			KnockbackConfig.set(path + ".projectiles.snowball.vertical", this.snowballVertical);
			KnockbackConfig.set(path + ".projectiles.egg.horizontal", this.eggHorizontal);
			KnockbackConfig.set(path + ".projectiles.egg.vertical", this.eggVertical);
		}

		KnockbackConfig.save();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public double getHorizontal() {
		return horizontal;
	}

	@Override
	public void setHorizontal(double horizontal) {
		this.horizontal = horizontal;
	}

	@Override
	public double getVertical() {
		return vertical;
	}

	@Override
	public void setVertical(double vertical) {
		this.vertical = vertical;
	}

	@Override
	public double getVerticalMin() {
		return verticalMin;
	}

	@Override
	public void setVerticalMin(double verticalMin) {
		this.verticalMin = verticalMin;
	}

	@Override
	public double getVerticalMax() {
		return verticalMax;
	}

	@Override
	public void setVerticalMax(double verticalMax) {
		this.verticalMax = verticalMax;
	}

	@Override
	public double getExtraHorizontal() {
		return extraHorizontal;
	}

	@Override
	public void setExtraHorizontal(double extraHorizontal) {
		this.extraHorizontal = extraHorizontal;
	}

	@Override
	public double getExtraVertical() {
		return extraVertical;
	}

	@Override
	public void setExtraVertical(double extraVertical) {
		this.extraVertical = extraVertical;
	}

	@Override
	public double getFrictionHorizontal() {
		return frictionHorizontal;
	}

	@Override
	public void setFrictionHorizontal(double frictionHorizontal) {
		this.frictionHorizontal = frictionHorizontal;
	}

	@Override
	public double getFrictionVertical() {
		return frictionVertical;
	}

	@Override
	public void setFrictionVertical(double frictionVertical) {
		this.frictionVertical = frictionVertical;
	}

	@Override
	public boolean isStopSprint() {
		return stopSprint;
	}

	@Override
	public void setStopSprint(boolean stopSprint) {
		this.stopSprint = stopSprint;
	}

	@Override
	public double getRodHorizontal() {
		return rodHorizontal;
	}

	@Override
	public void setRodHorizontal(double rodHorizontal) {
		this.rodHorizontal = rodHorizontal;
	}

	@Override
	public double getRodVertical() {
		return rodVertical;
	}

	@Override
	public void setRodVertical(double rodVertical) {
		this.rodVertical = rodVertical;
	}

	@Override
	public double getArrowHorizontal() {
		return arrowHorizontal;
	}

	@Override
	public void setArrowHorizontal(double arrowHorizontal) {
		this.arrowHorizontal = arrowHorizontal;
	}

	@Override
	public double getArrowVertical() {
		return arrowVertical;
	}

	@Override
	public void setArrowVertical(double arrowVertical) {
		this.arrowVertical = arrowVertical;
	}

	@Override
	public double getPearlHorizontal() {
		return pearlHorizontal;
	}

	@Override
	public void setPearlHorizontal(double pearlHorizontal) {
		this.pearlHorizontal = pearlHorizontal;
	}

	@Override
	public double getPearlVertical() {
		return pearlVertical;
	}

	@Override
	public void setPearlVertical(double pearlVertical) {
		this.pearlVertical = pearlVertical;
	}

	@Override
	public double getSnowballHorizontal() {
		return snowballHorizontal;
	}

	@Override
	public void setSnowballHorizontal(double snowballHorizontal) {
		this.snowballHorizontal = snowballHorizontal;
	}

	@Override
	public double getSnowballVertical() {
		return snowballVertical;
	}

	@Override
	public void setSnowballVertical(double snowballVertical) {
		this.snowballVertical = snowballVertical;
	}

	@Override
	public double getEggHorizontal() {
		return eggHorizontal;
	}

	@Override
	public void setEggHorizontal(double eggHorizontal) {
		this.eggHorizontal = eggHorizontal;
	}

	@Override
	public double getEggVertical() {
		return eggVertical;
	}

	@Override
	public void setEggVertical(double eggVertical) {
		this.eggVertical = eggVertical;
	}

	@Override
	public String[] getKnockbackValues() {
		return new String[] { "Horizontal§7: " + this.horizontal, "Vertical§7: " + this.vertical,
				"Vertical-Max§7: " + this.verticalMax, "Vertical-Min§7: " + this.verticalMin,
				"Extra-Horizontal§7: " + this.extraHorizontal, "Extra-Vertical§7: " + this.extraVertical,
				"Friction-Horizontal§7: " + this.frictionHorizontal, "Friction-Vertical§7: " + this.frictionVertical,
				"Stop-Sprint§7: " + this.stopSprint, };
	}

	@Override
	public String[] getProjectilesValues() {
		return new String[] { "Rod-Horizontal§7: " + this.rodHorizontal, "Rod-Vertical§7: " + this.rodVertical,
				"Arrow-Horizontal§7: " + this.arrowHorizontal, "Arrow-Vertical§7: " + this.arrowVertical,
				"Pearl-Horizontal§7: " + this.pearlHorizontal, "Pearl-Vertical§7: " + this.pearlVertical,
				"Snowball-Horizontal§7: " + this.snowballHorizontal, "Snowball-Vertical§7: " + this.snowballVertical,
				"Egg-Horizontal§7: " + this.eggHorizontal, "Egg-Vertical§7: " + this.eggVertical, };
	}
}
