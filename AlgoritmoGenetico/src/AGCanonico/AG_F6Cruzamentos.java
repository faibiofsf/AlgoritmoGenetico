package AGCanonico;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.DoubleStream;

import Avaliacao.AvaliacaoF6;
import Avaliacao.AvaliacaoF6Modificada;
import ComportamentoAG.Elitista;
import ComportamentoAG.EstadoEstacionario;
import Individuos.Individuo;
import Mutação.GeneAGene;
import Selecao.Roleta;

class AG_F6Cruzamentos {

	public ArrayList<Individuo> populacao;
	public long seed;
	private double probCruz, probMut, percentualElitismo;
	private int numeroIteracoes, tamanhoPopulacao, nBits, precisao, tipoElitismo;
	public Individuo melhorIndividuo, piorIndividuo;
	public ArrayList<Integer> fitnessPorIteracoes;
	public ArrayList<Double> mediaFitnessPopulacao;
	private Random random;
	public String saidaMelhorIndividuo, saidaIndividuos, avaliacao;
	public double[] execMediaPop, execMelhosInd, execPioresInd;
	double[] minMaxNorm;
	private boolean normalizado;

	// Construtor Entrada
	public AG_F6Cruzamentos(int tamanhoPopulacao, int numeroIteracoes, double probCruz, double probMut, int nBits, int precisao,
			String avaliacao, long seed, double[] maxMinNorm, boolean normalizado, int tipoElitismo,
			double percentualElitismo) {
		this.numeroIteracoes = numeroIteracoes;
		this.tamanhoPopulacao = tamanhoPopulacao;
		this.probCruz = probCruz;
		this.probMut = probMut;
		this.melhorIndividuo = new Individuo();
		this.fitnessPorIteracoes = new ArrayList<Integer>();
		this.mediaFitnessPopulacao = new ArrayList<Double>();
		this.nBits = nBits;
		this.precisao = precisao;
		this.seed = seed;
		random = new Random(this.seed);
		execMediaPop = new double[this.numeroIteracoes + 1];
		execMelhosInd = new double[this.numeroIteracoes + 1];
		execPioresInd = new double[this.numeroIteracoes + 1];
		this.avaliacao = avaliacao;
		this.minMaxNorm = maxMinNorm;
		this.normalizado = normalizado;
		this.tipoElitismo = tipoElitismo;
		this.percentualElitismo = percentualElitismo;
	}

	// Iniciar
	@SuppressWarnings("unchecked")
	public void run() {

		double fitnessPopulacao = 0;

		// Avaliar Populacao
		for (Individuo individuo : this.populacao) {
			avalia(individuo, avaliacao);
			fitnessPopulacao += (double) individuo.getFitness();
		}

		// Ranqueia populaÃ§Ã£o
		this.rank();

		this.insereNoGrafico(fitnessPopulacao, 0);

		int iteracao = 1;

		while (this.numeroIteracoes + 1 > iteracao) {

			ArrayList<Individuo> novaPopulacao = new ArrayList<Individuo>();

			while (novaPopulacao.size() < populacao.size()) {

				Individuo pai1 = null;
				Individuo pai2 = null;

				if (this.normalizado) {
					// Selecinar cruzamento por Roleta Normalizada
					Individuo[] pais = this.selecionaPaisNormalizado();
					pai1 = pais[0];
					pai2 = pais[1];
				} else {
					// Selecinar cruzamento por Roleta
					Individuo[] pais = this.selecionaPais();
					pai1 = pais[0];
					pai2 = pais[1];
				}
				// Cruzamento dos pais de acordo com a probabilidade e
				// MutaÃ§Ã£o
				// de acordo com probabilidade
				if (random.nextDouble() <= this.probCruz) {
					// Cruzamento
					Individuo[] filhos = this.filhosCruzamento(pai1, pai2);

					// Mutacao de acordo com probabilidade
					this.mutacao(filhos[0]);
					this.mutacao(filhos[1]);

					novaPopulacao.add(filhos[0]);
					novaPopulacao.add(filhos[1]);

				}
			}

			this.elitismo(novaPopulacao);

			fitnessPopulacao = 0;
			// Avaliar Nova Populacao
			for (Individuo individuo : this.populacao) {
				avalia(individuo, this.avaliacao);
				fitnessPopulacao += (double) individuo.getFitness();
			}

			// Ranqueia populacao
			this.rank();
			/*
			 * System.out.println(""); System.out.println("Iteraçao: " + iteracao); double[]
			 * XY = this.converteBinarioEmReal(melhorIndividuo.getCromossomo());
			 * System.out.println("Melhor Individuo: " + melhorIndividuo.getCromossomo() +
			 * ": " + XY[0] + " " + XY[1] + " : " + melhorIndividuo.getFitness());
			 */
			this.insereNoGrafico(fitnessPopulacao, iteracao);

			iteracao++;

		}

	}

	private void elitismo(ArrayList<Individuo> novaPopulacao) {
		if (tipoElitismo == 0) {
			this.novaPopSemElitismo(novaPopulacao);
		} else if (tipoElitismo == 1) {
			//this.novaPopElitista(novaPopulacao);
			new Elitista().novaPopulacao(novaPopulacao, this.populacao, 1);
		} else if (tipoElitismo == 2) {
			int quantidadeMantida = (int) ((this.percentualElitismo/100)*this.tamanhoPopulacao);
			//this.novaPopEstadoEstacionario(novaPopulacao);
			new EstadoEstacionario().novaPopulacao(novaPopulacao, this.populacao, quantidadeMantida);
		}
	}

	private void novaPopSemElitismo(ArrayList<Individuo> novaPopulacao) {
		this.populacao = null;
		this.populacao = (ArrayList<Individuo>) novaPopulacao.clone();
		novaPopulacao.clear();
	}

	private void insereNoGrafico(double fitnessPopulacao, int iteracao) {

		execMelhosInd[iteracao] = melhorIndividuo.getFitness();

		execPioresInd[iteracao] = piorIndividuo.getFitness();

		execMediaPop[iteracao] = fitnessPopulacao / this.populacao.size();
	}

	// Gerar populaÃ§Ã£o inicial de numeros reais
	private ArrayList<Individuo> populacaoInicialReais(int tamanhoPopulacao) {

		ArrayList<Individuo> populacaoInicial = new ArrayList<Individuo>();

		DoubleStream dsX = random.doubles(-100, 100);
		double[] numerosX = dsX.limit(tamanhoPopulacao).toArray();

		DoubleStream dsY = random.doubles(-100, 100);
		double[] numerosY = dsY.limit(tamanhoPopulacao).toArray();

		for (int i = 0; i < tamanhoPopulacao; i++) {
			String cromossomo = converteRealEmBinario(numerosX[i]) + converteRealEmBinario(numerosY[i]);

			Individuo individuo = new Individuo(cromossomo);
			double[] XY = { numerosX[i], numerosY[i] };
			individuo.setValorReal(XY);

			avalia(individuo, this.avaliacao);

			populacaoInicial.add(individuo);
		}

		return populacaoInicial;
	}

	// Gerar populaÃ§Ã£o inicial de numeros reais por arquivo
	private ArrayList<Individuo> populacaoInicialReais(int tamanhoPopulacao, Scanner f) {

		ArrayList<Individuo> populacaoInicial = new ArrayList<Individuo>();
		// Para saltar a seed, primeira linha do arquivo
		f.nextLine();

		for (int i = 0; i < tamanhoPopulacao; i++) {

			String[] s = f.nextLine().split("\t");
			double x = Double.parseDouble(s[0]);
			double y = Double.parseDouble(s[1]);

			String cromossomo = converteRealEmBinario(x) + converteRealEmBinario(y);

			Individuo individuo = new Individuo(cromossomo);
			double[] XY = { x, y };
			individuo.setValorReal(XY);

			avalia(individuo, this.avaliacao);

			populacaoInicial.add(individuo);
		}

		return populacaoInicial;
	}

	// Selecao Roleta
	private Individuo[] selecionaPais() {
		return new Roleta(random).selecionar(this.populacao);
	}

	// Selecao Normalizada
	private Individuo[] selecionaPaisNormalizado() {

		Individuo[] pais = new Roleta(random).selecionarNormalizado(this.populacao, this.minMaxNorm[1], this.minMaxNorm[0]);

		return pais;
	}

	// Cruzamento
	private Individuo[] filhosCruzamento(Individuo pai1, Individuo pai2) {

		return new Cruzamento.UmPontoCorte(random).cruzar(pai1, pai2, this.probCruz);
	}

	// MutaÃ§Ã£o
	private void mutacao(Individuo individuo) {

		new GeneAGene(random).mutar(individuo, probMut);

	}

	// Avaliacao
	private void avalia(Individuo individuo, String metodo) {

		if (metodo.equals("ScafferF6")) {
			new AvaliacaoF6(this.precisao).avaliar(individuo);
		} else
			new AvaliacaoF6Modificada(this.precisao).avaliar(individuo);
	}

	// Ranqueamento da populaÃ§Ã£o
	public void rank() {

		Collections.sort(this.populacao);

		// if (this.melhorIndividuo.getFitness() <
		// populacao.get(0).getFitness()){
		this.melhorIndividuo = new Individuo(this.populacao.get(0).getFitness(),
				this.populacao.get(0).getCromossomo());
		this.piorIndividuo = new Individuo(this.populacao.get(this.populacao.size() - 1).getFitness(),
				this.populacao.get(this.populacao.size() - 1).getCromossomo());
		// }
	}

	public String converteRealEmBinario(double numero) {
		String bin = "";
		int inteiro = (int) (numero * Math.pow(10, precisao));

		if (inteiro < 0) {
			inteiro = inteiro * -1;
		}

		while ((int) inteiro > 0) {
			if (inteiro % 2 == 0)
				bin = "0" + bin;
			else
				bin = "1" + bin;
			inteiro /= 2;

		}

		if (bin.length() < nBits) {
			for (int i = bin.length(); i < nBits; i++) {
				bin = "0" + bin;
			}
		}

		bin = (numero < 0) ? "1" + bin : "0" + bin;

		return bin;

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

	public static void main(String args[]) {

		double[] mutacoes = { 0.01 };
		double[] cruzamentos = { 0.75 };
		int[] populacoes = { 50 };
		int nIteracoes = 100;
		long[] seeds = { 123456, 654321, 765432, 234567, 987650 };
		int numeroExecucoes = 5, numeroPopulacoes = 5;
		boolean populacaoJaCriada = true;
		String[] avaliacao = { "ScafferF6" };
		double[] maxMinNorm = { 500, 1 };
		boolean[] normalizado = { false };
		//0 - Sem elitismo, 1 - com elitismo, 2 - Elitismo por estado estacionario
		int[] tipoElitismo = {0};
		int percentualElitismo = 10;
		//0 - um ponto de corte, 1 - uniforme, 2 ou mais - multiplos pontos de corte
		int[] tiposCruzamentos = {0,1,2};

		//for (int d = 0; d < tiposCruzamentos.length; d++) {
		
		for (int b = 0; b < tipoElitismo.length; b++) {

			// Executa com o fitness normalizado para cada populacao novamente e
			// para cada execucao
			for (int o = 0; o < normalizado.length; o++) {
				// Populacao
				for (int l = 0; l < numeroPopulacoes; l++) {

					for (int a = 0; a < avaliacao.length; a++) {

						FileWriter arqMediaInd = null, arqMelhorInd = null, arqMelhorPiorInd = null,
								arqMediaMedias = null;
						PrintWriter gravarArqMediaInd, gravarArqMelhorInd, gravarArqMelhorPiorInd, gravarArqMediaMedias;
						try {
							arqMediaInd = new FileWriter(
									"AlgoritmoGenetic_2\\pop" + l + "\\MediaPopulaco_pop " + l + "_" + avaliacao[a]
											+ "_Norm" + normalizado[o] + "_tipoElitismo" + tipoElitismo[b] + ".csv");
							arqMelhorInd = new FileWriter(
									"AlgoritmoGenetic_2\\pop" + l + "\\MelhorIndividuos_pop " + l + "_" + avaliacao[a]
											+ "_Norm" + normalizado[o] + "_tipoElitismo" + tipoElitismo[b] + ".csv");
							arqMelhorPiorInd = new FileWriter("AlgoritmoGenetic_2\\pop" + l
									+ "\\MelhorMediaPiorIndividuos_pop " + l + "_" + avaliacao[a] + "_Norm"
									+ normalizado[o] + "_tipoElitismo" + tipoElitismo[b] + ".csv");
							arqMediaMedias = new FileWriter("AlgoritmoGenetic_2\\pop" + l
									+ "\\Medias_MelhorMediaPiorIndividuos_pop " + l + "_" + avaliacao[a] + "_Norm"
									+ normalizado[o] + "_tipoElitismo" + tipoElitismo[b] + ".csv");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						gravarArqMediaInd = new PrintWriter(arqMediaInd);
						gravarArqMelhorInd = new PrintWriter(arqMelhorInd);
						gravarArqMelhorPiorInd = new PrintWriter(arqMelhorPiorInd);
						gravarArqMediaMedias = new PrintWriter(arqMediaMedias);

						ArrayList<double[]> mediaPopExecs = new ArrayList<double[]>();
						ArrayList<double[]> melhorIndExecs = new ArrayList<double[]>();
						ArrayList<double[]> piorIndExecs = new ArrayList<double[]>();

						double[] mediaMediaPopExecs = new double[nIteracoes + 1];
						double[] mediaMelhorIndExecs = new double[nIteracoes + 1];
						double[] mediaPiorIndExecs = new double[nIteracoes + 1];

						// Execucoes da populacao
						for (int k = 0; k < numeroExecucoes; k++) {
							// 28 bits para representar os numero -100,100 com 6
							// casas decimais
							// quando converter para real, considerar o piso igual a
							// -100
							AG_F6Cruzamentos ag = new AG_F6Cruzamentos(populacoes[0], nIteracoes, cruzamentos[0], mutacoes[0], 28, 6,
									avaliacao[a], seeds[k], maxMinNorm, normalizado[o], tipoElitismo[b],
									percentualElitismo);

							if (populacaoJaCriada) {
								FileReader file = null;
								try {
									file = new FileReader(
											"AlgoritmoGenetic_2\\PopulacoInicial_" + l + " _Execucaoo.txt");
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Scanner f = new Scanner(file);
								ag.populacao = ag.populacaoInicialReais(populacoes[0], f);
								f.close();
							} else {
								ag.populacao = ag.populacaoInicialReais(populacoes[0]);
								AG_F6Cruzamentos.gravaPopulacaoInicial(ag.populacao, k, ag.seed);
								if(k == numeroExecucoes-1) 
									populacaoJaCriada = true;
							}

							ag.run();

							mediaPopExecs.add(ag.execMediaPop);
							melhorIndExecs.add(ag.execMelhosInd);
							piorIndExecs.add(ag.execPioresInd);

						}
						gravarArqMediaInd.printf("#iteracoes\tE1\tE2\tE3\tE4\tE5");
						gravarArqMelhorInd.printf("#iteracoes\tE1\tE2\tE3\tE4\tE5");
						gravarArqMelhorPiorInd.printf(
								"#iteracoes\tMelhorE1\tMediaE1\tPiorE1\tMelhorE2\tMediaE2\tPiorE2\tMelhorE3\tMediaE3\tPiorE3\tMelhorE4\tMediaE4\tPiorE4\tMelhorE5\tMediaE5\tPiorE5");
						gravarArqMediaMedias.printf("#iteracoes\tMediaMelhor\tMediaMedias\tMediaPior");
						for (int i = 0; i < nIteracoes + 1; i++) {
							gravarArqMediaInd.printf("\n" + i + "\t" + mediaPopExecs.get(0)[i] + "\t"
									+ mediaPopExecs.get(1)[i] + "\t" + mediaPopExecs.get(2)[i] + "\t"
									+ mediaPopExecs.get(3)[i] + "\t" + mediaPopExecs.get(4)[i]);

							gravarArqMelhorInd.printf("\n" + i + "\t" + melhorIndExecs.get(0)[i] + "\t"
									+ melhorIndExecs.get(1)[i] + "\t" + melhorIndExecs.get(2)[i] + "\t"
									+ melhorIndExecs.get(3)[i] + "\t" + melhorIndExecs.get(4)[i]);

							gravarArqMelhorPiorInd.printf("\n" + i + "\t" + melhorIndExecs.get(0)[i] + "\t"
									+ mediaPopExecs.get(0)[i] + "\t" + piorIndExecs.get(0)[i] + "\t"
									+ melhorIndExecs.get(1)[i] + "\t" + mediaPopExecs.get(1)[i] + "\t"
									+ piorIndExecs.get(1)[i] + "\t" + melhorIndExecs.get(2)[i] + "\t"
									+ mediaPopExecs.get(2)[i] + "\t" + piorIndExecs.get(2)[i] + "\t"
									+ melhorIndExecs.get(3)[i] + "\t" + mediaPopExecs.get(3)[i] + "\t"
									+ piorIndExecs.get(3)[i] + "\t" + melhorIndExecs.get(4)[i] + "\t"
									+ mediaPopExecs.get(4)[i] + "\t" + piorIndExecs.get(4)[i]);

							mediaMediaPopExecs[i] = (mediaPopExecs.get(0)[i] + mediaPopExecs.get(1)[i]
									+ mediaPopExecs.get(2)[i] + mediaPopExecs.get(3)[i] + mediaPopExecs.get(4)[i])
									/ numeroExecucoes;
							mediaMelhorIndExecs[i] = (melhorIndExecs.get(0)[i] + melhorIndExecs.get(1)[i]
									+ melhorIndExecs.get(2)[i] + melhorIndExecs.get(3)[i] + melhorIndExecs.get(4)[i])
									/ numeroExecucoes;
							mediaPiorIndExecs[i] = (piorIndExecs.get(0)[i] + piorIndExecs.get(1)[i]
									+ piorIndExecs.get(2)[i] + piorIndExecs.get(3)[i] + piorIndExecs.get(4)[i])
									/ numeroExecucoes;

							gravarArqMediaMedias.printf("\n" + i + "\t" + mediaMelhorIndExecs[i] + "\t"
									+ mediaMediaPopExecs[i] + "\t" + mediaPiorIndExecs[i]);
						}

						try {

							gravarArqMelhorInd.close();
							arqMelhorInd.close();
							gravarArqMediaInd.close();
							arqMediaInd.close();
							gravarArqMelhorPiorInd.close();
							arqMelhorPiorInd.close();
							gravarArqMediaMedias.close();
							arqMediaMedias.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			}
//		}
	}

	public static void gravaPopulacaoInicial(ArrayList<Individuo> populacao, int k, long seed) {

		FileWriter arquivoPop = null;
		PrintWriter gravarArqPop = null;
		try {
			arquivoPop = new FileWriter("AlgoritmoGenetic_2\\PopulacoInicial_" + k + " _Execucaoo.txt");
			gravarArqPop = new PrintWriter(arquivoPop);
			gravarArqPop.println("Seed\t" + seed);
			for (Individuo individuo : populacao) {
				gravarArqPop.println(individuo.getValorReal()[0] + "\t" + String.valueOf(individuo.getValorReal()[1]));
			}

			gravarArqPop.close();
			arquivoPop.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
