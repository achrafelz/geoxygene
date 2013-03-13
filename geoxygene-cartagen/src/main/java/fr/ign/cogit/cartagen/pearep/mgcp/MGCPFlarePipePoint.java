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

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscPoint;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.bati.AutreConstruction;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.AutreConstructionImpl;

public class MGCPFlarePipePoint extends MGCPFeature implements IMiscPoint {

  private AutreConstruction geoxObj;

  // VMAP attributes
  private String ace, ale, cpyrt_note, dmf, hgt, len, src_date, src_info,
      tier_note, txt, uid, upd_date, upd_info, voi;
  private long acc, ace_eval, ale_eval, awp, fun, loc, src_name, smc, srl,
      upd_name, zval_type;

  /**
   * @param type
   */
  public MGCPFlarePipePoint(IPoint point, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new AutreConstructionImpl(point);
    this.setInitialGeom(point);
    this.setEliminated(false);
    this.setAttributeMap(attributes);//

    this.acc = getLongAttribute("acc");
    this.ace_eval = getLongAttribute("ace_eval");
    this.ale_eval = getLongAttribute("ale_eval");
    this.awp = getLongAttribute("awp");
    this.fun = getLongAttribute("fun");
    this.loc = getLongAttribute("loc");
    this.src_name = getLongAttribute("src_name");
    this.upd_name = getLongAttribute("upd_name");
    this.srl = getLongAttribute("srl");
    this.zval_type = getLongAttribute("zval_type");

    this.ace = (String) attributes.get("ace");
    this.ale = (String) attributes.get("ale");
    this.cpyrt_note = (String) attributes.get("cpyrt_note");
    this.hgt = (String) attributes.get("hgt");
    this.dmf = (String) attributes.get("dmf");
    this.len = (String) attributes.get("len");
    this.src_date = (String) attributes.get("src_date");
    this.src_info = (String) attributes.get("src_info");
    this.tier_note = (String) attributes.get("tier_note");
    this.txt = (String) attributes.get("txt");
    this.uid = (String) attributes.get("uid");
    this.upd_date = (String) attributes.get("upd_date");
    this.upd_info = (String) attributes.get("upd_info");
    this.cpyrt_note = (String) attributes.get("cpyrt_note");
    this.voi = (String) attributes.get("voi");

  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  }

  public String getVoi() {
    return this.voi;
  }

  public void setVoi(String voi) {
    this.voi = voi;
  }

  public long getAcc() {
    return this.acc;
  }

  public void setAcc(long acc) {
    this.acc = acc;
  }

  public String getHgt() {
    return this.hgt;
  }

  public void setHgt(String hgt) {
    this.hgt = hgt;
  }

  public long getFun() {
    return this.fun;
  }

  public void setFun(long fun) {
    this.fun = fun;
  }

  public String getAce() {
    return ace;
  }

  public void setAce(String ace) {
    this.ace = ace;
  }

  public String getAle() {
    return ale;
  }

  public void setAle(String ale) {
    this.ale = ale;
  }

  public String getCpyrt_note() {
    return cpyrt_note;
  }

  public void setCpyrt_note(String cpyrt_note) {
    this.cpyrt_note = cpyrt_note;
  }

  public String getSrc_date() {
    return src_date;
  }

  public void setSrc_date(String src_date) {
    this.src_date = src_date;
  }

  public String getSrc_info() {
    return src_info;
  }

  public void setSrc_info(String src_info) {
    this.src_info = src_info;
  }

  public String getTier_note() {
    return tier_note;
  }

  public void setTier_note(String tier_note) {
    this.tier_note = tier_note;
  }

  public String getTxt() {
    return txt;
  }

  public void setTxt(String txt) {
    this.txt = txt;
  }

  public long getAce_eval() {
    return ace_eval;
  }

  public void setAce_eval(long ace_eval) {
    this.ace_eval = ace_eval;
  }

  public long getAle_eval() {
    return ale_eval;
  }

  public void setAle_eval(long ale_eval) {
    this.ale_eval = ale_eval;
  }

  public String getUpd_info() {
    return upd_info;
  }

  public void setUpd_info(String upd_info) {
    this.upd_info = upd_info;
  }

  public String getUpd_date() {
    return upd_date;
  }

  public void setUpd_date(String upd_date) {
    this.upd_date = upd_date;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public long getSrc_name() {
    return src_name;
  }

  public void setSrc_name(long src_name) {
    this.src_name = src_name;
  }

  public long getUpd_name() {
    return upd_name;
  }

  public void setUpd_name(long upd_name) {
    this.upd_name = upd_name;
  }

  public long getZval_type() {
    return zval_type;
  }

  public void setZval_type(long zval_type) {
    this.zval_type = zval_type;
  }

  public long getSmc() {
    return smc;
  }

  public void setSmc(long smc) {
    this.smc = smc;
  }

  public String getDmf() {
    return dmf;
  }

  public void setDmf(String dmf) {
    this.dmf = dmf;
  }

  public String getLen() {
    return len;
  }

  public void setLen(String len) {
    this.len = len;
  }

  public long getAwp() {
    return awp;
  }

  public void setAwp(long awp) {
    this.awp = awp;
  }

  public long getLoc() {
    return loc;
  }

  public void setLoc(long loc) {
    this.loc = loc;
  }

  public long getSrl() {
    return srl;
  }

  public void setSrl(long srl) {
    this.srl = srl;
  }

}
