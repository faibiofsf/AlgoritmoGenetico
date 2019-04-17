package Cruzamento;

import Individuos.Individuo;

public interface ICruzamento {
	public Individuo[] cruzar(Individuo pai1, Individuo pai2, double probCruz);

}
