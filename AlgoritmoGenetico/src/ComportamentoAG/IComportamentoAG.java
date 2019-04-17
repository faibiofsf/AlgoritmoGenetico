package ComportamentoAG;

import java.util.ArrayList;

import Individuos.Individuo;

public interface IComportamentoAG {
	public void novaPopulacao(ArrayList<Individuo> novaPopulacao, ArrayList<Individuo> populacaoAtual, int quantidadeMantida);
}
