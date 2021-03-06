/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/
package fr.ign.cogit.geoxygene.appli.render.groups;

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;

public class RasterRenderingGroup  extends RenderingGroup {
    
    public static final String DEFAULT_RENDERING_METHOD_RASTER = "Raster";

    public RasterRenderingGroup(String _name, RenderingMethodDescriptor renderingmethod, Object style_element) {
        super(_name, renderingmethod, style_element);
    }

    @Override
    /**
     * Get or build the parameters for a raster
     */
    protected Map<String, Object> create() {
        Map<String, Object> params =  new HashMap<String, Object>();
        RasterSymbolizer rs = (RasterSymbolizer) this.style_element;
        params.put("Animate", rs.getAnimate());
        return params;
    }
    
    public static RenderingMethodDescriptor getRasterRenderingMethod(RasterSymbolizer r) {
        return RenderingMethodDescriptor.retrieveMethod(DEFAULT_RENDERING_METHOD_RASTER);
    }

}
