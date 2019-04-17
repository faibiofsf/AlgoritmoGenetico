package Cruzamento;

import java.util.Random;

import Individuos.Individuo;

public class UmPontoCorte implements ICruzamento {

	private Random random;
	
	public UmPontoCorte(Random random) {
		super();
		this.random = random;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Individuo[] cruzar(Individuo pai1, Individuo pai2, double probCruz) {
		// TODO Auto-generated method stub
		Individuo filho1 = new Individuo();
		Individuo filho2 = new Individuo();

		// Random r = new Random();
		int posicao = random.nextInt(pai1.getCromossomo().length());

		if (pai1.getCromossomo().length() != pai2.getCromossomo().length()) {
			System.out.println("teste");
		}

		String crom1 = pai1.getCromossomo().substring(0, posicao);
		crom1 += pai2.getCromossomo().substring(posicao, pai2.getCromossomo().length());

		String crom2 = pai2.getCromossomo().substring(0, posicao);
		crom2 += pai1.getCromossomo().substring(posicao, pai1.getCromossomo().length());

		filho1.setCromossomo(crom1);
		filho2.setCromossomo(crom2);

		Individuo[] filhos = { filho1, filho2 };

		return filhos;
	}
	
}
