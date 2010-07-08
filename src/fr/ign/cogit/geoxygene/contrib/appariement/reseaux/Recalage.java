/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for
 * the development and deployment of geographic (GIS) applications. It is a open
 * source
 * contribution of the COGIT laboratory at the Institut Géographique National
 * (the French
 * National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with
 * this library (see file LICENSE if present); if not, write to the Free
 * Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux;

import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ArcApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.NoeudApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * méthodes pour le recalage d'un réseau sur un autre.
 * NB : méthodes réalisées pour un cas particulier et non retouchées pour
 * assurer une bonne généricité.
 * ////////////// A manier avec précaution //////////////////.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0
 */

public class Recalage {
    /**
     * Recale la géométrie des arcs d'un graphe sur un autre graphe
     * une fois que ceux-ci ont été appariés.
     * Un lien (correspondant) est gardé entre les nouveaux arcs et leurs
     * correspondants
     * dans le réseau de référence (accessible par arc.getCorrespondants()),
     * IMPORTANT 1: ctARecaler doit être le réseau 1 dans l'appariement,
     * et ctSurLaquelleRecaler le réseau 2.
     * IMPORTANT 2: pour garder les liens, l'appariement doit avoir été lancé
     * avec le paramètre debugBilanSurObjetsGeo à FALSE
     * NB: méthode pour les cas relativement simples qui mérite sans doute
     * d'être affinée.
     * 
     * @param ctARecaler
     *            Le réseau à recaler
     * @param ctSurLaquelleRecaler
     *            Le réseau sur lequel recaler
     * @param liens
     *            Des liens d'appariement entre les réseaux "à recaler" et
     *            "sur lequel recaler"
     * @return Le réseau recalé (en entrée-sortie)
     */
    public static CarteTopo recalage(ReseauApp ctARecaler,
            ReseauApp ctSurLaquelleRecaler, EnsembleDeLiens liens) {
        CarteTopo ctRecale = new CarteTopo(I18N
                .getString("Recalage.CorrectedNetwork")); //$NON-NLS-1$
        // On ajoute dans le réseau recalé les arcs sur lesquels on recale qui
        // sont appariés.
        Iterator<?> itArcsSurLesquelsRecaler = ctSurLaquelleRecaler
                .getPopArcs().getElements().iterator();
        while (itArcsSurLesquelsRecaler.hasNext()) {
            ArcApp arc = (ArcApp) itArcsSurLesquelsRecaler.next();
            if (arc.getListeGroupes().size() == 0)
                continue;
            if (!arc.aUnCorrespondantGeneralise(liens))
                continue;
            Arc nouvelArc = ctRecale.getPopArcs().nouvelElement();
            nouvelArc.setGeometrie(arc.getGeometrie());
            nouvelArc
                    .setCorrespondants(arc.objetsGeoRefEnCorrespondance(liens));
        }

        // On ajoute dans le réseau recalé les arcs à recaler qui ne sont pas
        // appariés,
        // en modifiant la géométrie pour assurer un raccord amorti avec le
        // reste.
        Iterator<?> itArcsARecaler = ctARecaler.getPopArcs().getElements()
                .iterator();
        while (itArcsARecaler.hasNext()) {
            ArcApp arc = (ArcApp) itArcsARecaler.next();
            if (arc.getLiens(liens.getElements()).size() != 0)
                continue;
            Arc nouvelArc = ctRecale.getPopArcs().nouvelElement();
            nouvelArc.setGeometrie(new GM_LineString((DirectPositionList) arc
                    .getGeom().coord().clone())); // vraie duplication de
                                                  // géométrie (un peu tordu,
                                                  // certes)
            geometrieRecalee(arc, nouvelArc, liens);
        }
        return ctRecale;
    }

    /**
     * Methode utilisée par le recalage pour assurer le recalage.
     * Attention : cette méthode n'est pas très générique : elle suppose que
     * l'on recale Ref sur Comp uniquement
     * et elle mérite des affinements.
     */
    private static void geometrieRecalee(ArcApp arcARecaler, Arc arcRecale,
            EnsembleDeLiens liens) {
        NoeudApp noeudARecalerIni = (NoeudApp) arcARecaler.getNoeudIni();
        NoeudApp noeudARecalerFin = (NoeudApp) arcARecaler.getNoeudFin();
        NoeudApp noeudRecaleIni, noeudRecaleFin;
        List<LienReseaux> liensDuNoeudARecalerIni = noeudARecalerIni
                .getLiens(liens.getElements());
        List<LienReseaux> liensDuNoeudARecalerFin = noeudARecalerFin
                .getLiens(liens.getElements());
        GM_LineString nouvelleGeometrie = new GM_LineString(
                (DirectPositionList) arcARecaler.getGeom().coord().clone());
        GM_LineString geomTmp;
        double longueur, abscisse;
        Vecteur decalage, decalageCourant;
        DirectPosition ptCourant;
        int i;

        if (liensDuNoeudARecalerIni.size() == 1) {
            // si le noeud initial de l'arc à recalé est apparié avec le réseau
            // comp
            if ((liensDuNoeudARecalerIni.get(0)).getNoeuds2().size() == 1) {
                noeudRecaleIni = (NoeudApp) (liensDuNoeudARecalerIni.get(0))
                        .getNoeuds2().get(0);
                nouvelleGeometrie.setControlPoint(0, noeudRecaleIni
                        .getGeometrie().getPosition());

                decalage = new Vecteur(noeudARecalerIni.getCoord(),
                        noeudRecaleIni.getCoord());
                geomTmp = new GM_LineString(
                        (DirectPositionList) nouvelleGeometrie.coord().clone());
                longueur = nouvelleGeometrie.length();
                abscisse = 0;
                for (i = 1; i < nouvelleGeometrie.coord().size() - 1; i++) {
                    ptCourant = geomTmp.coord().get(i);
                    abscisse = abscisse
                            + geomTmp.getControlPoint(i).distance(
                                    geomTmp.getControlPoint(i - 1));
                    decalageCourant = decalage.multConstante(1 - abscisse
                            / longueur);
                    nouvelleGeometrie.setControlPoint(i, decalageCourant
                            .translate(ptCourant));
                }
            }
        }

        // si le noeud final de l'arc à recalé est apparié avec le réseau comp
        if (liensDuNoeudARecalerFin.size() == 1) {
            if ((liensDuNoeudARecalerFin.get(0)).getNoeuds2().size() == 1) {
                noeudRecaleFin = (NoeudApp) (liensDuNoeudARecalerFin.get(0))
                        .getNoeuds2().get(0);
                nouvelleGeometrie.setControlPoint(nouvelleGeometrie.coord()
                        .size() - 1, noeudRecaleFin.getGeometrie()
                        .getPosition());

                decalage = new Vecteur(noeudARecalerFin.getCoord(),
                        noeudRecaleFin.getCoord());
                geomTmp = new GM_LineString(
                        (DirectPositionList) nouvelleGeometrie.coord().clone());
                longueur = nouvelleGeometrie.length();
                abscisse = 0;
                for (i = nouvelleGeometrie.coord().size() - 2; i > 0; i--) {
                    ptCourant = geomTmp.coord().get(i);
                    abscisse = abscisse
                            + geomTmp.getControlPoint(i).distance(
                                    geomTmp.getControlPoint(i + 1));
                    decalageCourant = decalage.multConstante(1 - abscisse
                            / longueur);
                    nouvelleGeometrie.setControlPoint(i, decalageCourant
                            .translate(ptCourant));
                }
            }
        }

        arcRecale.setGeom(nouvelleGeometrie);
    }

}
