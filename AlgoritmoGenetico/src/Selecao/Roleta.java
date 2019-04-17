package Selecao;

import java.util.ArrayList;
import java.util.Random;

import Individuos.Individuo;

public class Roleta extends Normalizacao implements ISelecao {

	private Random random;
	private ArrayList<Individuo> populacao;

	public Roleta(Random random) {
		super();
		this.random = random;
	}

	@Override
	public Individuo[] selecionar(ArrayList<Individuo> populacao) {

		this.populacao = populacao;
		
		double[] fitness = new double[populacao.size()];

		fitness = retornaFitness(populacao);
		Individuo[] pais = new Individuo[2];
		
		for (int i = 0; i < pais.length; i++) {
			pais[i] = roleta(fitness); 
		}

		return pais;
	}

	@Override
	public Individuo[] selecionarNormalizado(ArrayList<Individuo> populacao, double max, double min) {
		// TODO Auto-generated method stub
		double[] fitness = new double[populacao.size()];

		fitness = retornaFitness(populacao, max, min);

		Individuo[] pais = new Individuo[2];
		
		for (int i = 0; i < pais.length; i++) {
			pais[i] = roleta(fitness); 
		}

		return pais;
	}

	private Individuo roleta(double[] fitness) {

		int somatorio = 0;
		double probabilidades = 0.0;
		Individuo pai = null;

		for (int i1 = 0; i1 < fitness.length; i1++) {
			somatorio += fitness[i1];
		}

		// Roleta
		double ponteiro = random.nextDouble();

		for (int i1 = 0; i1 < fitness.length; i1++) {
			Individuo individuo = this.populacao.get(i1);
			if (ponteiro <= ((double) fitness[i1] / (double) somatorio) + probabilidades) {
				pai = new Individuo(individuo.getFitness(), individuo.getCromossomo());
				break;
			} else {
				probabilidades += ((double) fitness[i1] / (double) somatorio);
			}
		}

		return pai;
	}

}
