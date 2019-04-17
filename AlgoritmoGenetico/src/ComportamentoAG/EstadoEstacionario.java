package ComportamentoAG;

import java.util.ArrayList;
import java.util.Collections;

import Individuos.Individuo;

public class EstadoEstacionario implements IComportamentoAG {

	@Override
	public void novaPopulacao(ArrayList<Individuo> novaPopulacao, ArrayList<Individuo> populacaoAtual,
			int quantidadeMantida) {
		// TODO Auto-generated method stub

		ArrayList<Individuo> melhores = new ArrayList<Individuo>();

		for (int i = 0; i < quantidadeMantida; i++) {
			melhores.add(new Individuo(populacaoAtual.get(i).getFitness(), populacaoAtual.get(i).getCromossomo()));
		}

		populacaoAtual = null;
		populacaoAtual = (ArrayList<Individuo>) novaPopulacao.clone();
		// Ranqueia populacao
		Collections.sort(populacaoAtual);

		for (int i = populacaoAtual.size() - quantidadeMantida; i < populacaoAtual.size(); i++) {
			populacaoAtual.set(i, melhores.get(populacaoAtual.size() - i - 1));
		}
		
		novaPopulacao.clear();
		
	}

	
}
