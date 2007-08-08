/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for 
 * the development and deployment of geographic (GIS) applications. It is a open source 
 * contribution of the COGIT laboratory at the Institut G�ographique National (the French 
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net 
 *  
 * Copyright (C) 2005 Institut G�ographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with 
 * this library (see file LICENSE if present); if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 */

package fr.ign.cogit.geoxygene.contrib.cartetopo.exemple;

import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * Exemple de construction et d'utilisation d'une carte topo simple, h�rit�e
 * du sch�ma g�n�rique de la cartetopo.
 * 
 * La carte topo en question est definie par heritage � partir des
 * classes du package : MonNoeud, MonArc, MaFace, MaCarteTopo (on n'utilise
 * pas de groupes dans cet exemple).
 * 
 * La seule petite difficult� est d'indiquer dans le code que
 * MaCarteTopo est constitue� d'objets MonNoeud, MonArc et MaFace
 * plut�t que les g�n�riques Noeud, Arc et Face. Cette op�ration doit
 * se faire par un constructeur sp�cial dans la classe MaCarteTopo.
 * 
 * NB: il est bien entendu possible d'uiliser une cartetopo par d�faut si
 * la surcharge des classes arcs, noeuds et faces n'est pas necessaire
 * dans l'application.
 * 
 * @author Bonin
 * @version 1.0
 */

public class MaCarteTopo extends CarteTopo {

    public MaCarteTopo() {}
    
    public MaCarteTopo(String nom_logique) {
        this.ojbConcreteClass = this.getClass().getName(); // n�cessaire pour ojb
        this.setNom(nom_logique);
        this.setPersistant(false);

        /* Declaration des type de noeuds, arcs et face que la cartetopo va contenir */
    	Population noeuds = new Population(false,"Noeud",MonNoeud.class,true);
        this.addPopulation(noeuds);
        Population arcs = new Population(false,"Arc",MonArc.class,true);
        this.addPopulation(arcs);
        Population faces = new Population(false,"Face",MaFace.class,true);
        this.addPopulation(faces);
    }
	
}
