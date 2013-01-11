/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.mgcp;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.relief.CourbeDeNiveau;
import fr.ign.cogit.geoxygene.schemageo.impl.relief.CourbeDeNiveauImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.ChampContinuImpl;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/*
 * ###### IGN / CartAGen ###### Title: ContourLine Description: Courbes de
 * niveau Author: J. Renard Date: 18/09/2009
 */

public class MGCPContourLine extends GeneObjDefault implements IContourLine,
    MGCPFeature {

  /**
   * Associated Geoxygene schema object
   */
  private CourbeDeNiveau geoxObj;

  private long acc, hqc, smc;
  private String zv2;

  /**
   * Constructor
   */
  public MGCPContourLine(CourbeDeNiveau geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    if ((int) (this.getAltitude() / GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE)
        * GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE == this.getAltitude()) {
      this.isMaster = true;
    }
  }

  public MGCPContourLine(ILineString line, double value) {
    super();
    this.geoxObj = new CourbeDeNiveauImpl(new ChampContinuImpl(), value, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    if ((int) (this.getAltitude() / GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE)
        * GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE == this.getAltitude()) {
      this.isMaster = true;
    }
  }

  public MGCPContourLine(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new CourbeDeNiveauImpl(new ChampContinuImpl(), Integer
        .valueOf((String) attributes.get("zv2")), line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    if ((int) (this.getAltitude() / GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE)
        * GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE == this.getAltitude()) {
      this.isMaster = true;
    }

    this.acc = (Long) attributes.get("acc");
    this.hqc = (Long) attributes.get("hqc");
    this.zv2 = (String) attributes.get("zv2");

  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

  private boolean isMaster = false;

  @Override
  public boolean isMaster() {
    return this.isMaster;
  }

  @Override
  public double getWidth() {
    if (this.isMaster()) {
      return GeneralisationLegend.CN_LARGEUR_MAITRESSE;
    }
    return GeneralisationLegend.CN_LARGEUR_NORMALE;
  }

  @Override
  public IPolygon getSymbolExtent() {
    return (IPolygon) CommonAlgorithms.buffer(this.getGeom(), this.getWidth()
        * 0.5 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);
  }

  @Override
  public double getAltitude() {
    return (this.geoxObj).getValeur();
  }

  @Override
  public void setAltitude(double z) {
    (this.geoxObj).setValeur(z);
  }

  public long getAcc() {
    return acc;
  }

  public void setAcc(long acc) {
    this.acc = acc;
  }

  public long getHqc() {
    return hqc;
  }

  public void setHqc(long hqc) {
    this.hqc = hqc;
  }

  @Override
  public Map<String, Object> getAttributeMap(MGCPFeature feat) {
    // TODO Auto-generated method stub
    return null;
  }

  public void setZv2(String zv2) {
    this.zv2 = zv2;
  }

  public String getZv2() {
    return zv2;
  }

  public long getSmc() {
    return smc;
  }

  public void setSmc(long smc) {
    this.smc = smc;
  }

}