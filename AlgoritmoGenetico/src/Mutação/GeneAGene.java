package Mutação;

import java.util.Random;

import Individuos.Individuo;

public class GeneAGene implements IMutacao {
	private Random random;

	public GeneAGene(Random random) {
		super();
		this.random = random;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void mutar(Individuo individuo, double probMut) {
		// TODO Auto-generated method stub
		char[] crom = individuo.getCromossomo().toCharArray();

		for (int posicao = 0; posicao < crom.length; posicao++) {

			if (random.nextDouble() < probMut) {
				if (crom[posicao] == '0') {
					crom[posicao] = '1';
				} else if (crom[posicao] == '1') {
					crom[posicao] = '0';
				}

			}
		}

		individuo.setCromossomo(String.valueOf(crom));
	}

}
