package ComportamentoAG;

import java.util.ArrayList;
import java.util.Collections;

import Individuos.Individuo;

public class Elitista implements IComportamentoAG {

	@Override
	public void novaPopulacao(ArrayList<Individuo> novaPopulacao, ArrayList<Individuo> populacaoAtual, int quantidadeMantida) {
		// TODO Auto-generated method stub
		Individuo melhorIndividuo = new Individuo(populacaoAtual.get(0).getFitness(),
				populacaoAtual.get(0).getCromossomo());
		populacaoAtual = null;
		populacaoAtual = (ArrayList<Individuo>) novaPopulacao.clone();
		// Ranqueia populacao
		Collections.sort(populacaoAtual);
		populacaoAtual.set(populacaoAtual.size() - 1, melhorIndividuo);
		novaPopulacao.clear();
	}


}
