package Individuos;

public class Individuo implements Comparable<Individuo> {

	private double fitness = 0;
	private String cromossomo;
	private double[] XY;

	public Individuo() {
	}

	public Individuo(String cromossomo) {
		this.cromossomo = cromossomo;
	}

	public Individuo(double fitness, String cromossomo) {
		super();
		this.fitness = fitness;
		this.cromossomo = cromossomo;
	}

	public void setValorReal(double[] XY) {
		this.XY = XY;
	}

	public double[] getValorReal() {
		return this.XY;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public String getCromossomo() {
		return cromossomo;
	}

	public void setCromossomo(String cromossomo) {
		this.cromossomo = cromossomo;
	}

	@Override
	public int compareTo(Individuo outroIndividuo) {
		// TODO Auto-generated method stub
		if (this.fitness > outroIndividuo.getFitness()) {
			return -1;
		}
		if (this.fitness < outroIndividuo.getFitness()) {
			return 1;
		}
		return 0;
	}
}