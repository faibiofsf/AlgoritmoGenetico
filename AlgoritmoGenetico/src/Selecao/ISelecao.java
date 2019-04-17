package Selecao;

import java.util.ArrayList;

import Individuos.Individuo;

public interface ISelecao {
	public Individuo[] selecionar(ArrayList<Individuo> populacao);
	public Individuo[] selecionarNormalizado(ArrayList<Individuo> populacao, double max, double min);
		
}
