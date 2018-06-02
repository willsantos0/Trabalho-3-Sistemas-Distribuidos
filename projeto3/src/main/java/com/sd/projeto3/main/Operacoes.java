/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd.projeto3.main;

import com.sd.projeto3.model.Mapa;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Willian
 */
public class Operacoes {
    
     private static Map<BigInteger, String> mapa = new HashMap();
    
    public void salvar(Mapa mapa1) {
        BigInteger chave = new BigInteger(String.valueOf(mapa1.getChave()));

        if (mapa.containsKey(mapa1.getChave())) {
            System.out.println("Mensagem com essa chave ja adicionada");
        }

        mapa.put(chave, mapa1.getTexto());
    }

    public void editar(Mapa mapa1) {
        BigInteger chave = new BigInteger(String.valueOf(mapa1.getChave()));

        if (mapa.containsKey(chave)) {
            mapa.replace(chave, mapa1.getTexto());
            return;
        }
        System.out.println("Chave nao encontrada");       
    }

    public void excluir(Mapa mapa1) {
        BigInteger chave = new BigInteger(String.valueOf(mapa1.getChave()));

        mapa.remove(chave);
    }

    public String buscar(Mapa mapa1) {
        BigInteger chave = new BigInteger(String.valueOf(mapa1.getChave()));
        
        if( mapa.get(chave) != null ) {
			return "=========================\nChave: " + chave + "\nMensagem: " + mapa.get( chave ) + "\n=========================";
		} else {
			return "Chave nao encontrada";
		}
    }
    
    public Mapa buscarObjeto(Mapa mapa1) {
        BigInteger chave = new BigInteger(String.valueOf(mapa1.getChave()));
        
        if( mapa.get(chave) != null ) {
            Mapa m = new Mapa();
            m.setChave(mapa1.getChave());
            m.setTexto(mapa.get( chave ));
            return m;
	} else {
            return null;
	}
    }

    public Map<BigInteger, String> getMapa() {
        return mapa;
    }
    
    public String buscarTodos() {
		String toString = "";
		for( Map.Entry< BigInteger, String > entry : mapa.entrySet() ) {
			toString = toString.concat( "(" + entry.getKey().toString() + "," + entry.getValue().toString() + ")" );
		}
		return toString;
	}
    
}
