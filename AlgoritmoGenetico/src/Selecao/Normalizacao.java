package Selecao;

import java.util.ArrayList;

import Individuos.Individuo;

public class Normalizacao {
	
	public double[] retornaFitness(ArrayList<Individuo> populacao, final double max, final double min) {
		double[] novosFitness = new double[populacao.size()];

		for (int i = 0; i < novosFitness.length; i++) {
			novosFitness[novosFitness.length - i - 1] = min
					+ ((max - min) / (populacao.size() - 1)) * i - 1;
		}

		return novosFitness;
	}
	
	public double[] retornaFitness(ArrayList<Individuo> populacao) {
		double[] novosFitness = new double[populacao.size()];

		for (int i = 0; i < novosFitness.length; i++) {
			novosFitness[i] = populacao.get(i).getFitness();
		}

		return novosFitness;
	}
	
}
