package Avaliacao;

import java.util.ArrayList;

import Individuos.Individuo;


public interface IAvaliacao {
	public void avaliar(Individuo individuo);
	
	public void avaliar(Individuo individuo, Object objetivo);
}
