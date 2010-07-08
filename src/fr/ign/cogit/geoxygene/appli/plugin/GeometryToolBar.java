/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicToolBarUI;

import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.mode.AddPointMode;
import fr.ign.cogit.geoxygene.appli.mode.CreateInteriorRingMode;
import fr.ign.cogit.geoxygene.appli.mode.CreateLineStringMode;
import fr.ign.cogit.geoxygene.appli.mode.CreatePointMode;
import fr.ign.cogit.geoxygene.appli.mode.CreatePolygonMode;
import fr.ign.cogit.geoxygene.appli.mode.CreateRectangleMode;
import fr.ign.cogit.geoxygene.appli.mode.MoveFeatureMode;
import fr.ign.cogit.geoxygene.appli.mode.MovePointMode;
import fr.ign.cogit.geoxygene.appli.mode.RemovePointMode;
import fr.ign.cogit.geoxygene.appli.mode.SelectionMode;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_CurveSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * Class allowing geometry edition.
 * </p>
 * Classe représentant la boîte à outils permettant l'édition de la
 * géométrie des objets, et définit toutes les méthodes qui servent à
 * l'édition.
 * @author Sylvain Becuwe
 * @author Julien Perret
 */
@SuppressWarnings( { "serial", "nls",
        "unqualified-field-access" })
public class GeometryToolBar extends JToolBar {
    public final static int WIDTH = 220;
    public final static int HEIGHT = 350;
    /*
     * Variables pour l'onglet Edition
     */
    JButton bSelectionObjet = null;
    JLabel lNbSelection = new JLabel("Objets sélectionnés: 0");
    JCheckBox cbVoirSelection = new JCheckBox("Afficher selection", true);
    JButton bViderSelection = new JButton("Vider la sélection");
    JButton bAjouterPoint = null;
    JButton bSupprimerPoint = null;
    JButton bDeplacerPoint = null;
    JButton bDeplacerObjet = null;
    JPanel pSelections = new JPanel();
    JPanel pEditionPoints = new JPanel();
    JPanel pEditionObjets = new JPanel();
    JPanel pOptions = new JPanel();
    /*
     * Variables pour l'onglet Creation
     */
    JComboBox cBoxCoucheAModifier = new JComboBox();
    JTextField newLayerNameText = new JTextField("");
    JButton bCreationPoints = null;
    JButton bCreationPolygon = null;
    JButton bCreationRectangle = null;
    JButton bCreationInteriorRing = null;
    JButton bCreationLigne = null;
    JPanel pCoucheAModifier = new JPanel();
    JPanel pCreationGeom = new JPanel();
    String layerName = "Créer une nouvelle couche";
    /*
     * Variables pour l'onglet Options
     */
    JLabel lMarge = new JLabel("Précision du curseur (en m)");
    JSpinner marge;
    JCheckBox cbSelectionMultiple = new JCheckBox("Sélection multiple");
    JCheckBox cbAutoriserGeomNonValide = new JCheckBox(
            "Autoriser les géométries non valides");
    int precisionCurseur = 20;
    // Le panel qui contient les onglets
    JTabbedPane tabbedPane = new JTabbedPane();
    // Les panels onglets
    JPanel tabEdition = new JPanel();
    JPanel tabCreation = new JPanel();
    JPanel tabOptions = new JPanel();
    // Autres variables
    private ProjectFrame frame;
    CustomToolBarUI ui = new CustomToolBarUI();

    /**
     *
     */
    public class CustomToolBarUI extends BasicToolBarUI {
        public CustomToolBarUI() {
            super();
        }

        @Override
        public void setOrientation(int orientation) {
            super.setOrientation(SwingConstants.VERTICAL);
        }
    }

    /**
     * @param frame
     */
    public GeometryToolBar(final ProjectFrame frame) {
        super("Boîte à outils", SwingConstants.VERTICAL);
        this.frame = frame;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                requestFocus();
            }
        });
        SelectionMode selectionMode = new SelectionMode(this.frame
                .getMainFrame(), this.frame.getMainFrame().getMode());
        bSelectionObjet = selectionMode.getButton();
        AddPointMode addMode = new AddPointMode(this.frame.getMainFrame(), this.frame
                .getMainFrame().getMode());
        addMode.setGeometryToolBar(this);
        JButton bAjouterPoint = addMode.getButton();
        RemovePointMode removeMode = new RemovePointMode(this.frame.getMainFrame(),
                this.frame.getMainFrame().getMode());
        removeMode.setGeometryToolBar(this);
        JButton bSupprimerPoint = removeMode.getButton();
        MovePointMode movePointMode = new MovePointMode(this.frame
                .getMainFrame(), this.frame.getMainFrame().getMode());
        movePointMode.setGeometryToolBar(this);
        JButton bDeplacerPoint = movePointMode.getButton();
        MoveFeatureMode moveFeatureMode = new MoveFeatureMode(this.frame
                .getMainFrame(), this.frame.getMainFrame().getMode());
        moveFeatureMode.setGeometryToolBar(this);
        JButton bDeplacerObjet = moveFeatureMode.getButton();
        /*
         * Variables pour l'onglet Creation
         */
        CreatePointMode createPointMode = new CreatePointMode(this.frame
                .getMainFrame(), this.frame.getMainFrame().getMode());
        createPointMode.setGeometryToolBar(this);
        bCreationPoints = createPointMode.getButton();
        CreatePolygonMode createPolygonMode = new CreatePolygonMode(this.frame
                .getMainFrame(), this.frame.getMainFrame().getMode());
        createPolygonMode.setGeometryToolBar(this);
        bCreationPolygon = createPolygonMode.getButton();
        CreateRectangleMode createRectangleMode = new CreateRectangleMode(
                this.frame.getMainFrame(), this.frame.getMainFrame().getMode());
        createRectangleMode.setGeometryToolBar(this);
        bCreationRectangle = createRectangleMode.getButton();
        CreateInteriorRingMode createRingMode = new CreateInteriorRingMode(
                this.frame.getMainFrame(), this.frame.getMainFrame().getMode());
        createRingMode.setGeometryToolBar(this);
        bCreationInteriorRing = createRingMode.getButton();
        CreateLineStringMode createLineStringMode = new CreateLineStringMode(
                this.frame.getMainFrame(), this.frame.getMainFrame().getMode());
        createLineStringMode.setGeometryToolBar(this);
        bCreationLigne = createLineStringMode.getButton();
        setUI(ui);
        setLayout(new GridLayout(2, 1));
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        tabEdition.setLayout(new GridLayout(2, 1));
        tabCreation.setLayout(new GridLayout(2, 1));
        tabOptions.setLayout(new GridLayout(3, 1));
        SpinnerNumberModel model = new SpinnerNumberModel(precisionCurseur, 1,
                50, 1);
        marge = new JSpinner(model);
        marge.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Integer i = (Integer) (marge.getValue());
                precisionCurseur = i.intValue();
            }
        });
        cBoxCoucheAModifier.addItem("Créer une nouvelle couche");
        for (Layer layer : frame.getLayers()) {
            cBoxCoucheAModifier.addItem(layer.getName());
        }
        cBoxCoucheAModifier.setSelectedIndex(0);
        cBoxCoucheAModifier.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cBoxCoucheAModifier.removeAllItems();
                cBoxCoucheAModifier.addItem("Créer une nouvelle couche");
                for (Layer layer : frame.getLayers()) {
                    if (layer.isVisible()) {
                        cBoxCoucheAModifier.addItem(layer.getName());
                    }
                }
                cBoxCoucheAModifier.setSelectedItem(layerName);
            }
        });
        // Ajouts des listeners aux boutons
        cBoxCoucheAModifier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox comboBox = (JComboBox) (e.getSource());
                if (comboBox.getSelectedIndex() != 0
                        && comboBox.getSelectedIndex() != -1) {
                    layerName = (String) comboBox.getSelectedItem();
                    Layer layer = getFrame().getLayer(layerName);
                    System.out.println(layer.getFeatureCollection()
                            .getFeatureType());
                    if (layer.getFeatureCollection().getFeatureType() != null) {
                        if (layer.getFeatureCollection().getFeatureType()
                                .getGeometryType() != GM_Polygon.class
                                && layer.getFeatureCollection()
                                        .getFeatureType().getGeometryType() != GM_MultiSurface.class) bCreationInteriorRing
                                .setVisible(false);
                        else {
                            bCreationInteriorRing.setVisible(true);
                        }
                    }
                } else {
                    bCreationInteriorRing.setVisible(false);
                }
            }
        });
        bAjouterPoint.setToolTipText("Insérer un sommet");
        bSupprimerPoint.setToolTipText("Supprimer un sommet");
        bDeplacerPoint.setToolTipText("Déplacer un sommet");
        bCreationPoints.setToolTipText("Créer un point");
        bCreationPolygon.setToolTipText("Créer un polygone");
        bCreationInteriorRing
                .setToolTipText("Insérer un anneau intérieur dans un polygone");
        bCreationRectangle.setToolTipText("Créer un rectangle");
        bCreationLigne.setToolTipText("Créer une polyligne");
        bDeplacerObjet.setToolTipText("Déplacer les objets sélectionnés");
        bViderSelection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                getFrame().clearSelection();
            }
        });
        cbVoirSelection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                getFrame().repaint();
            }
        });
        // Ajout des components au tabEdition
        // Ajout des boutons
        pSelections.add(bSelectionObjet);
        pSelections.add(cbSelectionMultiple);
        pSelections.add(cbVoirSelection);
        pSelections.add(lNbSelection);
        pSelections.add(bViderSelection);
        // Fin de l'ajout des boutons
        pSelections.setBorder(BorderFactory.createTitledBorder("Sélection"));
        // Ajout des boutons
        pEditionPoints.add(bAjouterPoint);
        pEditionPoints.add(bSupprimerPoint);
        pEditionPoints.add(bDeplacerPoint);
        // Fin de l'ajout des boutons
        pEditionPoints.setBorder(BorderFactory
                .createTitledBorder("Edition des sommets"));
        tabEdition.add(pEditionPoints);
        // Fin tabEdition
        // Ajout des boutons
        pEditionObjets.add(bDeplacerObjet);
        // Fin de l'ajout des boutons
        pEditionObjets.setBorder(BorderFactory
                .createTitledBorder("Edition des objets"));
        tabEdition.add(pEditionObjets);
        // Fin tabEdition
        // Ajout des components au tabCreation
        // Ajout des boutons
        pCoucheAModifier.setLayout(new BorderLayout());
        pCoucheAModifier.add(cBoxCoucheAModifier, BorderLayout.NORTH);
        pCoucheAModifier.add(newLayerNameText, BorderLayout.CENTER);
        // Fin de l'ajout des boutons
        pCoucheAModifier.setBorder(BorderFactory
                .createTitledBorder("Couche à modifier"));
        tabCreation.add(pCoucheAModifier);
        // Ajout des boutons
        pCreationGeom.add(bCreationPoints);
        pCreationGeom.add(bCreationLigne);
        pCreationGeom.add(bCreationPolygon);
        pCreationGeom.add(bCreationRectangle);
        pCreationGeom.add(bCreationInteriorRing);
        // Fin de l'ajout des boutons
        pCreationGeom.setBorder(BorderFactory
                .createTitledBorder("Création de géométrie"));
        tabCreation.add(pCreationGeom);
        // Fin tabCreation
        // Ajout des components au tabOptions
        tabOptions.add(cbAutoriserGeomNonValide);
        JPanel margePanel = new JPanel();
        margePanel.add(lMarge);
        margePanel.add(marge);
        margePanel.setBorder(BorderFactory
                .createEtchedBorder(EtchedBorder.LOWERED));
        tabOptions.add(margePanel);
        // Fin tabOption
        tabbedPane.addTab("Edition", tabEdition);
        tabbedPane.addTab("Création", tabCreation);
        tabbedPane.addTab("Options", tabOptions);
        add(pSelections);
        add(tabbedPane);
    }

    /**
     * @return
     */
    public ProjectFrame getFrame() {
        return frame;
    }

    /**
     * @return
     */
    public boolean isSelectionMultiple() {
        return cbSelectionMultiple.isSelected();
    }

    /**
     * @return
     */
    public boolean autoriserGeomNonValide() {
        return cbAutoriserGeomNonValide.isSelected();
    }

    /**
     * Permet d'ajouter un point à une géométrie.
     */
    public void addPoint(DirectPosition p) {
        GM_Point point = new GM_Point(p);
        double minDistance = Double.POSITIVE_INFINITY;
        FT_Feature closestFeature = null;
        for (FT_Feature feature : getFrame().getLayerViewPanel()
                .getSelectedFeatures()) {
            double distance = feature.getGeom().distance(point);
            if (distance < minDistance) {
                minDistance = distance;
                closestFeature = feature;
            }
        }
        if (closestFeature == null) { return; }
        addPoint(p, closestFeature);
    }

    /**
     * @param point
     * @param feature
     */
    @SuppressWarnings("unchecked")
    private void addPoint(DirectPosition point, FT_Feature feature) {
        System.out.println("addPoint geom = "+feature.getGeom());
        if (feature.getGeom().isPolygon()) {
            if (!feature.getGeom().coord().contains(point)) {
                GM_Polygon polygon = new GM_Polygon(
                        (GM_Polygon) feature.getGeom());
                addPoint(point, polygon);
                feature.setGeom(polygon);
            }
        } else {
            if (feature.getGeom().isLineString()) {
                if (!feature.getGeom().coord().contains(point)) {
                    GM_LineString line = new GM_LineString(
                            feature.getGeom().coord());
                    addPoint(point, line);
                    feature.setGeom(line);
                }
            } else {
                if (feature.getGeom().isMultiSurface()) {
                    if (!feature.getGeom().coord().contains(point)) {
                        GM_MultiSurface<GM_Polygon> multiSurface
                        = new GM_MultiSurface<GM_Polygon>(
                                (GM_MultiSurface<GM_Polygon>) feature
                                        .getGeom());
                        addPoint(point, multiSurface);
                        feature.setGeom(multiSurface);
                    }
                } else {
                    if (feature.getGeom().isMultiCurve()) {
                        if (!feature.getGeom().coord().contains(point)) {
                            GM_MultiCurve<GM_LineString> multiCurve
                            = new GM_MultiCurve<GM_LineString>(
                                    (GM_MultiCurve<GM_LineString>) feature
                                            .getGeom());
                            addPoint(point, multiCurve);
                            feature.setGeom(multiCurve);
                        }
                    } else {
                    }
                }
            }
        }
    }

    /**
     * @param point
     * @param multiCurve
     */
    private void addPoint(DirectPosition point,
            GM_MultiCurve<GM_LineString> multiCurve) {
        int indexSurfaceMin = 0;
        GM_LineString surfaceMin = multiCurve.get(0);
        double distanceMin = Distances.distance(point, surfaceMin);
        for (int index = 1; index < multiCurve.size(); index++) {
            GM_LineString surface = multiCurve.get(index);
            double distance = Distances.distance(point, surface);
            if (distance < distanceMin) {
                distanceMin = distance;
                surfaceMin = surface;
                indexSurfaceMin = index;
            }
        }
        surfaceMin = new GM_LineString(surfaceMin.coord());
        addPoint(point, surfaceMin);
        multiCurve.set(indexSurfaceMin, surfaceMin);
    }

    /**
     * @param point
     * @param multiSurface
     */
    private void addPoint(DirectPosition point,
            GM_MultiSurface<GM_Polygon> multiSurface) {
        int indexSurfaceMin = 0;
        GM_Polygon surfaceMin = multiSurface.get(0);
        double distanceMin = Distances.distance(point, surfaceMin);
        for (int index = 1; index < multiSurface.size(); index++) {
            GM_Polygon surface = multiSurface.get(index);
            double distance = Distances.distance(point, surface);
            if (distance < distanceMin) {
                distanceMin = distance;
                surfaceMin = surface;
                indexSurfaceMin = index;
            }
        }
        surfaceMin = new GM_Polygon(surfaceMin);
        addPoint(point, surfaceMin);
        multiSurface.set(indexSurfaceMin, surfaceMin);
    }

    /**
     * @param point
     * @param polygon
     */
    private void addPoint(DirectPosition point, GM_Polygon polygon) {
        int indexRingMin = -1;
        GM_Ring ringMin = polygon.getExterior();
        double distanceMin = Distances.distance(point, ringMin);
        for (int index = 0; index < polygon.sizeInterior(); index++) {
            GM_Ring interiorRing = polygon.getInterior().get(index);
            double distance = Distances.distance(point, interiorRing);
            if (distance < distanceMin) {
                distanceMin = distance;
                ringMin = interiorRing;
                indexRingMin = index;
            }
        }
        DirectPositionList points = ringMin.coord();
        Operateurs.projectAndInsert(point, points);
        if (indexRingMin == -1) {
            polygon.setExterior(new GM_Ring(new GM_CompositeCurve(
                    new GM_LineString(points))));
        } else {
            polygon.setInterior(indexRingMin, new GM_Ring(
                    new GM_CompositeCurve(new GM_LineString(points))));
        }
    }
    
    /**
     * @param point
     * @param line
     */
    private void addPoint(DirectPosition point, GM_LineString line) {
        Operateurs.projectAndInsert(point, line.getControlPoint());
    }

    /**
     * Permet de supprimer un point à une géométrie.
     * @param p position
     */
    public void removePoint(DirectPosition p) {
        GM_Point point = new GM_Point(p);
        double minDistance = Double.POSITIVE_INFINITY;
        FT_Feature closestFeature = null;
        for (FT_Feature feature : getFrame().getLayerViewPanel()
                .getSelectedFeatures()) {
            double distance = feature.getGeom().distance(point);
            if (distance < minDistance) {
                minDistance = distance;
                closestFeature = feature;
            }
        }
        if (closestFeature == null) { return; }
        removePoint(p, closestFeature);
    }

    /**
     * @param point
     * @param feature
     */
    @SuppressWarnings("unchecked")
    private void removePoint(DirectPosition point, FT_Feature feature) {
        System.out.println("removePoint geom = " + feature.getGeom());
        if (feature.getGeom().isPolygon()) {
            GM_Polygon polygon = new GM_Polygon(
                    (GM_Polygon) feature.getGeom());
            removePoint(point, polygon);
            feature.setGeom(polygon);
        } else {
            if (feature.getGeom().isLineString()) {
                if (!feature.getGeom().coord().contains(point)) {
                    GM_LineString line = new GM_LineString(
                            feature.getGeom().coord());
                    removePoint(point, line);
                    feature.setGeom(line);
                }
            } else {
                if (feature.getGeom().isMultiSurface()) {
                    if (!feature.getGeom().coord().contains(point)) {
                        GM_MultiSurface<GM_Polygon> multiSurface
                        = new GM_MultiSurface<GM_Polygon>(
                                (GM_MultiSurface<GM_Polygon>) feature
                                        .getGeom());
                        removePoint(point, multiSurface);
                        feature.setGeom(multiSurface);
                    }
                } else {
                    if (feature.getGeom().isMultiCurve()) {
                        if (!feature.getGeom().coord().contains(point)) {
                            GM_MultiCurve<GM_LineString> multiCurve
                            = new GM_MultiCurve<GM_LineString>(
                                    (GM_MultiCurve<GM_LineString>) feature
                                            .getGeom());
                            removePoint(point, multiCurve);
                            feature.setGeom(multiCurve);
                        }
                    } else {
                    }
                }
            }
        }
    }

    /**
     * @param point
     * @param multiCurve
     */
    private void removePoint(DirectPosition point,
            GM_MultiCurve<GM_LineString> multiCurve) {
        int indexSurfaceMin = 0;
        GM_LineString lineMin = multiCurve.get(0);
        double distanceMin = Distances.distance(point, lineMin);
        for (int index = 1; index < multiCurve.size(); index++) {
            GM_LineString surface = multiCurve.get(index);
            double distance = Distances.distance(point, surface);
            if (distance < distanceMin) {
                distanceMin = distance;
                lineMin = surface;
                indexSurfaceMin = index;
            }
        }
        lineMin = new GM_LineString(lineMin.coord());
        removePoint(point, lineMin);
        multiCurve.set(indexSurfaceMin, lineMin);
    }

    private void removePoint(DirectPosition point, GM_LineString line) {
        if (line.sizeControlPoint() > 2) {
            removePoint(point, line.getControlPoint());
        } else {
            // LOG THE REASON WHY WE'RE NOT DOING ANYTHING
        }
    }

    /**
     * @param point
     * @param multiSurface
     */
    private void removePoint(DirectPosition point,
            GM_MultiSurface<GM_Polygon> multiSurface) {
        int indexSurfaceMin = 0;
        GM_Polygon surfaceMin = multiSurface.get(0);
        double distanceMin = Distances.distance(point, surfaceMin);
        for (int index = 1; index < multiSurface.size(); index++) {
            GM_Polygon surface = multiSurface.get(index);
            double distance = Distances.distance(point, surface);
            if (distance < distanceMin) {
                distanceMin = distance;
                surfaceMin = surface;
                indexSurfaceMin = index;
            }
        }
        surfaceMin = new GM_Polygon(surfaceMin);
        removePoint(point, surfaceMin);
        multiSurface.set(indexSurfaceMin, surfaceMin);
    }

    /**
     * @param point
     * @param polygon
     */
    private void removePoint(DirectPosition point, GM_Polygon polygon) {
        if (polygon.numPoints() < 5) {
            // TODO LOG WHY WE'RE NOT DOING ANYTHING
            return;
        }
        int indexRingMin = -1;
        GM_Ring ringMin = polygon.getExterior();
        double distanceMin = Distances.distance(point, ringMin);
        for (int index = 0; index < polygon.sizeInterior(); index++) {
            GM_Ring interiorRing = polygon.getInterior().get(index);
            double distance = Distances.distance(point, interiorRing);
            if (distance < distanceMin) {
                distanceMin = distance;
                ringMin = interiorRing;
                indexRingMin = index;
            }
        }
        DirectPositionList points = ringMin.coord();
        int closestPointIndex = closestPointIndex(point, points);
        points.remove(closestPointIndex);
        if (closestPointIndex == 0) {
            // the closest point is the first and therefore also the last
            // we remove it
            points.remove(points.size()-1);
            // and we also have to copy the new first element at the end
            points.add(points.size(), points.get(0));
        }
        if (indexRingMin == -1) {
            polygon.setExterior(new GM_Ring(new GM_CompositeCurve(
                    new GM_LineString(points))));
        } else {
            polygon.setInterior(indexRingMin, new GM_Ring(
                    new GM_CompositeCurve(new GM_LineString(points))));
        }
    }
    /**
     * @param point
     * @param points
     */
    private void removePoint(DirectPosition point, DirectPositionList points) {
        points.remove(closestPointIndex(point, points));
    }

    /**
     * @param point
     * @param points
     */
    private int closestPointIndex(DirectPosition point, DirectPositionList points) {
        int indexPointMin = 0;
        double distanceMin = point.distance(points.get(0));
        for (int index = 1; index < points.size(); index++) {
            DirectPosition currentPoint = points.get(index);
            double distance = point.distance( currentPoint);
            if (distance < distanceMin) {
                distanceMin = distance;
                indexPointMin = index;
            }
        }
        return indexPointMin;
    }

    /*
    @SuppressWarnings("unchecked")
    public void supprimerPointExteriorRingPolygon() {
        GM_Ring exteriorRing = ((GM_Polygon) objetLePlusProche).getExterior();
        List<GM_Ring> interiorRings = ((GM_Polygon) objetLePlusProche)
                .getInterior();
        DirectPositionList points = exteriorRing.coord();
        GM_LineString exteriorLine = ((GM_Polygon) objetLePlusProche)
                .exteriorLineString();
        if (sommetLePlusProche.equals(exteriorLine.startPoint())
                || sommetLePlusProche.equals(exteriorLine.endPoint())) {
            points.remove(0);
            points.remove(points.size() - 1);
            points.add(points.get(0));
        } else {
            points.remove(sommetLePlusProche);
        }
        if (points.size() > 3) {
            GM_CurveSegment curveSegment = new GM_LineString(points);
            GM_CompositeCurve curve = new GM_CompositeCurve(curveSegment);
            GM_Ring newExteriorRing = new GM_Ring(curve);

            GM_Polygon newPoly = new GM_Polygon();
            newPoly.setExterior(newExteriorRing);
            for (GM_Ring ring : interiorRings) {
                newPoly.addInterior(ring);
            }
            if (newPoly.isValid() || autoriserGeomNonValide()) {

                List<FT_Feature> features = new ArrayList<FT_Feature>(
                        getFrame().getLayerViewPanel().getSelectedFeatures());
                if (isAggregate) {
                    GM_Aggregate<GM_Polygon> geom = (GM_Aggregate<GM_Polygon>) features
                            .get(indiceFeaturePlusProche).getGeom();
                    geom.set(indiceObjetPlusProche, newPoly);
                } else {
                    features.get(indiceFeaturePlusProche).setGeom(newPoly);
                }
                getFrame().getLayerViewPanel().repaint();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void supprimerPointInteriorRingPolygon() {
        GM_Ring exteriorRing = ((GM_Polygon) objetLePlusProche).getExterior();
        List<GM_Ring> interiorRings = ((GM_Polygon) objetLePlusProche)
                .getInterior();
        GM_Ring interiorRing = ((GM_Polygon) objetLePlusProche)
                .getInterior(indiceInteriorRingLePlusProche);
        DirectPositionList points = interiorRing.coord();
        GM_LineString interiorLine = ((GM_Polygon) objetLePlusProche)
                .interiorLineString(indiceInteriorRingLePlusProche);
        if (sommetLePlusProche.equals(interiorLine.startPoint())
                || sommetLePlusProche.equals(interiorLine.endPoint())) {
            points.remove(0);
            points.remove(points.size() - 1);
            points.add(points.get(0));
        } else {
            points.remove(sommetLePlusProche);
        }
        if (points.size() > 3) {
            GM_CurveSegment curveSegment = new GM_LineString(points);
            GM_CompositeCurve curve = new GM_CompositeCurve(curveSegment);
            GM_Ring newInteriorRing = new GM_Ring(curve);
            if (newInteriorRing.isValid() || autoriserGeomNonValide()) {
                interiorRings.set(indiceInteriorRingLePlusProche,
                        newInteriorRing);
                GM_Polygon newPoly = new GM_Polygon();
                newPoly.setExterior(exteriorRing);
                for (GM_Ring ring : interiorRings) {
                    newPoly.addInterior(ring);
                }
                List<FT_Feature> features = new ArrayList<FT_Feature>(
                        getFrame().getLayerViewPanel().getSelectedFeatures());
                if (isAggregate) {
                    GM_Aggregate<GM_Polygon> geom = (GM_Aggregate<GM_Polygon>) features
                            .get(indiceFeaturePlusProche).getGeom();
                    geom.set(indiceObjetPlusProche, newPoly);
                } else {
                    features.get(indiceFeaturePlusProche).setGeom(newPoly);
                }
                getFrame().repaint();
            }
        } else {
            interiorRings.remove(interiorRing);
            GM_Polygon newPoly = new GM_Polygon();
            newPoly.setExterior(exteriorRing);
            for (GM_Ring ring : interiorRings) {
                newPoly.addInterior(ring);
            }
            List<FT_Feature> features = new ArrayList<FT_Feature>(getFrame()
                    .getLayerViewPanel().getSelectedFeatures());
            if (isAggregate) {
                GM_Aggregate<GM_Polygon> geom = (GM_Aggregate<GM_Polygon>) features
                        .get(indiceFeaturePlusProche).getGeom();
                geom.set(indiceObjetPlusProche, newPoly);
            } else {
                features.get(indiceFeaturePlusProche).setGeom(newPoly);
            }
            frame.getLayerViewPanel().repaint();
        }
    }

    @SuppressWarnings("unchecked")
    public void supprimerPointLineString() {
        GM_LineString line = (GM_LineString) objetLePlusProche;
        DirectPositionList points = line.coord();
        if (points.size() > 2) {
            points.remove(sommetLePlusProche);
            GM_LineString newLine = new GM_LineString(points);
            List<FT_Feature> features = new ArrayList<FT_Feature>(getFrame()
                    .getLayerViewPanel().getSelectedFeatures());
            if (isAggregate) {
                GM_Aggregate<GM_LineString> geom = (GM_Aggregate<GM_LineString>) features
                        .get(indiceFeaturePlusProche).getGeom();
                geom.set(indiceObjetPlusProche, newLine);
            } else {
                features.get(indiceFeaturePlusProche).setGeom(newLine);
            }
            getFrame().getLayerViewPanel().repaint();
        }
    }
    */

    /**
     * Permet de déplacer un point aux nouvelles coordonnées envoyées en
     * paramètre
     * @param x
     *            La nouvelle coordonnée en x
     * @param y
     *            La nouvelle coordonnée en y
     */
    /*
    public void deplacerPoint(double x, double y) {
        if (objetLePlusProche == null || pointLePlusProche == null
                || sommetLePlusProche == null) return;
        if (objetLePlusProche.isPolygon()) deplacerPointPolygon(x, y);
        else if (objetLePlusProche.isLineString()) {
            deplacerPointLineString(x, y);
        }
    }

    public void deplacerPointPolygon(double x, double y) {
        if (isInterior) {
            deplacerPointInteriorRingPolygon(x, y);
        } else {
            deplacerPointExteriorRingPolygon(x, y);
        }
    }

    @SuppressWarnings("unchecked")
    public void deplacerPointExteriorRingPolygon(double x, double y) {
        DirectPosition oldPoint = sommetLePlusProche;
        DirectPosition newPoint = new DirectPosition(x, y);
        List<GM_Ring> interiorRings = ((GM_Polygon) objetLePlusProche)
                .getInterior();
        GM_Ring exteriorRing = ((GM_Polygon) objetLePlusProche).getExterior();
        DirectPositionList points = exteriorRing.coord();
        GM_LineString exteriorLine = ((GM_Polygon) objetLePlusProche)
                .exteriorLineString();
        if (oldPoint.equals(exteriorLine.startPoint())
                || oldPoint.equals(exteriorLine.endPoint())) {
            points.set(0, newPoint);
            points.set(points.size() - 1, newPoint);
        } else {
            points.set(indiceSommetPlusProche, newPoint);
        }
        GM_CurveSegment curveSegment = new GM_LineString(points);
        GM_CompositeCurve curve = new GM_CompositeCurve(curveSegment);
        GM_Ring newExteriorRing = new GM_Ring(curve);
        GM_Polygon newPoly = new GM_Polygon();
        newPoly.setExterior(newExteriorRing);
        for (GM_Ring ring : interiorRings) {
            newPoly.addInterior(ring);
        }
        if (newPoly.isValid() || autoriserGeomNonValide()) {
            if (isAggregate) {
                boolean isValid = true;
                GM_Aggregate<GM_Polygon> geom = (GM_Aggregate<GM_Polygon>) aggregateLePlusProche;
                for (GM_Polygon poly : geom) {
                    if (!poly.equals(objetLePlusProche)) {
                        if (poly.intersects(newPoly)) {
                            isValid = false;
                        }
                    }
                    if (!isValid) break;
                }
                if (isValid || autoriserGeomNonValide()) {
                    geom.set(indiceObjetPlusProche, newPoly);
                }
            } else {
                List<FT_Feature> features = new ArrayList<FT_Feature>(
                        getFrame().getLayerViewPanel().getSelectedFeatures());
                features.get(indiceFeaturePlusProche).setGeom(newPoly);
            }
            getFrame().getLayerViewPanel().repaint();
        }
    }

    @SuppressWarnings("unchecked")
    public void deplacerPointInteriorRingPolygon(double x, double y) {
        DirectPosition oldPoint = sommetLePlusProche;
        DirectPosition newPoint = new DirectPosition(x, y);
        List<GM_Ring> interiorRings = new ArrayList<GM_Ring>();
        interiorRings.addAll(((GM_Polygon) objetLePlusProche).getInterior());
        GM_Ring interiorRing = interiorRingLePlusProche;
        GM_Ring exteriorRing = ((GM_Polygon) objetLePlusProche).getExterior();
        DirectPositionList points = interiorRing.coord();
        GM_LineString interiorLine = ((GM_Polygon) objetLePlusProche)
                .interiorLineString(indiceInteriorRingLePlusProche);
        if (oldPoint.equals(interiorLine.startPoint())
                || oldPoint.equals(interiorLine.endPoint())) {
            points.set(0, newPoint);
            points.set(points.size() - 1, newPoint);
        } else {
            points.set(indiceSommetPlusProche, newPoint);
        }
        GM_CurveSegment curveSegment = new GM_LineString(points);
        GM_CompositeCurve curve = new GM_CompositeCurve(curveSegment);
        GM_Ring newInteriorRing = new GM_Ring(curve);
        interiorRings.set(indiceInteriorRingLePlusProche, newInteriorRing);
        GM_Polygon newPoly = new GM_Polygon();
        newPoly.setExterior(exteriorRing);
        for (GM_Ring ring : interiorRings) {
            newPoly.addInterior(ring);
        }
        boolean ringIntersection = false;
        for (GM_Ring ring : newPoly.getInterior()) {
            if (!ring.equals(newInteriorRing)) {
                if (newInteriorRing.intersects(ring)) {
                    ringIntersection = true;
                    break;
                }
            }
        }
        if ((newPoly.isValid() && !ringIntersection)
                || autoriserGeomNonValide()) {
            if (isAggregate) {
                boolean isValid = true;
                List<FT_Feature> features = new ArrayList<FT_Feature>(
                        getFrame().getLayerViewPanel().getSelectedFeatures());
                GM_Aggregate<GM_Polygon> geom = (GM_Aggregate<GM_Polygon>) features
                        .get(indiceFeaturePlusProche).getGeom();
                for (GM_Polygon poly : geom) {
                    if (!poly.equals(objetLePlusProche)) {
                        if (poly.intersects(newPoly)) {
                            isValid = false;
                        }
                    }
                    if (!isValid) break;
                }
                if (isValid || autoriserGeomNonValide()) {
                    geom.set(indiceObjetPlusProche, newPoly);
                }
            } else {
                List<FT_Feature> features = new ArrayList<FT_Feature>(
                        getFrame().getLayerViewPanel().getSelectedFeatures());
                features.get(indiceFeaturePlusProche).setGeom(newPoly);
            }
            getFrame().getLayerViewPanel().repaint();
        }
    }

    @SuppressWarnings("unchecked")
    public void deplacerPointLineString(double x, double y) {
        DirectPosition newPoint = new DirectPosition(x, y);
        GM_LineString line = (GM_LineString) objetLePlusProche;
        DirectPositionList points = line.coord();
        points.set(indiceSommetPlusProche, newPoint);
        GM_LineString newLine = new GM_LineString(points);
        if (isAggregate) {
            boolean isValid = true;
            List<FT_Feature> features = new ArrayList<FT_Feature>(getFrame()
                    .getLayerViewPanel().getSelectedFeatures());
            GM_Aggregate<GM_LineString> geom = (GM_Aggregate<GM_LineString>) features
                    .get(indiceFeaturePlusProche).getGeom();
            for (GM_LineString segment : geom) {
                if (!segment.equals(objetLePlusProche)) {
                    if (segment.intersects(newLine)) {
                        isValid = false;
                    }
                }
                if (!isValid) break;
            }
            if (isValid || autoriserGeomNonValide()) {
                geom.set(indiceObjetPlusProche, newLine);
            }
        } else {
            List<FT_Feature> features = new ArrayList<FT_Feature>(getFrame()
                    .getLayerViewPanel().getSelectedFeatures());
            features.get(indiceFeaturePlusProche).setGeom(newLine);
        }
        getFrame().getLayerViewPanel().repaint();
    }
    */



    /**
     * Permet de créer un polygone.
     * @param points
     *            liste de points
     */
    public void createPolygon(DirectPositionList points) {
        points.add(points.get(0));
        GM_CurveSegment curveSegment = new GM_LineString(points);
        GM_CompositeCurve curve = new GM_CompositeCurve(curveSegment);
        GM_Ring newExteriorRing = new GM_Ring(curve);
        GM_Polygon newPoly = new GM_Polygon();
        newPoly.setExterior(newExteriorRing);
        if (newPoly.isValid() || autoriserGeomNonValide()) {
            System.out.println("test");
            if (cBoxCoucheAModifier.getSelectedIndex() != 0) {
                Layer layer = frame.getLayer(layerName);
                if (canAddObjectToLayer(newPoly, layer)) {
                    createPolygonExistingLayer(layer, newPoly);
                } else {
                    String message = "Attention! Vous essayez d'ajouter un polygone dans une couche qui ne contient que des objets de même type. "
                            + "\nVoulez-vous malgré tout insérer la nouvelle géométrie ?";

                    int reponse = JOptionPane.showConfirmDialog(this, message,
                            "Confirmer la création de la géométrie ?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    // Si on clique sur oui
                    if (reponse == JOptionPane.YES_OPTION) {
                        getFrame().getSld().addSymbolizer(layer.getName(),
                                GM_Polygon.class);
                        createPolygonExistingLayer(layer, newPoly);
                    } else {
                        // TODO
                        // getFrame().getPanelVisu().stopEditing();
                    }
                }
            } else {
                createPolygonNewLayer(newPoly);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void createPolygonExistingLayer(Layer layer, GM_Polygon newPoly) {
        FT_FeatureCollection<FT_Feature> features = (FT_FeatureCollection<FT_Feature>) layer
                .getFeatureCollection();
        if (features.getFeatureType().getGeometryType() == GM_Polygon.class) {
            FT_Feature newFeature = new DefaultFeature(newPoly);
            FeatureType featureType = features.getFeatureType();
            newFeature.setFeatureType(featureType);
            features.add(newFeature);
            getFrame().getLayerViewPanel().repaint();
        } else {
            GM_MultiSurface<GM_Polygon> newSurface = new GM_MultiSurface<GM_Polygon>();
            newSurface.add(newPoly);
            FT_Feature newFeature = new DefaultFeature(newSurface);
            /**
             * TODO A vérifier..;
             */
            newFeature.setFeatureType(features.getFeatureType());
            features.add(newFeature);
            getFrame().getLayerViewPanel().repaint();
        }
    }

    @SuppressWarnings("unchecked")
    public void createPolygonNewLayer(GM_Polygon newPoly) {
        Layer newLayer = getFrame().createLayer(newLayerNameText.getText(),
                GM_MultiSurface.class);
        cBoxCoucheAModifier
                .setSelectedIndex(cBoxCoucheAModifier.getItemCount() - 1);
        if (newLayer != null) {
            String newLayerName = newLayer.getName();
            GM_MultiSurface<GM_Polygon> newSurface = new GM_MultiSurface<GM_Polygon>();
            newSurface.add(newPoly);
            DefaultFeature newFeature = new DefaultFeature(newSurface);
            // Remplie la population
            FT_FeatureCollection<DefaultFeature> features = (FT_FeatureCollection<DefaultFeature>) getFrame()
                    .getLayer(newLayerName).getFeatureCollection();
            features.add(newFeature);
            getFrame().getLayerViewPanel().repaint();
        }
    }

    /**
     * @param points
     */
    public void createLineString(DirectPositionList points) {
        GM_LineString newLine = new GM_LineString(points);
        if (cBoxCoucheAModifier.getSelectedIndex() != 0) {
            Layer layer = frame.getLayer(layerName);
            if (canAddObjectToLayer(newLine, layer)) {
                creerLineStringCoucheExistante(layer, newLine);
            } else {
                String message = "Attention! Vous essayez d'ajouter une ligne dans une couche qui ne contient que des objets de même type. "
                        + "\nVoulez-vous malgré tout insérer la nouvelle géométrie ?";
                int reponse = JOptionPane.showConfirmDialog(this, message,
                        "Confirmer la création de la géométrie ?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                // Si on clique sur oui
                if (reponse == JOptionPane.YES_OPTION) {
                    getFrame().getSld().addSymbolizer(layer.getName(),
                            GM_LineString.class);
                    creerLineStringCoucheExistante(layer, newLine);
                } else {
                    // TODO
                    // getFrame().getPanelVisu().stopEditing();
                }
            }
        } else {
            creerLineStringNouvelleCouche(newLine);
        }
    }

    @SuppressWarnings("unchecked")
    public void creerLineStringCoucheExistante(Layer layer,
            GM_LineString newLine) {
        FT_FeatureCollection<FT_Feature> features = (FT_FeatureCollection<FT_Feature>) layer
                .getFeatureCollection();
        if (features.getFeatureType().getGeometryType() == GM_LineString.class) {
            FT_Feature newFeature = new DefaultFeature(newLine);
            if (newLine.isValid()) {
                features.add(newFeature);
                getFrame().getLayerViewPanel().repaint();
            }
        } else {
            GM_MultiCurve<GM_LineString> newMultiCurve = new GM_MultiCurve<GM_LineString>();
            newMultiCurve.add(newLine);
            FT_Feature newFeature = new DefaultFeature(newMultiCurve);
            if (newLine.isValid()) {
                features.add(newFeature);
                getFrame().getLayerViewPanel().repaint();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void creerLineStringNouvelleCouche(GM_LineString newLine) {
        if (newLine.isValid()) {
            Layer newLayer = getFrame().createLayer(newLayerNameText.getText(),
                    GM_MultiCurve.class);
            cBoxCoucheAModifier.setSelectedIndex(cBoxCoucheAModifier
                    .getItemCount() - 1);
            if (newLayer != null) {
                String newLayerName = newLayer.getName();
                GM_MultiCurve<GM_LineString> newCurve = new GM_MultiCurve<GM_LineString>();
                newCurve.add(newLine);
                DefaultFeature newFeature = new DefaultFeature(newCurve);
                // Remplie la population
                FT_FeatureCollection<DefaultFeature> features = (FT_FeatureCollection<DefaultFeature>) getFrame()
                        .getLayer(newLayerName).getFeatureCollection();
                features.add(newFeature);
                getFrame().getLayerViewPanel().repaint();
            }
        }
    }

    public void creerPoint(DirectPosition point) {
        GM_Point newPoint = new GM_Point(point);
        if (cBoxCoucheAModifier.getSelectedIndex() != 0) {
            Layer layer = frame.getLayer(layerName);
            if (canAddObjectToLayer(newPoint, layer)) {
                creerPointCoucheExistante(layer, newPoint);
            } else {
                String message = "Attention! Vous essayez d'ajouter un point dans une couche qui ne contient que des objets de même type. "
                        + "\nVoulez-vous malgré tout insérer la nouvelle géométrie ?";
                int reponse = JOptionPane.showConfirmDialog(this, message,
                        "Confirmer la création de la géométrie ?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                // Si on clique sur oui
                if (reponse == JOptionPane.YES_OPTION) {
                    getFrame().getSld().addSymbolizer(layer.getName(),
                            GM_Point.class);
                    creerPointCoucheExistante(layer, newPoint);
                } else {
                    // TODO
                    // getFrame().getPanelVisu().stopEditing();
                }
            }
        } else {
            creerPointNouvelleCouche(newPoint);
        }
    }

    @SuppressWarnings("unchecked")
    public void creerPointCoucheExistante(Layer layer, GM_Point newPoint) {
        FT_FeatureCollection<FT_Feature> features = (FT_FeatureCollection<FT_Feature>) layer
                .getFeatureCollection();
        if (features.getFeatureType().getGeometryType() == GM_Point.class) {
            FT_Feature newFeature = new DefaultFeature(newPoint);

            if (newPoint.isValid()) {
                features.add(newFeature);
                getFrame().getLayerViewPanel().repaint();
            }
        } else {
            GM_MultiPoint newMultiPoint = new GM_MultiPoint();
            newMultiPoint.add(newPoint);
            FT_Feature newFeature = new DefaultFeature(newMultiPoint);

            if (newPoint.isValid()) {
                features.add(newFeature);
                getFrame().getLayerViewPanel().repaint();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void creerPointNouvelleCouche(GM_Point newPoint) {
        if (newPoint.isValid()) {
            Layer newLayer = getFrame().createLayer(newLayerNameText.getText(),
                    GM_MultiPoint.class);
            cBoxCoucheAModifier.setSelectedIndex(cBoxCoucheAModifier
                    .getItemCount() - 1);
            if (newLayer != null) {
                String newLayerName = newLayer.getName();
                GM_MultiPoint newMultiPoint = new GM_MultiPoint();
                newMultiPoint.add(newPoint);
                DefaultFeature newFeature = new DefaultFeature(newMultiPoint);

                // Remplie la population
                FT_FeatureCollection<DefaultFeature> features = (FT_FeatureCollection<DefaultFeature>) getFrame()
                        .getLayer(newLayerName).getFeatureCollection();
                features.add(newFeature);
                getFrame().repaint();
            }
        }
    }

    /**
     * Cette méthode créer un interior ring à partir de la liste de points
     * envoyée en paramètre, puis l'ajoute au polygon de la couche sélectionnée
     * @param points
     *            La liste de point qui forment l'anneau intérieur
     */
    @SuppressWarnings("unchecked")
    public void creerInteriorRing(DirectPositionList points) {
        if (cBoxCoucheAModifier.getSelectedIndex() != 0) {
            boolean isInside = false;
            boolean isAggregate = false;
            GM_CurveSegment curveSegment = new GM_LineString(points);
            GM_CompositeCurve curve = new GM_CompositeCurve(curveSegment);
            GM_Ring newInteriorRing = new GM_Ring(curve);
            Layer layer = frame.getLayer(layerName);
            FT_FeatureCollection<FT_Feature> features = (FT_FeatureCollection<FT_Feature>) layer
                    .getFeatureCollection();
            int i = 0;
            int j = 0;
            int indiceFeature = 0;
            int indiceObjet = 0;
            GM_Object objet = null;
            while (i < features.size() && !isInside) {
                GM_Object geom = features.get(i).getGeom();
                if (geom.isMultiSurface()) {
                    GM_MultiSurface surface = (GM_MultiSurface) geom;
                    j = 0;
                    while (j < surface.size() && !isInside) {
                        GM_Object obj = surface.get(j);
                        if (newInteriorRing.within(obj)) {
                            isInside = true;
                            isAggregate = true;
                            indiceFeature = i;
                            indiceObjet = j;
                            objet = obj;
                        } else j++;
                    }
                } else if (geom.isPolygon()) {
                    if (newInteriorRing.within(geom)) {
                        isInside = true;
                        isAggregate = false;
                        indiceFeature = i;
                        objet = geom;
                    }
                }
                i++;
            }

            if (objet != null && objet.isPolygon()) {
                GM_Polygon poly = (GM_Polygon) objet;
                GM_Ring exteriorRing = poly.getExterior();
                List<GM_Ring> interiorRings = new ArrayList<GM_Ring>();
                interiorRings.addAll(poly.getInterior());
                interiorRings.add(newInteriorRing);
                GM_Polygon newPoly = new GM_Polygon();
                newPoly.setExterior(exteriorRing);
                for (GM_Ring ring : interiorRings) {
                    newPoly.addInterior(ring);
                }
                if (newPoly.isValid() || autoriserGeomNonValide()) {
                    if (isAggregate) {
                        GM_Aggregate<GM_Polygon> geom = (GM_Aggregate<GM_Polygon>) features
                                .get(indiceFeature).getGeom();
                        geom.set(indiceObjet, newPoly);
                        features.get(indiceFeature).setGeom(geom);
                        getFrame().getLayerViewPanel().repaint();
                    } else {
                        features.get(indiceFeature).setGeom(newPoly);
                        getFrame().getLayerViewPanel().repaint();
                    }
                }
            }
        }
    }

    public void supprimerObjetsSelectionnes() {
        for (FT_Feature ft : getFrame().getLayerViewPanel()
                .getSelectedFeatures()) {
            supprimerObjet(ft);
        }
    }

    public void supprimerObjet(FT_Feature ft) {
        Layer layer = getFrame().getLayerFromFeature(ft);
        layer.getFeatureCollection().remove(ft);
    }

    /**
     * Permet de tester si on peut ajouter le nouvel objet dans la couche
     * Cette méthode vérifie que les types de géométrie correspondent
     * @param obj
     *            l'objet que l'on souhaite insérer
     * @param layer
     *            le layer dans lequel on souhaite insérer obj
     * @return true si l'objet peut être ajouté au layer
     */
    @SuppressWarnings("unchecked")
    public boolean canAddObjectToLayer(GM_Object obj, Layer layer) {
        FT_FeatureCollection<FT_Feature> features = (FT_FeatureCollection<FT_Feature>) layer
                .getFeatureCollection();
        boolean canAdd = true;
        if (!features.isEmpty()) {
            if (!mixedGeomType(layer)) {
                GM_Object geom = features.get(0).getGeom();
                if ((geom instanceof GM_Polygon)
                        && (obj instanceof GM_MultiSurface)) {
                } else if ((geom instanceof GM_MultiSurface)
                        && (obj instanceof GM_Polygon)) {
                } else if ((geom instanceof GM_Point)
                        && (obj instanceof GM_MultiPoint)) {
                } else if ((geom instanceof GM_MultiPoint)
                        && (obj instanceof GM_Point)) {
                } else if ((geom instanceof GM_LineString)
                        && (obj instanceof GM_MultiCurve)) {
                } else if ((geom instanceof GM_MultiCurve)
                        && (obj instanceof GM_LineString)) {
                } else {
                    canAdd = false;
                }
            }
        }
        return canAdd;
    }

    public void deplacerObjetsSelectionnes(double tx, double ty) {
        Set<FT_Feature> features = getFrame().getLayerViewPanel()
                .getSelectedFeatures();
        for (FT_Feature ft : features) {
            ft.setGeom(ft.getGeom().translate(tx, ty, 0));
        }
        getFrame().getLayerViewPanel().repaint();
    }

    /**
     * Cette méthode teste si le layer envoyé en paramètre est composé de
     * géométrie de types différents
     * @param layer
     *            le layer que l'ont veut tester
     * @return true si le layer contient des géométries de type différents
     */
    @SuppressWarnings("unchecked")
    public boolean mixedGeomType(Layer layer) {
        FT_FeatureCollection<FT_Feature> features = (FT_FeatureCollection<FT_Feature>) layer
                .getFeatureCollection();
        return mixedGeomType(features);
    }

    /**
     * Cette méthode teste si la feature collection envoyée en paramètre est
     * composé de géométrie de types différents
     * @param features
     *            les features que l'ont veut tester
     * @return true si le layer contient des géométries de type différents
     */
    public boolean mixedGeomType(FT_FeatureCollection<FT_Feature> features) {
        boolean mixedGeomType = false;
        int i = 0;
        GM_Object firstGeom = null;
        Class<? extends GM_Object> firstClass = null;
        while (i < features.size() - 1 && !mixedGeomType) {
            FT_Feature ft = features.get(i);
            if (i == 0) {
                if (ft.getGeom() != null) {
                    firstClass = ft.getGeom().getClass();
                    firstGeom = ft.getGeom();
                    i++;
                }
            } else {
                if (firstClass != ft.getGeom().getClass()) {
                    if ((firstGeom instanceof GM_Polygon)
                            && (ft.getGeom() instanceof GM_MultiSurface<?>)) {
                        i++;
                    } else if ((firstGeom instanceof GM_MultiSurface<?>)
                            && (ft.getGeom() instanceof GM_Polygon)) {
                        i++;
                    } else if ((firstGeom instanceof GM_Point)
                            && (ft.getGeom() instanceof GM_MultiPoint)) {
                        i++;
                    } else if ((firstGeom instanceof GM_MultiPoint)
                            && (ft.getGeom() instanceof GM_Point)) {
                        i++;
                    } else if ((firstGeom instanceof GM_LineString)
                            && (ft.getGeom() instanceof GM_MultiCurve<?>)) {
                        i++;
                    } else if ((firstGeom instanceof GM_MultiCurve<?>)
                            && (ft.getGeom() instanceof GM_LineString)) {
                        i++;
                    } else {
                        mixedGeomType = true;
                    }
                } else {
                    i++;
                }
            }
        }
        return mixedGeomType;
    }
}
