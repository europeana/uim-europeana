/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.uim.gui.cp.client.europeanawidgets;

import eu.europeana.uim.gui.cp.client.IngestionWidget;
import eu.europeana.uim.gui.cp.client.services.IntegrationSeviceProxyAsync;
import eu.europeana.uim.gui.cp.client.services.RepositoryServiceAsync;
import eu.europeana.uim.gui.cp.client.services.ResourceServiceAsync;

import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;




/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 13 Mar 2012
 */
public class ExecutionFlowManagementWidget extends IngestionWidget implements MouseUpHandler, MouseMoveHandler, MouseDownHandler {
//public class ExecutionFlowManagementWidget extends IngestionWidget  {
    private final RepositoryServiceAsync repositoryService;
    private final ResourceServiceAsync   resourceService;
	private final IntegrationSeviceProxyAsync integrationservice;
	
	 interface Binder extends UiBinder<Widget, ExecutionFlowManagementWidget> {
	    }

	/**
	 * @param repositoryService
	 * @param resourceService
	 * @param integrationservice
	 */
	public ExecutionFlowManagementWidget(
			RepositoryServiceAsync repositoryService,
			ResourceServiceAsync resourceService,
			IntegrationSeviceProxyAsync integrationservice) {
		super("Visual Manager", "This view allows you to select operations and visualize the whole ingestion process");
		this.resourceService = resourceService;
		this.repositoryService = repositoryService;
		this.integrationservice = integrationservice;
	}
	
	    @UiField(provided = true)
	    public HTML svgContainer;
	    
	    
	    private boolean dragging;
	    private float x0, y0;
	    private OMSVGPoint p;
	    private OMSVGSVGElement svg;
	    private OMSVGRectElement square;

	    
		@Override
		public Widget onInitialize() {
			// Cast the document into a SVG document
			

			svgContainer = new HTML();
            Element div = svgContainer.getElement();
            OMSVGDocument doc = OMSVGParser.currentDocument();

            // Create the root svg element
            svg = doc.createSVGSVGElement();
            svg.setViewBox(0f, 0f, 400f, 200f);

            // Create a circle
            final OMSVGCircleElement circle = doc.createSVGCircleElement(80f, 80f, 40f);
            circle.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
            setCircleColor(circle, SVGConstants.CSS_RED_VALUE);
            svg.appendChild(circle);
    
            // Set a mousedown event handler
            circle.addMouseDownHandler(new MouseDownHandler() {
                final String[] colors = new String[] {
                        SVGConstants.CSS_RED_VALUE,
                        SVGConstants.CSS_BLUE_VALUE,
                        SVGConstants.CSS_GREEN_VALUE,
                        SVGConstants.CSS_YELLOW_VALUE,
                        SVGConstants.CSS_PINK_VALUE };

                @Override
                public void onMouseDown(MouseDownEvent event) {
                    String color = getCircleColor(circle);
                    while (color.equals(getCircleColor(circle))) {
                        setCircleColor(circle, colors[Random.nextInt(colors.length)]);
                    }
                    event.stopPropagation();
                    event.preventDefault();
                }
            });
            
            // Create a square
            square = doc.createSVGRectElement(140f, 40f, 80f, 80f, 0f, 0f);
            square.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
            square.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_GREEN_VALUE);
            square.addMouseDownHandler(this);
            square.addMouseUpHandler(this);
            square.addMouseMoveHandler(this);
            svg.appendChild(square);
            
            // Insert the SVG root element into the HTML UI
            div.appendChild(svg.getElement());


			Binder binder = GWT.create(Binder.class);
            Widget widget =binder.createAndBindUi(this);
            
            return widget;
		}
	    

	    private static final String getCircleColor(OMSVGCircleElement circle) {
	        return circle.getStyle().getSVGProperty(SVGConstants.CSS_FILL_PROPERTY);
	    }
	    private static final void setCircleColor(OMSVGCircleElement circle, String color) {
	        circle.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
	    }
	    
	    @Override
	    protected void asyncOnInitialize(final AsyncCallback<Widget> callback) {
	        GWT.runAsync(ExecutionFlowManagementWidget.class, new RunAsyncCallback() {
	            @Override
	            public void onFailure(Throwable caught) {
	                callback.onFailure(caught);
	            }

	            @Override
	            public void onSuccess() {
	                callback.onSuccess(onInitialize());
	            }
	        });
	    }
	    


	    @Override
	    public void onMouseUp(MouseUpEvent event) {
	        dragging = false;
	        DOMHelper.releaseCaptureElement();
	        event.stopPropagation();
	        event.preventDefault();
	    }

	    @Override
	    public void onMouseMove(MouseMoveEvent event) {
	        if (dragging) {
	            OMSVGPoint d = getLocalCoordinates(event).substract(p);
	            square.getX().getBaseVal().setValue(x0 + d.getX());
	            square.getY().getBaseVal().setValue(y0 + d.getY());
	        }
	        event.stopPropagation();
	        event.preventDefault();
	    }
	    

	    public OMSVGPoint getLocalCoordinates(MouseEvent<? extends EventHandler> e) {
	        OMSVGPoint p = svg.createSVGPoint(e.getClientX(), e.getClientY());
	        OMSVGMatrix m = svg.getScreenCTM().inverse();
	        return p.matrixTransform(m);
	    }


	    @Override
	    public void onMouseDown(MouseDownEvent event) {
	        dragging = true;
	        p = getLocalCoordinates(event);
	        x0 = square.getX().getBaseVal().getValue();
	        y0 = square.getY().getBaseVal().getValue();
	        DOMHelper.setCaptureElement(square, null);
	        event.stopPropagation();
	        event.preventDefault();
	    }

		


	
}
