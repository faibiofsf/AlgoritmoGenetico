package Avaliacao;

import Individuos.Individuo;

public class AvaliacaoF6Modificada implements IAvaliacao {

	private int precisao;

	public AvaliacaoF6Modificada(int precisao) {
		super();
		this.precisao = precisao;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void avaliar(Individuo individuo) {
		// TODO Auto-generated method stub
		String cromossomo = individuo.getCromossomo();

		double[] valoresXY = converteBinarioEmReal(cromossomo);

		double pontuacao = Integer.MIN_VALUE;
		pontuacao = ScafferF6_M(valoresXY[0], valoresXY[1]);

		individuo.setFitness(pontuacao);
		individuo.setValorReal(valoresXY);
	}

	@Override
	public void avaliar(Individuo individuo, Object objetivo) {
		// TODO Auto-generated method stub

	}

	// Scaffer's F6 function Modificada
	private double ScafferF6_M(double x, double y) {
		double temp1 = x * x + y * y;
		double temp2 = Math.sin(Math.sqrt(temp1));
		double temp3 = 1.0 + 0.001 * temp1;
		return (999.5 + ((temp2 * temp2 - 0.5) / (temp3 * temp3)));
	}

	public double[] converteBinarioEmReal(String binario) {

		String binX = String.copyValueOf(binario.toCharArray(), 0, (binario.length() / 2) - 1);

		String binY = String.copyValueOf(binario.toCharArray(), (binario.length() / 2), (binario.length() / 2) - 1);

		String binX1 = String.copyValueOf(binX.toCharArray(), 1, (binX.length() - 1));
		String binY1 = String.copyValueOf(binY.toCharArray(), 1, (binY.length() - 1));

		double numeroX = Integer.parseInt(binX1, 2);
		double numeroY = Integer.parseInt(binY1, 2);

		if (binX.startsWith("1")) {
			numeroX = numeroX * -1;
		}

		if (binY.startsWith("1")) {
			numeroY = numeroY * -1;
		}

		double[] retorno = { numeroX / Math.pow(10, precisao), numeroY / Math.pow(10, precisao) };

		return retorno;

	}

}
