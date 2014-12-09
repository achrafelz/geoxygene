package fr.ign.cogit.geoxygene.matching.geopoint;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.matching.beeri.AppariementBeeri;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;
import fr.ign.cogit.geoxygene.util.string.ApproximateMatcher;

/**
 * Points Data matching using distance and name. 
 * Matching criteria are sequentially applied
 * 
 */


public class SequentiallyPositionName {
  private static final Logger LOGGER = Logger.getLogger(SequentiallyPositionName.class);
  /**
   * Appariement par recherche du plus proche voisin et qui prend en compte les
   * toponymes. Chaque élément de popRef est apparié avec son plus proche voisin
   * dans popComp
   * 
   * @param popComp Population d'objets avec une géomtrie ponctuelle.
   * @param popRef Population d'objets avec une géomtrie ponctuelle.
   * @param seuilDistanceMax Seuil de distance au dessous duquel on n'apparie
   *          pas deux objets.
   * @param attributeRef
   * @param attributeComp
   * 
   * @return Ensemble de liens d'appariement. 
   *         Seulement des liens 1-1 sont créés.
   * 
   */
  public static EnsembleDeLiens appariementPPVEvalTop (IPopulation<IFeature> popRef, IPopulation<IFeature> popComp,
      double seuilEcart, double seuilDistanceMax, String attributeRef, String attributeComp) {
    
    EnsembleDeLiens liens = new EnsembleDeLiens();
    
    ApproximateMatcher AM = new ApproximateMatcher();
    AM.setIgnoreCase(true);
    
    // Indexation de la population de comparaison
    if (!popComp.hasSpatialIndex()) {
      LOGGER.info("Indexation de popComp (oronymes)");
      popComp.initSpatialIndex(Tiling.class, true, 10);
    }
    
    int sizeRef = popRef.getElements().size();
    int sizeComp = popComp.getElements().size();
    LOGGER.info("Size popRef " + sizeRef);
    LOGGER.info("Size popComp = " + sizeComp);
    
    // On parcourt les objets ref un par un
    for (IFeature objetRef : popRef) {
      
      IPopulation<IFeature> pop1Ref = new Population<IFeature>("Ref");
      pop1Ref.setFeatureType(objetRef.getFeatureType());
      pop1Ref.add(objetRef);
      IPopulation<IFeature> candidatsApp = popComp.selectionElementsProchesGenerale(pop1Ref, seuilDistanceMax);
    
      if (candidatsApp.size() == 0) {
        continue;
      }
      
      // Pour chaque objet ref on calcule la distance à tous les objets comp
      // proches
      // pour ne garder que le plus proche
      double distPP = seuilDistanceMax;
      IFeature candidatRetenu = null;
      for (IFeature objetComp : candidatsApp) {
        double distance = ((GM_Point) objetRef.getGeom()).getPosition().distance(((GM_Point) objetComp.getGeom()).getPosition());
        if (distance <= distPP) {
          distPP = distance;
          candidatRetenu = objetComp;
        }
      }
      // On crée un nouveau lien avec sa géométrie et son évaluation
      Lien lien = liens.nouvelElement();
      lien.addObjetRef(objetRef);
      lien.addObjetComp(candidatRetenu);

      GM_LineString ligne = new GM_LineString();
      ligne.addControlPoint(((GM_Point) objetRef.getGeom()).getPosition());
      ligne.addControlPoint(((GM_Point) candidatRetenu.getGeom()).getPosition());
      lien.setGeom(ligne);
      
      String oronyme = candidatRetenu.getAttribute(attributeRef).toString();
      oronyme = AM.processAccent(oronyme);
      String toponyme = objetRef.getAttribute(attributeComp).toString();
      toponyme = AM.processAccent(toponyme);
      
      int ecart = AM.match(toponyme, oronyme);
      int ecartRelatif = 100 * ecart / Math.max(toponyme.length(), oronyme.length());
      if (ecart <= seuilEcart || oronyme.startsWith(toponyme)) {
        if (ecartRelatif > 50) {
          if (oronyme.startsWith(toponyme) || oronyme.endsWith(toponyme)) {
            lien.setEvaluation(0.5);
          } else {
            lien.setEvaluation(0);
          }
        } else if (ecartRelatif >= 10 && ecartRelatif <= 50) {
          lien.setEvaluation(0.5);
        } else {
          lien.setEvaluation(1);
        }
      } else
        lien.setEvaluation(0);
    }
    
    return liens;
  }
 

}